package com.emc.ecs.cloudfoundry.broker.service;

import com.emc.ecs.cloudfoundry.broker.config.BrokerConfig;
import com.emc.ecs.cloudfoundry.broker.config.CatalogConfig;
import com.emc.ecs.cloudfoundry.broker.model.PlanProxy;
import com.emc.ecs.cloudfoundry.broker.model.ServiceDefinitionProxy;
import com.emc.ecs.cloudfoundry.broker.repository.ServiceInstanceBinding;
import com.emc.ecs.cloudfoundry.broker.repository.ServiceInstanceRepository;
import com.emc.ecs.management.sdk.Connection;
import com.emc.ecs.management.sdk.model.ObjectBucketCreate;
import com.emc.ecs.management.sdk.model.UserSecretKey;
import com.emc.ecs.management.sdk.ECSManagementClient;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.VolumeMount;
import org.springframework.cloud.servicebroker.model.catalog.Plan;

import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emc.ecs.common.Fixtures.*;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
    private static final String EXISTS = "exists";
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

            Context("create bucket", () -> {


                BeforeEach(()-> {

                    doNothing().when(ecsManagementClient)
                            .createBucket(same(connection), any(ObjectBucketCreate.class));
                });

                Context("when the bucket exists", () -> {
                    BeforeEach(()-> {
                        when(ecsManagementClient.exists(same(BUCKET_NAME), same(NAMESPACE))).thenReturn(true);
                    });


                });

                Context("when the bucket doesn't exist", () -> {
                    BeforeEach(()-> {
                        when(ecsManagementClient.exists(same(BUCKET_NAME), same(NAMESPACE))).thenReturn(false);
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
                    });
                });
            });
        });
    }
    @Test
    public void noop() {
    }

}
