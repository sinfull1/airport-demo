# kafka-clickhouse
Sample repository example to pick batches from kafka into clickhouse
The project aims to process airline flight data and load it into a ClickHouse database.
It provides two APIs that allow users to retrieve information about flight paths: one for finding the flight path 
with the maximum number of flight stops and another for finding the shortest path between two airports.

The process starts by gathering airline flight data, which typically includes information such as departure airport, 
arrival airport, flight duration, and flight stops. 
This data is then processed and transformed into a suitable format for loading into the ClickHouse database.

Once the flight data is loaded into the ClickHouse database, the project exposes two APIs for querying flight paths. 
The first API retrieves the flight path with the maximum number of flight stops. It analyzes the flight data stored in ClickHouse, 
identifies the path with the highest number of stops, and returns the result to the user.

The second API allows users to find the shortest path between two airports. 
It takes two airport codes as input and performs a graph traversal algorithm on the flight data in ClickHouse to determine
the shortest path between the given airports. The result is returned to the user, 
including details such as the total duration of the flight and any stops along the way.

These APIs provide valuable insights and information for analyzing flight patterns, optimizing travel routes,
and understanding connectivity between airports. Users can leverage the flight data stored in the ClickHouse database 
to make informed decisions about flight routes and plan their travels efficiently.

Overall, this project combines data processing, database management using ClickHouse, 
and API development to provide useful flight path information based on airline flight data. 
It enables users to explore flight patterns and find optimal routes for their journeys.






Regenerate response
