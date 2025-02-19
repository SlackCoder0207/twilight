package org.slackcoder.twilight.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@Data
@NoArgsConstructor
@Node("Resource")
public class Resource {
    @Id
    private UUID resourceId;  // 资源唯一标识

    private String title;  // 资源标题
    private String description;  // 资源描述
    private String category;  // 资源类别
    private String url;  // 资源链接

    @Relationship(type = "PUBLISHED_BY", direction = Relationship.Direction.OUTGOING)
    private User publisher;  // 资源发布者（User 关联）

    public Resource(String title, String description, String category, String url, User publisher) {
        this.resourceId = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
        this.publisher = publisher;
    }
}
