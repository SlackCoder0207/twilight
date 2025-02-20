package org.slackcoder.twilight.repository;

import org.slackcoder.twilight.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends Neo4jRepository<User, UUID> {
    @Query("MATCH (u:User) WHERE u.userId = $userId RETURN u")
    Optional<User> findByUserId(UUID userId);
}
