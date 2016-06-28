gpxToBd.jar is an algorithm to transform gpx files into a trajectory, split it into moves and stops and save in a 

postgres database. The input of the jar file is a configuration file and the path of the gpx files. Like this:

"gpxToBd.jar config.txt c:\gpx"

The algorithm creates 3 tables in the database: fulltrajgpx, movestrajgpx and stopstrajgpx. 

- fulltrajgpx: table with all the points from the gpx file
- movestrajgpx: table with a line of the moves with the start and end time
- stopstrajgpx: table with a line of the stops with the start and end time

The config file has the following itens:

url of the postgres server
database name
user login
user password
minumum time of the stop (in seconds)
average speed of the points in the stop (in %)
speed limit of a point to be included in the stop (in %)
number of tolerace points with speed greater than the speed limit (in units)

For example:

jdbc:postgresql://localhost:5432/
EdinburghPedestrian
postgres
postgres
300
0.2
0.4
5
