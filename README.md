Sample repository example to demonstrate few things:

1. Loading data from file into Clickhouse
2. Using Hibernate clickhouse dialect to perform analytics querys
3. Read kafka messages to insert into clickhouse tables

This project uses basic ideas from the blog below

https://altinity.com/blog/harnessing-the-power-of-clickhouse-arrays-part-2
https://clickhouse.com/docs/en/getting-started/example-datasets/ontime

We build two API

1. /getMaxHops -> Return the max hops source airport and destination airport in the data
2. /path/shortest/{origin}/{dest} - give the shortest flight path between two airports

For Graph implementation we use JgraphT library

