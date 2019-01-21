package com.emc.ecs.management.sdk;

import com.emc.ecs.cloudfoundry.broker.EcsManagementClientException;
import com.emc.ecs.management.sdk.model.ObjectBucketCreate;
import com.emc.ecs.management.sdk.model.ObjectBucketInfo;

public interface Client {
    // BucketAction
    void createBucket(Connection connection, String id, String namespace, String replicationGroup)
            throws EcsManagementClientException;
    void createBucket(Connection connection, ObjectBucketCreate createParam)
            throws EcsManagementClientException;
    boolean exists(Connection connection, String id, String namespace)
            throws EcsManagementClientException;
    ObjectBucketInfo get(Connection connection, String id, String namespace)
            throws EcsManagementClientException;
    void delete(Connection connection, String id, String namespace)
            throws EcsManagementClientException;
}
