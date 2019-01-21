package com.emc.ecs.management.sdk;

import com.emc.ecs.cloudfoundry.broker.EcsManagementClientException;
import com.emc.ecs.management.sdk.model.*;

abstract public class ECSManagementClient implements Client {
    private Connection connection;

    public void createBucket(String id, String namespace, String replicationGroup)
            throws EcsManagementClientException {
        BucketAction.create(this.connection, id, namespace, replicationGroup);
    }
    public void createBucket(ObjectBucketCreate createParam)
            throws EcsManagementClientException {
        BucketAction.create(this.connection, createParam);
    }
    public boolean exists(String id, String namespace)
            throws EcsManagementClientException {
        return BucketAction.exists(this.connection, id, namespace);
    }
    public ObjectBucketInfo get(String id, String namespace)
            throws EcsManagementClientException {
        return BucketAction.get(this.connection, id, namespace);
    }
    public void delete(String id, String namespace)
            throws EcsManagementClientException {
        BucketAction.delete(this.connection, id, namespace);
    }
}
