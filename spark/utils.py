import time
import re
from haversine import haversine

## general

def debug(string):
    f = open("/tmp/debug", 'aw')
    f.write("\n ------------- \n")
    f.write(string)
    f.write("\n ------------- \n")
            
## Cell

class Cell():

    # shift (250m N., 250m W) from Barryville
    CORNER_LATITUDE=41.474937+0.003
    CORNER_LONGITUDE=-74.913585-0.02027
    CORNER=(CORNER_LATITUDE,CORNER_LONGITUDE)
    
    def __init__(self,latitude,longitude):
        self.x=int(haversine(self.CORNER,(self.CORNER_LATITUDE,longitude))/0.5)
        if longitude<self.CORNER_LONGITUDE:
            self.x=-self.x
        self.y=int(haversine(self.CORNER,(latitude,self.CORNER_LONGITUDE))/0.5)
        if latitude>self.CORNER_LATITUDE:
            self.y=-self.y
        
    def is_outlier(self):
        return self.x<0 or self.x>300 or self.y<0 or self.y>300

    def __str__(self):
        return "["+str(self.x)+"."+str(self.y)+"]"

def path(x):
    try:
        beg=Cell(float(x[0]),float(x[1]))
        end=Cell(float(x[2]),float(x[3]))
        if beg.is_outlier() or end.is_outlier():
            return None
        return str(beg)+" -> "+str(end)
    except ValueError:
        return None
    
## operators
    
def clean(rdd):
    return rdd\
      .map(lambda x: x.encode("ascii")\
      .split(",")[6:10])\
      .filter(lambda x : len(x)==4)\
      .map(lambda x: [x[1],x[0],x[3],x[2]])\
      .map(lambda x: (path(x),1))\
      .filter(lambda x : x[0]<>None)
        
def topK(rdd,n):
    return rdd\
      .reduceByKey(lambda a,b : a+b)\
      .map(lambda x: (x[1],x[0]))\
      .top(n)
        
## streaming operators

def sclean(dstream):
    return clean(dstream)

def update_count(new, last):
    if last==None:
        last=0
    return sum(new)+last    
    
def scount(dstream):
    return dstream\
      .reduceByKey(lambda a,b : a+b)\
      .updateStateByKey(update_count)

## Misra-Gries algorithm

class MisraGries():
    
    def __init__(self,sc,C):
        self.topN = {}
        self.C = C

    def __update__(self,time,rdd):
        dic = dict(rdd.collect())
        for k in dic:
            if not (k in self.topN):
                self.topN[k]=0
            self.topN[k]+=dic[k]
            if len(self.topN)>self.C:
                toRemove=[]
                for l in self.topN:
                    if l<>k:
                        if self.topN[l]==1:
                            toRemove.append(l)
                        else:                        
                            self.topN[l]-=1
                for l in toRemove:
                    self.topN.pop(l)
                
    def update(self,dstream):
        dstream\
          .reduceByKey(lambda a,b : a+b)\
          .foreachRDD(self.__update__)
