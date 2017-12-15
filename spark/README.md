# Traveling in the Big Apple

The Association for Computer Machinery holds annually the International Conference on Distributed Event-Based Systems (ACM DEBS).
This conference includes a Grand Challenge which aims at providing a common ground and evaluation criteria for both research and industrial event-based systems.
This practical is freely inspired from the [ACM DEBS 2015 Grand Challenge](http://www.debs2015.org/call-grand-challenge.html).

In this practical, we focus on the processing of a data stream originating from the New York City (NYC) Taxi and Limousine Commission.
This data is made available under the Freedom of Information Law and provides information regarding the pickups, drops-off, and payments made in the NYC medallion taxis.
The goal is to process this spatio-temporal data stream in order to find the most frequent routes.
We shall use [Apache Spark](http://spark.apache.org).

## 1. Data

We consider the first 20 days of year 2013.
This data set is available [here](https://drive.google.com/file/d/0B0TBL8JNn3JgTGNJTEJaQmFMbk0/view?usp=sharing).
It weights approximately 130MB.

This data set reports the NYC taxi trips.
It includes the starting and drop-off points, their corresponding timestamps, as well as some information related to the payment.
Data are reported at the end of the trip, that this in the order of their drop-off timestamps.
With more details, the attributes of each tuple in the stream are as follows:

* **medallion**	an md5sum of the identifier of the taxi - vehicle bound
* **hack_license**	an md5sum of the identifier for the taxi license
* **pickup_datetime**	time when the passenger(s) were picked up
* **dropoff_datetime**	time when the passenger(s) were dropped off
* **trip_time_in_secs**	duration of the trip
* **trip_distance	trip** distance in miles
* **pickup_longitude**	longitude coordinate of the pickup location
* **pickup_latitude**	latitude coordinate of the pickup location
* **dropoff_longitude**	longitude coordinate of the drop-off location
* **dropoff_latitude**	latitude coordinate of the drop-off location
* **payment_type**	the payment method - credit card or cash
* **fare_amount**	fare amount in dollars
* **surcharge**	surcharge in dollars
* **mta_tax**	tax in dollars
* **tip_amount**	tip in dollars
* **tolls_amount**	bridge and tunnel tolls in dollars
* **total_amount**	total paid amount in dollars

Next, you will find the first ten lines of the data file:

	07290D3599E7A0D62097A346EFCC1FB5,E7750A37CAB07D0DFF0AF7E3573AC141,2013-01-01 00:00:00,2013-01-01 00:02:00,120,0.44,-73.956528,40.716976,-73.962440,40.715008,CSH,3.50,0.50,0.50,0.00,0.00,4.50
	22D70BF00EEB0ADC83BA8177BB861991,3FF2709163DE7036FCAA4E5A3324E4BF,2013-01-01 00:02:00,2013-01-01 00:02:00,0,0.00,0.000000,0.000000,0.000000,0.000000,CSH,27.00,0.00,0.50,0.00,0.00,27.50
	0EC22AAF491A8BD91F279350C2B010FD,778C92B26AE78A9EBDF96B49C67E4007,2013-01-01 00:01:00,2013-01-01 00:03:00,120,0.71,-73.973145,40.752827,-73.965897,40.760445,CSH,4.00,0.50,0.50,0.00,0.00,5.00
	1390FB380189DF6BBFDA4DC847CAD14F,BE317B986700F63C43438482792C8654,2013-01-01 00:01:00,2013-01-01 00:03:00,120,0.48,-74.004173,40.720947,-74.003838,40.726189,CSH,4.00,0.50,0.50,0.00,0.00,5.00
	3B4129883A1D05BE89F2C929DE136281,7077F9FD5AD649AEACA4746B2537E3FA,2013-01-01 00:01:00,2013-01-01 00:03:00,120,0.61,-73.987373,40.724861,-73.983772,40.730995,CRD,4.00,0.50,0.50,0.00,0.00,5.00
	5FAA7F69213D26A42FA435CA9511A4FF,00B7691D86D96AEBD21DD9E138F90840,2013-01-01 00:02:00,2013-01-01 00:03:00,60,0.00,0.000000,0.000000,0.000000,0.000000,CRD,2.50,0.50,0.50,0.25,0.00,3.75
	DFBFA82ECA8F7059B89C3E8B93DAA377,CF8604E72D83840FBA1978C2D2FC9CDB,2013-01-01 00:02:00,2013-01-01 00:03:00,60,0.39,-73.981544,40.781475,-73.979439,40.784386,CRD,3.00,0.50,0.50,0.70,0.00,4.70
	1E5F4C1CAE7AB3D06ABBDDD4D9DE7FA6,E0B2F618053518F24790C7FD0264E302,2013-01-01 00:03:00,2013-01-01 00:04:00,60,0.00,-73.993973,40.751266,0.000000,0.000000,CSH,2.50,0.50,0.50,0.00,0.00,3.50
	468244D1361B8A3EB8D206CC394BC9E9,BB899DFEA9CC964B50C540A1D685CCFB,2013-01-01 00:00:00,2013-01-01 00:04:00,240,1.71,-73.955383,40.779728,-73.967758,40.760326,CSH,6.50,0.50,0.50,0.00,0.00,7.50
	5F78CC6D4ECD0541B765FECE17075B6F,B7567F5BFD558C665D23B18451FE1FD1,2013-01-01 00:00:00,2013-01-01 00:04:00,240,1.21,-73.973000,40.793140,-73.981453,40.778465,CRD,6.00,0.50,0.50,1.30,0.00,8.30

The data file is sorted chronologically according to the *dropoff_datetime* field.
Events with the same dropoff_datetime are in random order.

Please note that the quality of the data is not perfect.
Some events might miss information such as drop off and pickup coordinates, or fare information.
In addition, some information, such as the fare price might have been entered incorrectly by the taxi drivers thus, introducing additional noise.
However, solving data quality issues is outside of the scope of this practical.

## 2. The Challenge

The challenge of this practical is to solve the following query:

	Find the top 10 most frequent cab routes.

In this query, a route is represented by a starting grid cell and an ending grid cell.
A cells is a square of 500 m X 500 m.
The cell grid starts with cell 1.1, located at (41.474937, -74.913585), in Barryville.
More precisely, coordinate (41.474937, -74.913585) marks the center of the first cell.
Cell numbers increase towards the east and south, with the shift to east being the first and the shift to south the second component of the cell, i.e., cell 3.7 is 2 cells east and 6 cells south of cell 1.1.

The overall grid expands 150km south and 150km east from cell 1.1 with the cell 300.300 being the last cell in the grid.
All trips starting or ending outside this area are treated as *outliers* and must not be considered in the result computation.

## 2.1 Tentative plan

Below, we propose you a tentative plan to implement the query atop Apache Spark.
The plan unrolls in three phases.
In a nutshell, our idea is first to solve the problem statically, when considering a chunk of the data.
In this task, we do not consider the grid of cells, but solely each route.
Then, we refine our solution and solve the online problem, leveraging the streaming capabilities of Spark.
In the last phase, we process the data this time taking into account the grid.

Below, we present several code snippet at each phase of the plan.
Code snippets are in Python -- but you may adapt them with a few modifications to Scala (or even Java).
We use the command *spark-submit* to submit the job locally (using *--master local script.py*).

### 2.1.1 Phase 1

1. Split the data into chunks of 1000 lines using the Bash *split* command.
Hereafter, we use the first chunk, that is the file "aa".
2. Create a *SparkContex* object and use it to create an rdd from the file *frdd=sc.textFile("data/xaa")*.
3. Prepare the data of the file before processing it.
(3.1.) Extract the coordinates of the start and end points.
(3.2.) Use these coordinates to sign the route e.g., "73.981544:40.781475:-73.979439:40.784386".
(3.3.) With the *map* operator create tuples of the form *(route,1)* for each line of the file
4. Use the *reduce* operator to count the number of hits for each cab route.
5. Revert the tuples in the rdd, to obtain something of the form *(#hits,route)*.
6. Extract the top 10 routes with the operator *top(10)*.

### 2.1.2 Phase 2

1. Create a *StreamingContext* with an update rate of 1 second.
2. Create a queue *rddQ* and populate it with the rdds created from all the splits of the data file.
Here, you might use something of the form: *rddQ.append(sc.textFile("data/split/"+filename))*.
3. Obtain a dstream from the queue with *dstream = ssc.queueStream(rddQ)*.
4. Prepare the data as in phase 1.
5. Use the *updateStateByKey(update_count)* to create a stateful operator *update_count* which updates the count of each route every time a new rdd is received
6. Using the operator *foreachRDD*, constantly update the result at the driver.
7. When testing your code, you should use *ssc.awaitTerminationOrTimeout* to stop the computation after some time.

### 2.1.3 Phase 3

7. Create a *Cell* that holds two fields *x* and *y* that stores respectively the x and y position of the cell in the grid.
The constructor of a *Cell* object takes as input a latitude and a longitude, and sets *x* and *y* appropriately.
In this task, we advise you to use the *haversine* function from the *haversine* package.
8. Create the start and end cells from each line of the data stream.
(Here, you should handle the case when a cell is outside of the grid.)

## 2.2 Extension: Misra-Gries Algorithm

A major downside of using *updateStateByKey* is the fact that for each new incoming batch, the transformation iterates the entire state store, regardless of whether a new value for a given key has been consumed or not.
This effects performance especially when dealing with a large amount of state over time.

To sidestep the above problem, we propose you to implement and use the algorithm of Misra and Gries.
This algorithm is detailed in the [following](http://www.cs.dartmouth.edu/~ac/Teach/CS49-Fall11/Notes/lecnotes.pdf) document, at pages 8-9.
This is one of the first so-called sketch algorithm.
It allows to compute approximately the heavy hitters in a data stream, using a bounded amount of memory.
