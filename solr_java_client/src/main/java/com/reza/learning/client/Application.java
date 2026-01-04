package com.reza.learning.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.impl.CloudHttp2SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {


    @Value("${app.client.zookeeper.cluster}")
    private String[] cluster;

    @Value("${app.client.solr.default-collection}")
    private String defaultCollection;

    @Value("${app.client.zookeeper.connection.timeout}")
    private Integer zookeeperConnectionTimeout;

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication.run(Application.class, args);
        log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        log.info("EXECUTING : command line runner");
        log.info("Connecting to zookeepers: cluster at '{}' to query '{}'", cluster, defaultCollection);

        try {
            var builder = createBuilder();

            try (var client = builder.build()){
                var queryParams = createQuery();
                final var response = client.query(queryParams);
                parseResponse(response);
            }
        } catch (Exception e) {
            log.info("Error occurred");
            throw new RuntimeException(e);
        }
        log.info("Executed!");

    }

    private static void parseResponse(QueryResponse response) {
        var documents = response.getResults();

        log.info("Found " + documents.getNumFound() + " documents");
        for (var document : documents) {
            final String id = (String) document.getFirstValue("id");
            final String name = (String) document.getFirstValue("name");

            log.info("id: " + id + "; name: " + name);
        }
    }

    private CloudHttp2SolrClient.Builder createBuilder() {
        return new CloudSolrClient
                .Builder(List.of(cluster), Optional.empty())
                .withDefaultCollection(defaultCollection)
                .withZkConnectTimeout(zookeeperConnectionTimeout, TimeUnit.MILLISECONDS);
    }

    private static MapSolrParams createQuery() {
        final Map<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("q", "*:*");
        queryParamMap.put("fl", "id, name");
        queryParamMap.put("sort", "id asc");
        return new MapSolrParams(queryParamMap);
    }
}