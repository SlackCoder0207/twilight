package org.slackcoder.twilight.controller;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@PreAuthorize("permitAll()")
public class GraphController {

    private final Driver neo4jDriver;

    public GraphController(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    @GetMapping("/api/embeddingGraph")
    public ResponseEntity<Map<String, Object>> getEmbeddingGraph() {
        try (Session session = neo4jDriver.session(SessionConfig.forDatabase("twilight"))) {
            var nodeResult = session.run("MATCH (n) WHERE n:User OR n:Resource RETURN n");
            List<Map<String, Object>> nodeList = new ArrayList<>();
            while (nodeResult.hasNext()) {
                Record record = nodeResult.next();
                Node node = record.get("n").asNode();
                Map<String, Object> nodeMap = new HashMap<>();
                nodeMap.put("id", node.id());
                nodeMap.put("labels", node.labels());
                nodeMap.put("properties", node.asMap());
                nodeList.add(nodeMap);
            }

            //查询所有INTERACTS_WITH关系
            var relResult = session.run("MATCH (n:User)-[r:INTERACTS_WITH]->(m:Resource) RETURN r");
            List<Map<String, Object>> relList = new ArrayList<>();
            while (relResult.hasNext()) {
                Record record = relResult.next();
                Relationship rel = record.get("r").asRelationship();
                Map<String, Object> relMap = new HashMap<>();
                relMap.put("id", rel.id());
                relMap.put("type", rel.type());
                relMap.put("startNode", rel.startNodeId());
                relMap.put("endNode", rel.endNodeId());
                relMap.put("properties", rel.asMap());
                relList.add(relMap);
                System.out.println(relMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("nodes", nodeList);
            response.put("relationships", relList);
            System.out.println(response);
            return ResponseEntity.ok(response);
        }
    }
}
