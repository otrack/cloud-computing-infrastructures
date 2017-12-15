# ./bin/pyspark --master local[2]

# http://www.debs2015.org/call-grand-challenge.html
# http://chriswhong.com/open-data/foil_nyc_taxi/
# http://spark.apache.org/docs/latest/api/python>/pyspark.html
# http://spark.apache.org/docs/latest/programming-guide.html
# https://developers.google.com/maps/documentation/javascript/examples/polyline-simple

sc = SparkContext('local', 'test')
ssc = StreamingContext(sc, 1)

lines=sc.textFile("../all.csv")
lines.map(lambda x: x.encode("ascii").split(",")[6:10]).filter(lambda x : float(x[0])!=0).map(lambda x: (x[0]+":"+x[1]+":"+x[2]+":"+x[3],1)).reduceByKey(lambda a,b : a+b).map(lambda x: (x[1],x[0])).top(10)

# https://spark.apache.org/docs/0.9.1/streaming-custom-receivers.html

nc -i 1 -lk 9999 < short.csv

# https://data.cityofnewyork.us/resource/gkne-dk5s.json?$offset=1000
# http://toddwschneider.com/posts/analyzing-1-1-billion-nyc-taxi-and-uber-trips-with-a-vengeance/

# CSC 4524, http://enseignements.telecom-sudparis.eu/fiche.php?c=CSC4524
# what is the amount of memory to use if we keep everything ?
# this boils down to computing the amount of GPS location in NYC.
# is this practical ?

# https://hal.archives-ouvertes.fr/hal-00998708/document
# Misra-Gries algorithm p5, Fig. 2
# http://www.cs.dartmouth.edu/~ac/Teach/CS49-Fall11/Notes/lecnotes.pdf
# Misra-Gries algorithm p8

ssc.stop()
sc.stop()
sc = SparkContext('local', 'test')

from pyspark.streaming import StreamingContext
def stepOne(new, last):
    if last==None:
        last=0
    else:
        last=last[0]
    return [sum(new)+last,1]


def stepTwo(new, last):
    if last==None:
        return new
    if last[1]==0:
        last[0]=last[0]-1
    return [last[0],0]


sc.setLogLevel("ERROR")
sc.setCheckpointDir("/tmp")
ssc = StreamingContext(sc, 1)
lines = ssc.socketTextStream("localhost", 9999)
running_counts = lines\
  .map(lambda x: x.encode("ascii").split(",")[6:10])\
  .filter(lambda x : float(x[0])!=0)\
  .map(lambda x: (x[0]+":"+x[1]+":"+x[2]+":"+x[3],1))\
  .reduceByKey(lambda a,b : a+b)\
  .updateStateByKey(stepOne)
#  .updateStateByKey(stepTwo)
  
running_counts.pprint(10000)

# .map(lambda x: (x[1],x[0]))

ssc.start()
ssc.awaitTermination()


http://blog.cloudera.com/blog/2014/08/building-lambda-architecture-with-spark-streaming/
