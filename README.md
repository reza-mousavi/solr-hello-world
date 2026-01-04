# SOLR: A Distributed Coordination Service

ApacheTM Solr is a search server built on top of **Apache LuceneTM**, an open source, Java-based, information retrieval library. 

It also has secondary support for limited relational, graph, statistical, data analysis or storage related use cases. 

Solr Cloud? what is it? 

## Design Goals

- **Scalability** Supports clustering, horizontal scaling, through Zookeeper 
- **Replication** Data can be replicated across cluster
- **High Availability** It supports cluster of hosts
- **High Throughput** data is kept in-memory.
- **Various Response Types** JSON is the default response format, but it could also be XML, CSV, optimized binary.

## Terms

- **Queries** transmitted to Solr via HTTP 1.1 or 2.0 requests.
- **Document** any long string
- **Field** Attributes of a document in terms of key value with data type
- **Schema** A document can have an schema
- **Index** Process of adding document that Solr can digest for searching, Apache Tika, HTTP calls, CLI 

## API Flow

- Create a collection
- Define a schema
- Index Data
- Search

Create a collection

```bash
curl --request POST --url http://localhost:8983/api/collections \
  --header 'Content-Type: application/json' \
  --data '{ "name": "reza", "numShards": 1, "replicationFactor": 1 }'
```

Define a Schema 

```bash
curl --request POST \
--url http://localhost:8983/api/collections/reza/schema \
--header 'Content-Type: application/json' \
--data '{
"add-field": [
    {"name": "name", "type": "text_general", "multiValued": false},
    {"name": "cat", "type": "string", "multiValued": true},
    {"name": "manu", "type": "string"},
    {"name": "features", "type": "text_general", "multiValued": true},
    {"name": "weight", "type": "pfloat"},
    {"name": "price", "type": "pfloat"},
    {"name": "popularity", "type": "pint"},
    {"name": "inStock", "type": "boolean", "stored": true},
    {"name": "store", "type": "location"}
]
}'
```

Index Data

```bash
curl --request POST \
--url 'http://localhost:8983/api/collections/reza/update' \
  --header 'Content-Type: application/json' \
  --data '  {
    "id" : "978-0641723445",
    "cat" : ["book","hardcover"],
    "name" : "The Lightning Thief",
    "author" : "Rick Riordan",
    "series_t" : "Percy Jackson and the Olympians",
    "sequence_i" : 1,
    "genre_s" : "fantasy",
    "inStock" : true,
    "price" : 12.50,
    "pages_i" : 384
  }'
```

Commit changes

```bash
curl -X POST -H 'Content-type: application/json' \
    -d '{"set-property":{"updateHandler.autoCommit.maxTime":15000}}' \
    http://localhost:8983/api/collections/reza/config
```

Search

```bash
curl 'http://localhost:8983/solr/reza/select?q=name%3Alightning'
```

Delete an Index

```bash
curl 'http://localhost:8983/solr/techproducts/update?stream.body=<delete><query>*:*</query></delete>&commit=true'
```


### Solr CLI

```
```

### Solr on Docker

Solr can be executed on different ways

```bash
solr-demo
```

```bash
solr-precreate gettingstarted
```

```bash
```

*NB: Running Solr on docker requires permission on the files. For that you need to grant READ/WRTIE/EXECUTE permission to data folder*

```bash
chmod -R o+rwx data
```
 

## Index Data

Could be achieved in multiple ways. Assuming we want to index some data on techproducts

Using Command line from one of Solr containers:

```bash
/bin/solr post -c techproducts example/exampledocs/*
```

Or 

```bash
java -jar -Dc=techproducts -Dauto example\exampledocs\post.jar example\exampledocs\*
```

- Using REST API
- Using Another Docker Container, look for the Solr documentation, mind network!

```bash
docker run --rm -e SOLR_HOST=solr1 --network docs_solr solr solr create -c gettingstarted3 -p 8983
```
### Solr For Java

The project is called, **SolrJ**, aims to ease Solr connection through Http. It supports multiple HTTP clients

- **SolrClient** Parent class
- **HttpSolrClient** - Communicates directly with a single Solr node.
- **Http2SolrClient** - async, non-blocking and general-purpose client that leverage HTTP/2 using the Jetty Http library.
- **HttpJdkSolrClient** - General-purpose client using the JDK’s built-in Http Client. 
- **LBHttpSolrClient** - balances request load across a list of Solr nodes. 
- **LBHttp2SolrClient** - just like LBHttpSolrClient but using Http2SolrClient instead, with the Jetty Http library.
- **CloudSolrClient** - geared towards communicating with SolrCloud deployments. 
- **ConcurrentUpdateSolrClient** - geared towards indexing-centric workloads. Buffers documents internally before sending larger batches to Solr.
- **ConcurrentUpdateHttp2SolrClient** - just like ConcurrentUpdateSolrClient but using Http2SolrClient instead, with the Jetty Http library.

## Running Application

- Build Java Client using Java 17
- Run docker images
- Browse Solr http://localhost:8983/solr/#/techproducts/query

## More Info


| Technology         | Description                                                                                                          |
|--------------------|----------------------------------------------------------------------------------------------------------------------|
| Solr               | [Solr](https://solr.apache.org/)                                                                                     |
| Solr Documentation | [Solr Docs](https://solr.apache.org/guide/solr/latest/index.html)                                                    |
| Solr CSR           | [Solr Control Script](https://solr.apache.org/guide/solr/latest/deployment-guide/solr-control-script-reference.html) |
| SolrJ              | [SolrJ](https://solr.apache.org/guide/solr/latest/deployment-guide/solrj.html)                                       |
| Solr Docker        | [Solr Docker](https://solr.apache.org/guide/solr/latest/deployment-guide/solr-in-docker.html)                        |
| Solr DockerHub     | [Solr DockerHub](https://hub.docker.com/_/solr)                                                                      |
