from pyspark import SparkContext
from utils import *

sc = SparkContext('local', 'test')
sc.setLogLevel("ERROR")

rdd=sc.textFile("data/split/aa")
rdd=clean(rdd)
result=topK(rdd,10)

sc.stop()

for (k,v) in result:
    print(str(k)+" "+str(v))

