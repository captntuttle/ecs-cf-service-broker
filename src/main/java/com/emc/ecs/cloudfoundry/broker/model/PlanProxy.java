package com.emc.ecs.cloudfoundry.broker.model;

//import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Component
public class PlanProxy {
    private String id;
    private String name;
    private String description;
    private Boolean free = false;
    private PlanMetadataProxy metadata;
    private Map<String, Object> serviceSettings = new HashMap<>();
    private boolean repositoryPlan = false;

    public PlanProxy() {
        super();
    }

    public PlanProxy(String id, String name, String description,
                     PlanMetadataProxy metadata, Boolean free) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.metadata = metadata;
        this.free = free;
    }

    public PlanProxy(String id, String name, String description,
                     PlanMetadataProxy metadata, Boolean free, Map<String, Object> serviceSettings) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.metadata = metadata;
        this.free = free;
        this.serviceSettings = serviceSettings;
    }

//    Plan unproxy() {
//        return new Plan(id, name, description, metadata.unproxy(), free);
//    }

    Plan unproxy() {
        return Plan.builder()
                .id(id)
                .name(name)
                .description(description)
                .metadata(metadata.unproxy())
                .free(free)
                .build();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanMetadataProxy getMetadata() {
        return metadata;
    }

    public void setMetadata(PlanMetadataProxy metadata) {
        this.metadata = metadata;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Map<String, Object> getServiceSettings() {
        return serviceSettings;
    }

    public void setServiceSettings(Map<String, Object> serviceSettings) {
        this.serviceSettings = serviceSettings;
    }

    public void setRepositoryPlan(boolean repositoryPlan) {
        this.repositoryPlan = repositoryPlan;
    }

    public boolean getRepositoryPlan() {
        return repositoryPlan;
    }
}