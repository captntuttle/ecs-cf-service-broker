package com.emc.ecs.management.sdk;

import com.emc.ecs.cloudfoundry.broker.EcsManagementClientException;
import com.emc.ecs.management.sdk.model.*;

import java.util.List;

public interface Client {
    // BucketAction
    void createBucket(Connection connection, String id, String namespace, String replicationGroup)
            throws EcsManagementClientException;
    void createBucket(Connection connection, ObjectBucketCreate createParam)
            throws EcsManagementClientException;
    boolean bucketExists(Connection connection, String id, String namespace)
            throws EcsManagementClientException;
    ObjectBucketInfo getBucketInfo(Connection connection, String id, String namespace)
            throws EcsManagementClientException;
    void deleteBucket(Connection connection, String id, String namespace)
            throws EcsManagementClientException;

    // BucketQuotaActions
    void createBucketQuota(Connection connection, String id, String namespace, int limit, int warn)
            throws EcsManagementClientException;
    void deleteBucketQuota(Connection connection, String id, String namespace)
            throws EcsManagementClientException;
    BucketQuotaDetails getBucketQuotaDetails(Connection connection, String id, String namespace)
            throws EcsManagementClientException;

    // BucketRetentionActions
    DefaultBucketRetention getBucketRetention(Connection connection, String namespace, String bucket)
            throws EcsManagementClientException;
    void updateBucketRetention(Connection connection, String namespace, String bucket, int period)
            throws EcsManagementClientException;

    // BucketAclActions
    void updateBucketAcl(Connection connection, String id, BucketAcl acl)
            throws EcsManagementClientException;
    BucketAcl getBucketAcl(Connection connection, String id,
                  String namespace) throws EcsManagementClientException;

    // ObjectUserActions
    void createObjectUser(Connection connection, String id,
                String namespace) throws EcsManagementClientException;
    boolean objectUserExists(Connection connection, String id,
                   String namespace) throws EcsManagementClientException;
    void deleteObjectUser(Connection connection, String id)
            throws EcsManagementClientException;

    // ObjectUserSecretActions
    UserSecretKey createObjectUserSecretKey(Connection connection, String id)
            throws EcsManagementClientException;
    UserSecretKey createObjectUserSecretKey(Connection connection, String id,
                         String key) throws EcsManagementClientException;
    List<UserSecretKey> listObjectUserSecretKey(Connection connection, String id)
            throws EcsManagementClientException;

    // NamespaceActions
    boolean namespaceExists(Connection connection, String namespace)
            throws EcsManagementClientException;
    void createNamespace(Connection connection, String namespace,
                String namespaceAdmins, String replicationGroup)
            throws EcsManagementClientException;
    void createNamespace(Connection connection, NamespaceCreate createParam)
            throws EcsManagementClientException;
    void deleteNamespace(Connection connection, String namespace)
            throws EcsManagementClientException;
    NamespaceInfo getNamespaceInfo(Connection connection, String namespace)
            throws EcsManagementClientException;
    void updateNamespace(Connection connection, String namespace, NamespaceUpdate updateParam)
            throws EcsManagementClientException;

    // NamespaceQuotaActions
    void createNamespaceQuota(Connection connection, String namespace, NamespaceQuotaParam createParam)
            throws EcsManagementClientException;
    NamespaceQuotaDetails getNamespaceQuotaDetails(Connection connection, String namespace)
            throws EcsManagementClientException;
    void deleteNamespaceQuota(Connection connection, String namespace)
            throws EcsManagementClientException;

    // NamespaceRetentionActions
    Boolean namespaceRetentionExists(Connection connection, String namespace, String retentionClass)
            throws EcsManagementClientException;
    void createNamespaceRetention(Connection connection, String namespace, RetentionClassCreate createParam)
            throws EcsManagementClientException;
    void deleteNamespaceRetention(Connection connection, String namespace, String retentionClass)
            throws EcsManagementClientException;
    RetentionClassDetails getNamespaceRetention(Connection connection, String namespace, String retentionClass)
            throws EcsManagementClientException;
    void updateNamespaceRetention(Connection connection, String namespace,
                String retentionClass, RetentionClassUpdate retentionClassUpdate)
            throws EcsManagementClientException;

    // BaseUrlActions
    List<BaseUrl> listBaseUrl(Connection connection)
            throws EcsManagementClientException;
    BaseUrlInfo getBaseUrlInfo(Connection connection, String id)
            throws EcsManagementClientException;

    // NFSExportActions
    List<NFSExport> listNFSExport(Connection connection, String pathPrefix)
            throws EcsManagementClientException;
    void createNFSExport(Connection connection, String exportPath)
            throws EcsManagementClientException;
    void deleteNFSExport(Connection connection, int exportId)
            throws EcsManagementClientException;
}
