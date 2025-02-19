package org.slackcoder.twilight.repository;

import org.slackcoder.twilight.model.Resource;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;
import java.util.UUID;

public interface ResourceRepository extends Neo4jRepository<Resource, UUID> {
    List<Resource> findByCategory(String category);
}
