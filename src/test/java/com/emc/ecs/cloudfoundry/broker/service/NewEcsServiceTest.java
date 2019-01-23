package com.emc.ecs.cloudfoundry.broker.service;

import com.emc.ecs.cloudfoundry.broker.config.BrokerConfig;
import com.emc.ecs.cloudfoundry.broker.config.CatalogConfig;
import com.emc.ecs.cloudfoundry.broker.model.PlanProxy;
import com.emc.ecs.cloudfoundry.broker.model.ServiceDefinitionProxy;
import com.emc.ecs.management.sdk.*;
import com.emc.ecs.management.sdk.model.*;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emc.ecs.common.Fixtures.*;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;


@RunWith(Ginkgo4jSpringRunner.class)
public class NewEcsServiceTest {
    private static final String FOO = "foo";
    private static final String ONE_YEAR = "one-year";
    private static final int ONE_YEAR_IN_SECS = 31536000;
    private static final String BASE_URL = "base-url";
    private static final String USE_SSL = "use-ssl";
    private static final String USER1 = "user1";
    private static final String EXISTS = "bucketExists";
    private static final String DEFAULT_BUCKET_QUOTA = "default-bucket-quota";
    private static final String DOMAIN_GROUP_ADMINS = "domain-group-admins";
    private static final String ACCESS_DURING_OUTAGE = "access-during-outage";
    private static final String ENCRYPTED = "encrypted";
    private static final String REPOSITORY = "repository";
    private static final String USER = "user";
    private static final String WARN = "warn";
    private static final String QUOTA = "quota";
    private static final String LIMIT = "limit";
    private static final String DOT = ".";
    private static final String HTTPS = "https://";
    private static final int THIRTY_DAYS_IN_SEC = 2592000;
    private static final String HTTP = "http://";
    private static final String _9020 = ":9020";
    private static final String _9021 = ":9021";
    private static final String RETENTION = "retention";
    private static final String THIRTY_DAYS = "thirty-days";
    private static final String UPDATE = "update";
    private static final String CREATE = "create";
    private static final String DELETE = "delete";
    public static final String COMPLIANCE_ENABLED = "compliance-enabled";
    public static final String DEFAULT_RETENTION = "default-retention";

    @Mock
    private Connection connection;

    @Mock
    private CatalogConfig catalogConfig;

    @Mock
    private BrokerConfig broker;

    @Mock
    private ECSManagementClient ecsManagementClient;

    @Autowired
    @InjectMocks
    private EcsService ecs;



    {
        Describe("EcsService", () -> {
            BeforeEach(() -> {
                doReturn(NAMESPACE).when(broker).getNamespace();

                // ecs = mock(EcsService.class);
                // connection = mock(Connection.class);
                // catalogConfig = mock(CatalogConfig.class);
//                 when(brokerConfig.getNamespace()).thenReturn(NAMESPACE);
//                when(brokerConfig.getNamespace()).thenReturn(NAMESPACE);
                // ecsManagementClient = mock(ECSManagementClient.class);
            });

            Context("Bucket Operations", () -> {


                BeforeEach(()-> {

                    doNothing().when(ecsManagementClient)
                            .createBucket(same(connection), any(ObjectBucketCreate.class));
                });

                Context("when the bucket exists", () -> {
                    BeforeEach(()-> {
//                        when(ecsManagementClient.bucketExists(same(BUCKET_NAME), same(NAMESPACE))).thenReturn(true);
                        doReturn(true).when(ecsManagementClient)
                                .bucketExists(same(connection), same(BUCKET_NAME), same(NAMESPACE));
                    });
                });

                Context("when the bucket doesn't exist", () -> {
                    BeforeEach(()-> {
//                        when(ecsManagementClient.bucketExists(same(BUCKET_NAME), same(NAMESPACE))).thenReturn(false);
                        doReturn(false).when(ecsManagementClient)
                                .bucketExists(same(connection), same(BUCKET_NAME), same(NAMESPACE));
                        doReturn("ecs-cf-broker-").when(broker).getPrefix();
                    });

                    It("should create the bucket", () -> {
                        ServiceDefinitionProxy serviceDefinitionProxy = bucketServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(BUCKET_PLAN_ID1);

                        Map<String, Object> params = new HashMap<>();
                        Map<String, Object> serviceSettings =
                                ecs.createBucket(BUCKET_NAME, serviceDefinitionProxy, planProxy, params);

                        ArgumentCaptor<ObjectBucketCreate> createCaptor = ArgumentCaptor
                                .forClass(ObjectBucketCreate.class);

                        verify(ecsManagementClient).createBucket(same(connection), createCaptor.capture());

                        ObjectBucketCreate objectBucketCreate = createCaptor.getValue();
                        assertEquals(PREFIX + BUCKET_NAME, objectBucketCreate.getName());
                        assertNull(objectBucketCreate.getIsEncryptionEnabled());
                        assertNull(objectBucketCreate.getIsStaleAllowed());
                        assertEquals(NAMESPACE, objectBucketCreate.getNamespace());
                    });

                    It("should creat the bucket without parameters", () -> {
                        ServiceDefinitionProxy serviceDefinitionProxy = bucketServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(BUCKET_PLAN_ID2);

                        when(catalogConfig.findServiceDefinition(BUCKET_SERVICE_ID))
                                .thenReturn(serviceDefinitionProxy);

                        Map<String, Object> serviceSettings =
                                ecs.createBucket(BUCKET_NAME, serviceDefinitionProxy, planProxy, null);

                        assertTrue((Boolean) serviceSettings.get(ENCRYPTED));
                        assertTrue((Boolean) serviceSettings.get(ACCESS_DURING_OUTAGE));
                        assertTrue((Boolean) serviceSettings.get(FILE_ACCESSIBLE));
                        assertNull(serviceSettings.get(QUOTA));

                        ArgumentCaptor<ObjectBucketCreate> createCaptor = ArgumentCaptor
                                .forClass(ObjectBucketCreate.class);

                        verify(ecsManagementClient).createBucket(same(connection), createCaptor.capture());

                        ObjectBucketCreate objectBucketCreate = createCaptor.getValue();

                        assertEquals(PREFIX + BUCKET_NAME, objectBucketCreate.getName());
                        assertEquals(NAMESPACE, objectBucketCreate.getNamespace());
                        assertTrue(objectBucketCreate.getIsEncryptionEnabled());
                        assertTrue(objectBucketCreate.getIsStaleAllowed());
                        assertTrue(objectBucketCreate.getFilesystemEnabled());
                        assertEquals("s3", objectBucketCreate.getHeadType());
                    });

                    It("should create the bucket with parameters", () -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put(ENCRYPTED, true);
                        params.put(ACCESS_DURING_OUTAGE, true);
                        Map<String, Object> quota = new HashMap<>();
                        quota.put(WARN, 9);
                        quota.put(LIMIT, 10);
                        params.put(QUOTA, quota);
                        params.put(FILE_ACCESSIBLE, true);

                        ServiceDefinitionProxy serviceDefinitionProxy = bucketServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(BUCKET_PLAN_ID1);

                        Map<String, Object> serviceSettings = ecs.createBucket(
                                BUCKET_NAME,
                                serviceDefinitionProxy,
                                planProxy,
                                params);

                        Map<String, Integer> returnQuota = (Map<String, Integer>) serviceSettings.get(QUOTA);

                        assertEquals(4, returnQuota.get(WARN).longValue());
                        assertEquals(5, returnQuota.get(LIMIT).longValue());
                        assertTrue((Boolean) serviceSettings.get(ENCRYPTED));
                        assertTrue((Boolean) serviceSettings.get(ACCESS_DURING_OUTAGE));
                        assertTrue((Boolean) serviceSettings.get(FILE_ACCESSIBLE));

                        ArgumentCaptor<ObjectBucketCreate> createCaptor = ArgumentCaptor
                                .forClass(ObjectBucketCreate.class);

                        verify(ecsManagementClient, times(1));
                        ecsManagementClient.createBucket(same(connection), createCaptor.capture());

                        ObjectBucketCreate create = createCaptor.getValue();
                        assertEquals(PREFIX + BUCKET_NAME, create.getName());
                        assertTrue(create.getIsEncryptionEnabled());
                        assertTrue(create.getIsStaleAllowed());
                        assertTrue(create.getFilesystemEnabled());
                        assertEquals(NAMESPACE, create.getNamespace());

                        verify(ecsManagementClient, times(1));
                        ecsManagementClient.createBucketQuota(same(connection), eq(PREFIX + BUCKET_NAME),
                                eq(NAMESPACE), eq(5), eq(4));
                    });

                    It("should create a file-enabled bucket", () -> {
                        ObjectBucketInfo fakeBucket = new ObjectBucketInfo();

                        doReturn(fakeBucket).when(ecsManagementClient)
                                .getBucketInfo(same(connection), anyString(), anyString());
                        boolean isEnabled = ecs.getBucketFileEnabled(FOO);
                        assertEquals(false, isEnabled);

                        fakeBucket.setFsAccessEnabled(true);
                        isEnabled = ecs.getBucketFileEnabled(FOO);
                        assertEquals(true, isEnabled);
                    });

                    It("should change bucket plan to no quota", () -> {
                        ServiceDefinitionProxy serviceDefinitionProxy = bucketServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(BUCKET_PLAN_ID2);

                        Map<String, Object> serviceSettings = ecs.changeBucketPlan(
                                BUCKET_NAME,
                                serviceDefinitionProxy,
                                planProxy,
                                new HashMap<>());

                        assertNull(serviceSettings.get(QUOTA));

                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);

                        verify(ecsManagementClient, times(1));
                        ecsManagementClient.deleteBucketQuota(
                                same(connection),
                                idCaptor.capture(),
                                nsCaptor.capture());

                        assertEquals(PREFIX + BUCKET_NAME, idCaptor.getValue());
                        assertEquals(NAMESPACE, nsCaptor.getValue());
                    });

                    It("should change the quota parameter", () -> {
                        ServiceDefinitionProxy serviceDefinitionProxy = bucketServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(BUCKET_PLAN_ID2);

                        Map<String, Object> quota = new HashMap<>();
                        quota.put(LIMIT, 100);
                        quota.put(WARN, 80);
                        Map<String, Object> params = new HashMap<>();
                        params.put(QUOTA, quota);

                        Map<String, Object> serviceSettings = ecs.changeBucketPlan(
                                BUCKET_NAME,
                                serviceDefinitionProxy,
                                planProxy,
                                params);

                        Map<String, Integer> returnQuota = (Map<String, Integer>) serviceSettings.get(QUOTA);
                        assertEquals(80, returnQuota.get(WARN).longValue());
                        assertEquals(100, returnQuota.get(LIMIT).longValue());

                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor
                                .forClass(Integer.class);
                        ArgumentCaptor<Integer> warnCaptor = ArgumentCaptor
                                .forClass(Integer.class);

                        verify(ecsManagementClient, times(1));
                        ecsManagementClient.createBucketQuota(
                                same(connection),
                                idCaptor.capture(),
                                nsCaptor.capture(),
                                limitCaptor.capture(),
                                warnCaptor.capture());

                        assertEquals(PREFIX + BUCKET_NAME, idCaptor.getValue());
                        assertEquals(NAMESPACE, nsCaptor.getValue());
                        assertEquals(Integer.valueOf(100), limitCaptor.getValue());
                        assertEquals(Integer.valueOf(80), warnCaptor.getValue());
                    });

                    It("should ignore quota parameters when quota parameters are supplied", () -> {
                        ServiceDefinitionProxy serviceDefinitionProxy = bucketServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(BUCKET_PLAN_ID1);

                        Map<String, Object> quota = new HashMap<>();
                        quota.put(LIMIT, 100);
                        quota.put(WARN, 80);
                        Map<String, Object> params = new HashMap<>();
                        params.put(QUOTA, quota);

                        Map<String, Object> serviceSettings = ecs.changeBucketPlan(
                                BUCKET_NAME,
                                serviceDefinitionProxy,
                                planProxy,
                                params);

                        Map<String, Integer> returnQuota = (Map<String, Integer>) serviceSettings.get(QUOTA);
                        assertEquals(4, returnQuota.get(WARN).longValue());
                        assertEquals(5, returnQuota.get(LIMIT).longValue());

                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor
                                .forClass(Integer.class);
                        ArgumentCaptor<Integer> warnCaptor = ArgumentCaptor
                                .forClass(Integer.class);

                        verify(ecsManagementClient, times(1));
                        ecsManagementClient.createBucketQuota(
                                same(connection),
                                idCaptor.capture(),
                                nsCaptor.capture(),
                                limitCaptor.capture(),
                                warnCaptor.capture());

                        assertEquals(PREFIX + BUCKET_NAME, idCaptor.getValue());
                        assertEquals(NAMESPACE, nsCaptor.getValue());
                        assertEquals(Integer.valueOf(5), limitCaptor.getValue());
                        assertEquals(Integer.valueOf(4), warnCaptor.getValue());
                    });

                    It("should create quota on bucket with no quotas", () -> {
                        ServiceDefinitionProxy serviceDefinitionProxy = bucketServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(BUCKET_PLAN_ID1);

                        Map<String, Object> serviceSettings = ecs.changeBucketPlan(
                                BUCKET_NAME,
                                serviceDefinitionProxy,
                                planProxy,
                                new HashMap<>());

                        Map<String, Integer> quota = (Map<String, Integer>) serviceSettings.get(QUOTA);
                        assertEquals(4, quota.get(WARN).longValue());
                        assertEquals(5, quota.get(LIMIT).longValue());

                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor
                                .forClass(Integer.class);
                        ArgumentCaptor<Integer> warnCaptor = ArgumentCaptor
                                .forClass(Integer.class);

                        verify(ecsManagementClient, times(1));
                        ecsManagementClient.createBucketQuota(
                                same(connection),
                                idCaptor.capture(),
                                nsCaptor.capture(),
                                limitCaptor.capture(),
                                warnCaptor.capture());

                        assertEquals(PREFIX + BUCKET_NAME, idCaptor.getValue());
                        assertEquals(NAMESPACE, nsCaptor.getValue());
                        assertEquals(Integer.valueOf(5), limitCaptor.getValue());
                        assertEquals(Integer.valueOf(4), warnCaptor.getValue());
                    });

                    It("should remove user from the bucket", () -> {
                        BucketAcl bucketAcl = new BucketAcl();
                        BucketUserAcl userAcl = new BucketUserAcl(PREFIX + USER1,
                                Collections.singletonList("full_control"));
                        BucketAclAcl acl = new BucketAclAcl();
                        acl.setUserAccessList(Collections.singletonList(userAcl));
                        bucketAcl.setAcl(acl);

                        doReturn(bucketAcl).when(ecsManagementClient).getBucketAcl(
                                same(connection),
                                eq(PREFIX + BUCKET_NAME),
                                eq(NAMESPACE));

                        doNothing().when(ecsManagementClient).updateBucketAcl(
                                same(connection),
                                eq(PREFIX + BUCKET_NAME),
                                any(BucketAcl.class));

                        ecs.removeUserFromBucket(BUCKET_NAME, USER1);

                        verify(ecsManagementClient);

                        ecsManagementClient.getBucketAcl(
                                eq(connection),
                                eq(PREFIX + BUCKET_NAME),
                                eq(NAMESPACE));

                        ArgumentCaptor<BucketAcl> aclCaptor = ArgumentCaptor
                                .forClass(BucketAcl.class);

                        verify(ecsManagementClient);

                        ecsManagementClient.updateBucketAcl(
                                eq(connection),
                                eq(PREFIX + BUCKET_NAME),
                                aclCaptor.capture());

                        List<BucketUserAcl> actualUserAcl = aclCaptor.getValue().getAcl()
                                .getUserAccessList();
                        assertFalse(actualUserAcl.contains(userAcl));
                    });

                    It("should be able to delete a user from a service", () -> {
                        ecs.deleteUser(USER1);
                        verify(ecsManagementClient);
                        ecsManagementClient.deleteObjectUser(
                                same(connection),
                                eq(PREFIX + USER1));
                    });

                    It("should create a bucket with retention", () -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put(DEFAULT_RETENTION, THIRTY_DAYS_IN_SEC);

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.getPlans().get(2);

                        when(catalogConfig.findServiceDefinition(NAMESPACE_SERVICE_ID))
                                .thenReturn(namespaceServiceFixture());

                        Map<String, Object> serviceSettings = ecs.createBucket(BUCKET_NAME,
                                serviceDefinitionProxy, planProxy, params);
                        assertEquals(THIRTY_DAYS_IN_SEC, serviceSettings.get(DEFAULT_RETENTION));

                        verify(ecsManagementClient);
                        ecsManagementClient.updateBucketRetention(same(connection), eq(NAMESPACE),
                                eq(PREFIX + BUCKET_NAME), eq(THIRTY_DAYS_IN_SEC));
                    });
                });
            });
            Context("Namespace Operations", () -> {
                Context("when the namespace exists", () -> {

                });
                Context("when the namespace does not exist", () -> {
                    BeforeEach(()-> {
                        doReturn(false).when(ecsManagementClient).namespaceExists(
                                same(connection),
                                same(NAMESPACE));

                        doReturn("ecs-cf-broker-").when(broker).getPrefix();
                    });

                    It("should create a default namespace", () -> {
                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.getPlans().get(0);

                        Map<String, Object> params = new HashMap<>();
                        Map<String, Object> serviceSettings = ecs.createNamespace(
                                NAMESPACE,
                                namespaceServiceFixture(),
                                planProxy,
                                params);

                        Map<String, Integer> quota = (Map<String, Integer>) serviceSettings.get(QUOTA);
                        assertEquals(4, quota.get(WARN).longValue());
                        assertEquals(5, quota.get(LIMIT).longValue());

                        verify(ecsManagementClient);

                        ArgumentCaptor<NamespaceCreate> createCaptor = ArgumentCaptor
                                .forClass(NamespaceCreate.class);
                        ecsManagementClient.createNamespace(same(connection), createCaptor.capture());
                        NamespaceCreate create = createCaptor.getValue();

                        assertEquals(PREFIX + NAMESPACE, create.getNamespace());
                        assertNull(create.getIsEncryptionEnabled());
                        assertNull(create.getIsComplianceEnabled());
                        assertNull(create.getIsStaleAllowed());
                        assertEquals(Integer.valueOf(5), create.getDefaultBucketBlockSize());

                        verify(ecsManagementClient);

                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<NamespaceQuotaParam> quotaParamCaptor = ArgumentCaptor
                                .forClass(NamespaceQuotaParam.class);
                        ecsManagementClient.createNamespaceQuota(
                                same(connection),
                                idCaptor.capture(),
                                quotaParamCaptor.capture());

                        assertEquals(PREFIX + NAMESPACE, idCaptor.getValue());
                        assertEquals(5, quotaParamCaptor.getValue().getBlockSize());
                        assertEquals(4, quotaParamCaptor.getValue().getNotificationSize());
                    });

                    It("should change the namespace plan", () -> {
                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<NamespaceUpdate> updateCaptor = ArgumentCaptor
                                .forClass(NamespaceUpdate.class);

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy =  serviceDefinitionProxy.findPlan(NAMESPACE_PLAN_ID2);
                        Map<String, Object> params = new HashMap<>();
                        Map<String, Object> serviceSettings = ecs.changeNamespacePlan(
                                NAMESPACE,
                                serviceDefinitionProxy,
                                planProxy,
                                params);

                        assertNull(serviceSettings.get(QUOTA));
                        verify(ecsManagementClient);

                        ecsManagementClient.updateNamespace(
                                same(connection),
                                idCaptor.capture(),
                                updateCaptor.capture());

                        assertEquals(PREFIX + NAMESPACE, idCaptor.getValue());
                        NamespaceUpdate update = updateCaptor.getValue();
                        assertEquals(EXTERNAL_ADMIN, update.getExternalGroupAdmins());
                        assertTrue(update.getIsEncryptionEnabled());
                        assertTrue(update.getIsComplianceEnabled());
                        assertTrue(update.getIsStaleAllowed());
                    });

                    It("should create a namespace with no parameters", () -> {
                        ArgumentCaptor<NamespaceCreate> createCaptor = ArgumentCaptor
                                .forClass(NamespaceCreate.class);

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.getPlans().get(0);
                        doReturn(serviceDefinitionProxy).when(catalogConfig)
                                .findServiceDefinition(NAMESPACE_SERVICE_ID);

                        Map<String, Object> serviceSettings = ecs.createNamespace(
                                NAMESPACE,
                                serviceDefinitionProxy,
                                planProxy,
                                new HashMap<>());

                        Map<String, Integer> quota = (Map<String, Integer>) serviceSettings.get(QUOTA);
                        assertEquals(4, quota.get(WARN).longValue());
                        assertEquals(5, quota.get(LIMIT).longValue());

                        verify(ecsManagementClient);

                        ecsManagementClient.createNamespace(same(connection), createCaptor.capture());
                        NamespaceCreate create = createCaptor.getValue();

                        assertEquals(PREFIX + NAMESPACE, create.getNamespace());
                        assertEquals(null, create.getExternalGroupAdmins());
                        assertEquals(null, create.getIsEncryptionEnabled());
                        assertEquals(null, create.getIsComplianceEnabled());
                        assertEquals(null, create.getIsStaleAllowed());
                        assertEquals(Integer.valueOf(5), create.getDefaultBucketBlockSize());

                        verify(ecsManagementClient);
                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<NamespaceQuotaParam> quotaParamCaptor = ArgumentCaptor
                                .forClass(NamespaceQuotaParam.class);
                        ecsManagementClient.createNamespaceQuota(
                                same(connection),
                                idCaptor.capture(),
                                quotaParamCaptor.capture());

                        assertEquals(PREFIX + NAMESPACE, idCaptor.getValue());
                        assertEquals(5, quotaParamCaptor.getValue().getBlockSize());
                        assertEquals(4, quotaParamCaptor.getValue().getNotificationSize());
                    });

                    It("should create a namespace with parameters", () -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put(DOMAIN_GROUP_ADMINS, EXTERNAL_ADMIN);
                        params.put(ENCRYPTED, true);
                        params.put(COMPLIANCE_ENABLED, true);
                        params.put(ACCESS_DURING_OUTAGE, true);
                        params.put(DEFAULT_BUCKET_QUOTA, 10);

                        ArgumentCaptor<NamespaceCreate> createCaptor = ArgumentCaptor
                                .forClass(NamespaceCreate.class);

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.getPlans().get(0);
                        doReturn(serviceDefinitionProxy).when(catalogConfig)
                                .findServiceDefinition(NAMESPACE_SERVICE_ID);

                        Map<String, Object> serviceSettings = ecs.createNamespace(
                                NAMESPACE,
                                serviceDefinitionProxy,
                                planProxy,
                                params);

                        Map<String, Integer> quota = (Map<String, Integer>) serviceSettings.get(QUOTA);
                        assertTrue((Boolean) serviceSettings.get(ENCRYPTED));
                        assertTrue((Boolean) serviceSettings.get(COMPLIANCE_ENABLED));
                        assertTrue((Boolean) serviceSettings.get(ACCESS_DURING_OUTAGE));
                        assertEquals(5, serviceSettings.get(DEFAULT_BUCKET_QUOTA));
                        assertEquals(4, quota.get(WARN).longValue());
                        assertEquals(5, quota.get(LIMIT).longValue());

                        verify(ecsManagementClient);

                        ecsManagementClient.createNamespace(same(connection), createCaptor.capture());
                        NamespaceCreate create = createCaptor.getValue();

                        assertEquals(PREFIX + NAMESPACE, create.getNamespace());
                        assertEquals(EXTERNAL_ADMIN, create.getExternalGroupAdmins());
                        assertTrue(create.getIsEncryptionEnabled());
                        assertTrue(create.getIsComplianceEnabled());
                        assertTrue(create.getIsStaleAllowed());
                        assertEquals(Integer.valueOf(5), create.getDefaultBucketBlockSize());

                        verify(ecsManagementClient);

                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<NamespaceQuotaParam> quotaParamCaptor = ArgumentCaptor
                                .forClass(NamespaceQuotaParam.class);
                        ecsManagementClient.createNamespaceQuota(
                                same(connection),
                                idCaptor.capture(),
                                quotaParamCaptor.capture());

                        assertEquals(PREFIX + NAMESPACE, idCaptor.getValue());
                        assertEquals(5, quotaParamCaptor.getValue().getBlockSize());
                        assertEquals(4, quotaParamCaptor.getValue().getNotificationSize());
                    });

                    It("should change namespace plan with parameters", () -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put(DOMAIN_GROUP_ADMINS, EXTERNAL_ADMIN);
                        params.put(ENCRYPTED, true);
                        params.put(COMPLIANCE_ENABLED, true);
                        params.put(ACCESS_DURING_OUTAGE, true);
                        params.put(DEFAULT_BUCKET_QUOTA, 10);

                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<NamespaceUpdate> updateCaptor = ArgumentCaptor
                                .forClass(NamespaceUpdate.class);

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(NAMESPACE_PLAN_ID1);
                        Map<String, Object> serviceSettings = ecs.changeNamespacePlan(
                                NAMESPACE,
                                serviceDefinitionProxy,
                                planProxy,
                                params);

                        Map<String, Integer> quota = (Map<String, Integer>) serviceSettings.get(QUOTA);
                        assertTrue((Boolean) serviceSettings.get(ENCRYPTED));
                        assertTrue((Boolean) serviceSettings.get(ACCESS_DURING_OUTAGE));
                        assertTrue((Boolean) serviceSettings.get(COMPLIANCE_ENABLED));
                        assertEquals(EXTERNAL_ADMIN, serviceSettings.get(DOMAIN_GROUP_ADMINS));
                        assertEquals(5, serviceSettings.get(DEFAULT_BUCKET_QUOTA));
                        assertEquals(4, quota.get(WARN).longValue());
                        assertEquals(5, quota.get(LIMIT).longValue());

                        verify(ecsManagementClient);
                        ecsManagementClient.updateNamespace(
                                same(connection),
                                idCaptor.capture(),
                                updateCaptor.capture());

                        NamespaceUpdate update = updateCaptor.getValue();
                        assertEquals(PREFIX + NAMESPACE, idCaptor.getValue());
                        assertEquals(EXTERNAL_ADMIN, update.getExternalGroupAdmins());
                        assertTrue(update.getIsEncryptionEnabled());
                        assertTrue(update.getIsComplianceEnabled());
                        assertTrue(update.getIsStaleAllowed());
                        assertEquals(Integer.valueOf(5), update.getDefaultBucketBlockSize());
                    });

                    It("should create a namespace with retention enabled", () -> {
                        Map<String, Object> params = new HashMap<>();

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.getPlans().get(2);

                        doReturn(namespaceServiceFixture()).when(catalogConfig)
                                .findServiceDefinition(NAMESPACE_SERVICE_ID);

                        Map<String, Object> serviceSettings = ecs.createNamespace(NAMESPACE,
                                serviceDefinitionProxy,
                                planProxy,
                                params);

                        Map<String, Object> returnRetention = (Map<String, Object>) serviceSettings.get(RETENTION);
                        assertEquals(ONE_YEAR_IN_SECS, returnRetention.get(ONE_YEAR));

                        verify(ecsManagementClient);
                        ArgumentCaptor<NamespaceCreate> createCaptor = ArgumentCaptor
                                .forClass(NamespaceCreate.class);
                        ecsManagementClient.createNamespace(same(connection), createCaptor.capture());
                        NamespaceCreate create = createCaptor.getValue();
                        assertEquals(PREFIX + NAMESPACE, create.getNamespace());
                        assertTrue(create.getIsEncryptionEnabled());
                        assertTrue(create.getIsStaleAllowed());
                        assertTrue(create.getIsComplianceEnabled());

                        verify(ecsManagementClient);
                        ArgumentCaptor<RetentionClassCreate> retentionCreateCaptor = ArgumentCaptor
                                .forClass(RetentionClassCreate.class);
                        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);

                        ecsManagementClient.createNamespaceRetention(
                                same(connection),
                                idCaptor.capture(),
                                retentionCreateCaptor.capture());

                        RetentionClassCreate retention = retentionCreateCaptor.getValue();
                        assertEquals(PREFIX + NAMESPACE, idCaptor.getValue());
                        assertEquals(ONE_YEAR, retention.getName());
                        assertEquals(ONE_YEAR_IN_SECS, retention.getPeriod());
                    });

                    /**
                     * When changing a namespace plan and a retention-class is specified in the
                     * parameters with the value -1, then the retention class should be removed.
                     */
                    It("should change the namespace plan retention plan", () -> {
                        Map<String, Object> retention = new HashMap<>();
                        retention.put(THIRTY_DAYS, THIRTY_DAYS_IN_SEC);
                        Map<String, Object> params = new HashMap<>();
                        params.put(RETENTION, retention);

                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<RetentionClassCreate> createCaptor = ArgumentCaptor
                                .forClass(RetentionClassCreate.class);

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(NAMESPACE_PLAN_ID2);
                        Map<String, Object> serviceSettings = ecs.changeNamespacePlan(NAMESPACE,
                                serviceDefinitionProxy,
                                planProxy,
                                params);

                        Map<String, Object> returnRetention = (Map<String, Object>) serviceSettings.get(RETENTION);
                        assertEquals(THIRTY_DAYS_IN_SEC, returnRetention.get(THIRTY_DAYS));


                        verify(ecsManagementClient);
                        ecsManagementClient.createNamespaceRetention(
                                same(connection),
                                nsCaptor.capture(),
                                createCaptor.capture());

                        assertEquals(PREFIX + NAMESPACE, nsCaptor.getValue());
                        assertEquals(THIRTY_DAYS, createCaptor.getValue().getName());
                        assertEquals(THIRTY_DAYS_IN_SEC, createCaptor.getValue().getPeriod());
                    });

                    /**
                     * When changing a namespace plan and a retention-class is specified in the
                     * parameters with a different value than the existing value, then the
                     * retention-class should be changed.
                     */
                    It("should remove the namespace retention plan", () -> {
                        Map<String, Object> retention = new HashMap<>();
                        retention.put(THIRTY_DAYS, -1);
                        Map<String, Object> params = new HashMap<>();
                        params.put(RETENTION, retention);

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(NAMESPACE_PLAN_ID2);
                        Map<String, Object> serviceSettings = ecs.changeNamespacePlan(
                                NAMESPACE, serviceDefinitionProxy, planProxy, params);
                        assertNull(serviceSettings.get(RETENTION));

                        verify(ecsManagementClient);
                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<String> rcCaptor = ArgumentCaptor.forClass(String.class);
                        ecsManagementClient.deleteNamespaceRetention(same(connection),
                                nsCaptor.capture(), rcCaptor.capture());
                        assertEquals(PREFIX + NAMESPACE, nsCaptor.getValue());
                        assertEquals(THIRTY_DAYS, rcCaptor.getValue());
                    });


                    It("should change retention-class when presented with new parameters", () -> {
                        Map<String, Object> retention = new HashMap<>();
                        retention.put(THIRTY_DAYS, THIRTY_DAYS_IN_SEC);
                        Map<String, Object> params = new HashMap<>();
                        params.put(RETENTION, retention);

                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<String> rcCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<RetentionClassUpdate> updateCaptor = ArgumentCaptor
                                .forClass(RetentionClassUpdate.class);

                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(NAMESPACE_PLAN_ID2);
                        Map<String, Object> serviceSettings = ecs.changeNamespacePlan(NAMESPACE,
                                serviceDefinitionProxy, planProxy, params);
                        Map<String, Object> returnRetention = (Map<String, Object>) serviceSettings.get(RETENTION);
                        assertEquals(THIRTY_DAYS_IN_SEC, returnRetention.get(THIRTY_DAYS));

                        verify(ecsManagementClient);
                        ecsManagementClient.updateNamespaceRetention(same(connection),
                                nsCaptor.capture(), rcCaptor.capture(), updateCaptor.capture());
                        assertEquals(PREFIX + NAMESPACE, nsCaptor.getValue());
                        assertEquals(THIRTY_DAYS, rcCaptor.getValue());
                        assertEquals(THIRTY_DAYS_IN_SEC, updateCaptor.getValue().getPeriod());
                    });

                    It("should delete namespace", () -> {
                        ecs.deleteNamespace(NAMESPACE);

                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);
                        verify(ecsManagementClient);
                        ecsManagementClient.deleteNamespace(same(connection), nsCaptor.capture());
                        assertEquals(PREFIX + NAMESPACE, nsCaptor.getValue());
                    });

                    It("should create a user within specific namespace", () -> {
                        doNothing().when(ecsManagementClient).createObjectUser(same(connection),
                                anyString(), anyString());

                        doReturn(new UserSecretKey()).when(ecsManagementClient).createObjectUserSecretKey(
                                same(connection), anyString());

                        doReturn(Collections.singletonList(new UserSecretKey()))
                                .when(ecsManagementClient).listObjectUserSecretKey(same(connection), anyString());

                        ecs.createUser(USER1, NAMESPACE);

                        ArgumentCaptor<String> nsCaptor = ArgumentCaptor.forClass(String.class);
                        ArgumentCaptor<String> userCaptor = ArgumentCaptor
                                .forClass(String.class);

                        verify(ecsManagementClient);
                        ecsManagementClient.createObjectUser(same(connection), userCaptor.capture(),
                                nsCaptor.capture());
                        assertEquals(PREFIX + NAMESPACE, nsCaptor.getValue());
                        assertEquals(PREFIX + USER1, userCaptor.getValue());
                    });

                    It("should be able to lookup a service definition from the catalog", () -> {
                        doReturn(namespaceServiceFixture()).when(catalogConfig)
                                .findServiceDefinition(NAMESPACE_SERVICE_ID);
                        ServiceDefinitionProxy service = ecs
                                .lookupServiceDefinition(NAMESPACE_SERVICE_ID);
                        assertEquals(NAMESPACE_SERVICE_ID, service.getId());
                    });

                    /*
                    * Catch expected exception
                    * */
                    It("should throw exception - lookup of a non-existent service definition ID", () -> {
                        try {
                            ecs.lookupServiceDefinition(NAMESPACE_SERVICE_ID);
                        } catch (ServiceBrokerException e) {
                            assert (e.getMessage().contains("No service matching service id"));
                        }
                    });

                    It("should add a non-existent export to a bucket", () -> {
                        String absolutePath = "/" + NAMESPACE + "/" + PREFIX + BUCKET_NAME + "/" + EXPORT_NAME;

                        doReturn(null).when(ecsManagementClient)
                                .listNFSExport(same(connection), eq(absolutePath));

                        doNothing().when(ecsManagementClient).createNFSExport(
                                same(connection), eq(absolutePath));

                        ecs.addExportToBucket(BUCKET_NAME, EXPORT_NAME);

                        ArgumentCaptor<String> listPathCaptor = ArgumentCaptor.forClass(String.class);
                        verify(ecsManagementClient);
                        ecsManagementClient.listNFSExport(same(connection), listPathCaptor.capture());
                        assertEquals(absolutePath, listPathCaptor.getValue());

                        ArgumentCaptor<String> createPathCaptor = ArgumentCaptor.forClass(String.class);
                        verify(ecsManagementClient);
                        ecsManagementClient.createNFSExport(same(connection), createPathCaptor.capture());
                        assertEquals(absolutePath, createPathCaptor.getValue());
                    });

                    It("should add a null export to a bucket", () -> {
                        String absolutePath = "/" + NAMESPACE + "/" + PREFIX + BUCKET_NAME + "/";

                        doReturn(null).when(ecsManagementClient)
                                .listNFSExport(same(connection), eq(absolutePath));

                        doNothing().when(ecsManagementClient).createNFSExport(
                                same(connection), eq(absolutePath));

                        ecs.addExportToBucket(BUCKET_NAME, null);

                        ArgumentCaptor<String> listPathCaptor = ArgumentCaptor.forClass(String.class);
                        verify(ecsManagementClient);
                        ecsManagementClient.listNFSExport(same(connection), listPathCaptor.capture());
                        assertEquals(absolutePath, listPathCaptor.getValue());

                        ArgumentCaptor<String> createPathCaptor = ArgumentCaptor.forClass(String.class);
                        verify(ecsManagementClient);
                        ecsManagementClient.createNFSExport(same(connection), createPathCaptor.capture());
                        assertEquals(absolutePath, createPathCaptor.getValue());
                    });
                });
            });
            Context("BaseURL operations", () -> {
                Context("when there is no namespace in host", () -> {
                    BeforeEach(() -> {
                        mock(BaseUrlAction.class);
                        BaseUrl baseUrl = new BaseUrl();
                        baseUrl.setId(BASE_URL_ID);
                        baseUrl.setName(BASE_URL_NAME);
                        doReturn(Collections.singletonList(baseUrl)).when(ecsManagementClient)
                                .listBaseUrl(same(connection));

                        BaseUrlInfo baseUrlInfo = new BaseUrlInfo();
                        baseUrlInfo.setId(BASE_URL_ID);
                        baseUrlInfo.setName(BASE_URL_NAME);
                        baseUrlInfo.setNamespaceInHost(false);
                        baseUrlInfo.setBaseurl(BASE_URL);
                        doReturn(baseUrlInfo).when(ecsManagementClient)
                                .getBaseUrlInfo(connection, BASE_URL_ID);
                    });

                    It("should use the default base url", () -> {
                        ServiceDefinitionProxy serviceDefinitionProxy = namespaceServiceFixture();
                        PlanProxy planProxy = serviceDefinitionProxy.findPlan(NAMESPACE_PLAN_ID1);

                        doReturn(DEFAULT_BASE_URL_NAME).when(broker).getBaseUrl();

                        String expectedUrl = HTTP + NAMESPACE + DOT + BASE_URL + _9020;
                        assertEquals(expectedUrl, ecs.getNamespaceURL(NAMESPACE,
                                serviceDefinitionProxy, planProxy, new HashMap<>()));
                    });

                    It("should lookup a namespace URL using the specific base url", () -> {
                        ServiceDefinitionProxy service = namespaceServiceFixture();
                        Map<String, Object> serviceSettings = service.getServiceSettings();
                        serviceSettings.put(USE_SSL, true);
                        service.setServiceSettings(serviceSettings);

                        PlanProxy plan = service.findPlan(NAMESPACE_PLAN_ID1);

                        doReturn(DEFAULT_BASE_URL_NAME).when(broker).getBaseUrl();

                        String expectedUrl = new StringBuilder().append(HTTPS).append(NAMESPACE)
                                .append(DOT).append(BASE_URL).append(_9021).toString();
                        assertEquals(expectedUrl, ecs.getNamespaceURL(NAMESPACE, service, plan, new HashMap<>()));
                    });
                });

                Context("when there is a namespace in host", () -> {
                    BeforeEach(() -> {
                        mock(BaseUrlAction.class);
                        BaseUrl baseUrl = new BaseUrl();
                        baseUrl.setId(BASE_URL_ID);
                        baseUrl.setName(BASE_URL_NAME);
                        doReturn(Collections.singletonList(baseUrl)).when(ecsManagementClient)
                                .listBaseUrl(same(connection));

                        BaseUrlInfo baseUrlInfo = new BaseUrlInfo();
                        baseUrlInfo.setId(BASE_URL_ID);
                        baseUrlInfo.setName(BASE_URL_NAME);
                        baseUrlInfo.setNamespaceInHost(true);
                        baseUrlInfo.setBaseurl(BASE_URL);
                        doReturn(baseUrlInfo).when(ecsManagementClient)
                                .getBaseUrlInfo(connection, BASE_URL_ID);
                    });

                    It("should lookup a namespace url using supplied base url - no SSL", () -> {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put(BASE_URL, BASE_URL_NAME);
                        ServiceDefinitionProxy service = namespaceServiceFixture();
                        PlanProxy plan = service.findPlan(NAMESPACE_PLAN_ID1);

                        String expectedUrl = HTTP + NAMESPACE + DOT + BASE_URL + _9020;
                        assertEquals(expectedUrl, ecs.getNamespaceURL(NAMESPACE, service, plan, params));
                    });

                    It("should lookup a namespace url using supplied base url - with SSL", () -> {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put(BASE_URL, BASE_URL_NAME);
                        ServiceDefinitionProxy service = namespaceServiceFixture();
                        Map<String, Object> serviceSettings = service.getServiceSettings();
                        serviceSettings.put(USE_SSL, true);
                        service.setServiceSettings(serviceSettings);
                        PlanProxy plan = service.findPlan(NAMESPACE_PLAN_ID1);

                        String expectedURl = HTTPS + NAMESPACE + DOT + BASE_URL + _9021;
                        assertEquals(expectedURl, ecs.getNamespaceURL(NAMESPACE, service, plan, params));
                    });
                });
            });
        });
    }
    @Test
    public void noop() {
    }


}
