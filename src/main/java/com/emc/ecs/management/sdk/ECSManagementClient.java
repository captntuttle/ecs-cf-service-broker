package com.emc.ecs.management.sdk;

import com.emc.ecs.cloudfoundry.broker.EcsManagementClientException;
import com.emc.ecs.management.sdk.model.*;

import java.util.List;

abstract public class ECSManagementClient implements Client {

    // BucketActions
    public void createBucket(Connection connection, String id, String namespace, String replicationGroup)
            throws EcsManagementClientException {
        BucketAction.create(connection, id, namespace, replicationGroup);
    }
    public void createBucket(Connection connection, ObjectBucketCreate createParam)
            throws EcsManagementClientException {
        BucketAction.create(connection, createParam);
    }
    public boolean bucketExists(Connection connection, String id, String namespace)
            throws EcsManagementClientException {
        return BucketAction.exists(connection, id, namespace);
    }
    public ObjectBucketInfo getBucketInfo(Connection connection, String id, String namespace)
            throws EcsManagementClientException {
        return BucketAction.get(connection, id, namespace);
    }
    public void deleteBucket(Connection connection, String id, String namespace)
            throws EcsManagementClientException {
        BucketAction.delete(connection, id, namespace);
    }

    // BucketQuotaActions
    public void createBucketQuota(Connection connection, String id, String namespace, int limit, int warn)
            throws EcsManagementClientException {
        BucketQuotaAction.create(connection, id, namespace, limit, warn);
    }
    public void deleteBucketQuota(Connection connection, String id, String namespace)
            throws EcsManagementClientException {
        BucketQuotaAction.delete(connection, id, namespace);
    }
    public BucketQuotaDetails getBucketQuotaDetails(Connection connection, String id, String namespace)
            throws EcsManagementClientException {
        return BucketQuotaAction.get(connection, id, namespace);
    }

    // BucketRetentionActions
    public DefaultBucketRetention getBucketRetention(Connection connection, String namespace, String bucket)
            throws EcsManagementClientException {
        return BucketRetentionAction.get(connection, namespace, bucket);
    }
    public void updateBucketRetention(Connection connection, String namespace, String bucket, int period)
            throws EcsManagementClientException {
        BucketRetentionAction.update(connection, namespace, bucket, period);
    }

    // BucketAclActions
    public void updateBucketAcl(Connection connection, String id, BucketAcl acl)
            throws EcsManagementClientException {
        BucketAclAction.update(connection, id, acl);
    }
    public BucketAcl getBucketAcl(Connection connection, String id, String namespace)
            throws EcsManagementClientException {
        return BucketAclAction.get(connection, id, namespace);
    }

    // ObjectUserActions
    public void createObjectUser(Connection connection, String id,
                                 String namespace) throws EcsManagementClientException {
        ObjectUserAction.create(connection, id, namespace);
    }
    public boolean objectUserExists(Connection connection, String id,
                                    String namespace) throws EcsManagementClientException {
        return ObjectUserAction.exists(connection, id, namespace);
    }
    public void deleteObjectUser(Connection connection, String id)
            throws EcsManagementClientException {
        ObjectUserAction.delete(connection, id);
    }

    // ObjectUserSecretActions
    public UserSecretKey createObjectUserSecretKey(Connection connection, String id)
            throws EcsManagementClientException {
        return ObjectUserSecretAction.create(connection, id);
    }
    public UserSecretKey createObjectUserSecretKey(Connection connection, String id,
                                                   String key) throws EcsManagementClientException {
        return ObjectUserSecretAction.create(connection, id, key);
    }
    public List<UserSecretKey> listObjectUserSecretKey(Connection connection, String id)
            throws EcsManagementClientException {
        return ObjectUserSecretAction.list(connection, id);
    }


    // NamespaceActions
    public boolean namespaceExists(Connection connection, String namespace)
            throws EcsManagementClientException {
        return NamespaceAction.exists(connection, namespace);
    }
    public void createNamespace(Connection connection, String namespace,
                         String namespaceAdmins, String replicationGroup)
            throws EcsManagementClientException {
        NamespaceAction.create(connection, namespace, namespaceAdmins, replicationGroup);
    }
    public void createNamespace(Connection connection, NamespaceCreate createParam)
            throws EcsManagementClientException {
        NamespaceAction.create(connection, createParam);
    }
    public void deleteNamespace(Connection connection, String namespace)
            throws EcsManagementClientException {
        NamespaceAction.delete(connection, namespace);
    }
    public NamespaceInfo getNamespaceInfo(Connection connection, String namespace)
            throws EcsManagementClientException {
        return NamespaceAction.get(connection, namespace);
    }
    public void updateNamespace(Connection connection, String namespace, NamespaceUpdate updateParam)
            throws EcsManagementClientException {
        NamespaceAction.update(connection, namespace, updateParam);
    }

    // NamespaceQuotaActions
    public void createNamespaceQuota(Connection connection, String namespace, NamespaceQuotaParam createParam)
            throws EcsManagementClientException {
        NamespaceQuotaAction.create(connection, namespace, createParam);
    }
    public NamespaceQuotaDetails getNamespaceQuotaDetails(Connection connection, String namespace)
            throws EcsManagementClientException {
        return NamespaceQuotaAction.get(connection, namespace);
    }
    public void deleteNamespaceQuota(Connection connection, String namespace)
            throws EcsManagementClientException {
        NamespaceQuotaAction.delete(connection, namespace);
    }

    // NamespaceRetentionActions
    public Boolean namespaceRetentionExists(Connection connection, String namespace, String retentionClass)
            throws EcsManagementClientException  {
        return NamespaceRetentionAction.exists(connection, namespace, retentionClass);
    }
    public void createNamespaceRetention(Connection connection, String namespace, RetentionClassCreate createParam)
            throws EcsManagementClientException {
        NamespaceRetentionAction.create(connection, namespace, createParam);
    }
    public void deleteNamespaceRetention(Connection connection, String namespace, String retentionClass)
            throws EcsManagementClientException {
        NamespaceRetentionAction.delete(connection, namespace, retentionClass);
    }
    public RetentionClassDetails getNamespaceRetention(Connection connection, String namespace, String retentionClass)
            throws EcsManagementClientException {
        return NamespaceRetentionAction.get(connection, namespace, retentionClass);
    }
    public void updateNamespaceRetention(Connection connection, String namespace,
                                         String retentionClass, RetentionClassUpdate retentionClassUpdate)
            throws EcsManagementClientException {
        NamespaceRetentionAction.update(connection, namespace, retentionClass, retentionClassUpdate);
    }

    // BaseUrlActions
    public List<BaseUrl> listBaseUrl(Connection connection)
            throws EcsManagementClientException {
        return BaseUrlAction.list(connection);
    }
    public BaseUrlInfo getBaseUrlInfo(Connection connection, String id)
            throws EcsManagementClientException {
        return BaseUrlAction.get(connection, id);
    }

    // NFSExportActions
    public List<NFSExport> listNFSExport(Connection connection, String pathPrefix)
            throws EcsManagementClientException {
        return NFSExportAction.list(connection, pathPrefix);
    }
    public void createNFSExport(Connection connection, String exportPath)
            throws EcsManagementClientException {
        NFSExportAction.create(connection, exportPath);
    }
    public void deleteNFSExport(Connection connection, int exportId)
            throws EcsManagementClientException {
        NFSExportAction.delete(connection, exportId);
    }
}
