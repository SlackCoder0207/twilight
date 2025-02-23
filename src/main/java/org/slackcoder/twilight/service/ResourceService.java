package org.slackcoder.twilight.service;

import org.slackcoder.twilight.model.Resource;
import org.slackcoder.twilight.model.User;
import org.slackcoder.twilight.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    public Resource addResource(String title, String description, String category, String url, UUID publisher, String type) {
        Resource resource = new Resource(title, description, category, url, publisher, type);
        return resourceRepository.save(resource);
    }

    public List<Resource> getResourcesByCategory(String category) {
        return resourceRepository.findByCategory(category);
    }
}
