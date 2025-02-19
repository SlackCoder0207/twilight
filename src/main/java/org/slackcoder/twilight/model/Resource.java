package org.slackcoder.twilight.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.UUID;

@Data
public class Resource {
    @Id
    private UUID resourceId;
    private String title;
    private String description;
    private String category;
    private String url;
    private UUID publisher;

    public Resource(String title, String description, String category, String url, UUID publisher) {
        this.resourceId = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
        this.publisher = publisher;
    }

}
