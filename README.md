
## Sample project for using clickhouse with hibenrate 6 and kafka

Sample repository example to demonstrate few things:

1. Loading data from file into Clickhouse
2. Using Hibernate clickhouse dialect to perform analytics querys
3. Read kafka messages to insert into clickhouse tables

This project uses basic ideas from the blog below

## References

- [Harnessing the Power of ClickHouse Arrays (Part 2)](https://altinity.com/blog/harnessing-the-power-of-clickhouse-arrays-part-2)
- [ClickHouse Example Datasets - On-Time Flight Performance](https://clickhouse.com/docs/en/getting-started/example-datasets/ontime)

We build two API
1. /getMaxHops  -> Return the max hops source airport and destination airport in the data
2. /path/shortest/{origin}/{dest}   - give the shortest flight path between two airports

Clickhouse dialect inclusion

        <dependency>
            <groupId>io.github.sinfull1</groupId>
            <artifactId>hibernate6-clickhouse-dialect</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>

Hibernate 

    @Query(value = "SELECT o.flightDate as flightDate, o.reportingAirline as airline, o.tailNumber as tailNumber,\n" +
            "    arraySort(groupArray(o.arrTime)) AS arrivals, " +
            "    arraySort('(x, y) -> y', groupArray(o.origin), groupArray(o.arrTime)) AS origins, " +
            "    groupArray(1, o.dest) AS groups, " +
            "    topK(3, o.dest) AS tops " +
            "  FROM Ontime o" +
            "  WHERE o.depTime < o.arrTime " +
            "  GROUP BY flightDate, airline, tailNumber " +
            "  ORDER BY flightDate, airline, tailNumber LIMIT 10")
    List<NewResultDao> getAnalysis();

    @Query(value = "SELECT o.origin as origin, " +
            " o.originCityName as origCity, " +
            " o.dest as destination, " +
            " o.destCityName as destCity, " +
            "  arrayAvg(groupArray(o.arrTime - o.depTime)) as times " +
            "  FROM Ontime o" +
            "  WHERE o.depTime < o.arrTime " +
            "  GROUP BY origin, origCity, destination, destCity " +
            "  ORDER BY origin, origCity, destination, destCity")
    List<EdgeListDao> getEdgeList();


For Graph implementation we use JgraphT library

