import time
import os
from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from utils import *

sc = SparkContext('local', 'test')
sc.setLogLevel("ERROR")
sc.setCheckpointDir("/tmp") # for stable state        
ssc = StreamingContext(sc, 0.01)

rddQ = []
for filename in os.listdir("data/split"):
    rddQ.append(sc.textFile("data/split/"+filename))
mg = MisraGries(sc,1000)
    
# processing
dstream = ssc.queueStream(rddQ)
dstream = sclean(dstream)
mg.update(dstream)

ssc.start()
ssc.awaitTerminationOrTimeout(30)
ssc.stop()

result = []
for k in mg.topN:
    result.append((k,mg.topN[k]))
result.sort(key=lambda tup: tup[1],reverse=True)
result=result[0:10]
for (k,v) in result:
    print(str(v)+" "+str(k))
              
