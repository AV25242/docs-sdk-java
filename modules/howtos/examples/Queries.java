// #tag::imports[]
import com.couchbase.client.java.*;
import com.couchbase.client.java.json.*;
import com.couchbase.client.java.query.*;
import reactor.core.publisher.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
// #end::imports[]


class Queries {
void cluster() {
// #tag::cluster[]
Cluster cluster = Cluster.connect("localhost", "username", "password");
Bucket bucket = cluster.bucket("travel-sample");
// #end::cluster[]
}

// For testing
Cluster cluster = Cluster.connect("localhost", "Administrator", "password");

public Queries() {
    Bucket bucket = cluster.bucket("travel-sample");
}

public static void main(String[] args) {
    Queries queries = new Queries();
    queries.simple();
}

void simple() {
// #tag::simple[]
QueryResult result = cluster.query("select * from `travel-sample` limit 10");

for (JsonObject row : result.rowsAsObject()) {
  System.out.println("Row: " + row);
}
// #end::simple[]
}

void positional() {
// #tag::positional[]
String stmt = "select * from `travel-sample` where type=$1 and country=$2 limit 10";
QueryResult result = cluster.query(
  stmt,
  queryOptions().parameters(JsonArray.from("airline", "United States"))
);
// #end::positional[]
}

void named() {
// #tag::named[]
String stmt = "select * from `travel-sample` where type=$type and country=$country limit 10";
QueryResult result = cluster.query(
  stmt,
  queryOptions().parameters(JsonObject.create()
    .put("type", "airline")
    .put("country", "United States")
  )
);
// #end::named[]
}

void requestPlus() {
// #tag::request-plus[]
String stmt = "select * from `travel-sample` limit 10";
QueryResult result = cluster.query(stmt, queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS));
// #end::request-plus[]
}

void async() {
// #tag::async[]
AsyncCluster async = cluster.async();
CompletableFuture<QueryResult> future = async.query("select * from `travel-sample` limit 10");

// Just for demo purposes, block on the CompletableFutures.
try {
  List<JsonObject> rows = future.thenApply(QueryResult::rowsAsObject).get();
} catch (InterruptedException | ExecutionException e) {
  e.printStackTrace();
}
// #end::async[]
}

void reactive() {
// #tag::reactive[]
ReactiveCluster reactive = cluster.reactive();
Mono<ReactiveQueryResult> mono = reactive.query("select * from `travel-sample`");

Flux<JsonObject> rows = mono.flatMapMany(ReactiveQueryResult::rowsAsObject);

// Just for example, block on the rows.  This is not best practice and apps
// should generally not block.
List<JsonObject> allRows = rows
  .doOnNext(System.out::println)
  .doOnError(err -> System.err.println("Error: " + err))
  .collectList()
  .block();

// #end::reactive[]
}

}
