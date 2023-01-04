# tailnumber-server
Tail Number Backend / Server

I made a little app to look up airplane tail numbers (USA & Switzerland). You can query by tail number or by owner / operator.

The data is stored in Cassandra and Elasticsearch.

[API endpoints](api/src/main/kotlin/com/edelweiss/software/tailnumber/server/api/plugins/Routing.kt)

If you want to run this you'd first want to import the data, then export it to Cassandra and Elasticsearch, and finally launch the API server. See the [scripts](scripts/).
