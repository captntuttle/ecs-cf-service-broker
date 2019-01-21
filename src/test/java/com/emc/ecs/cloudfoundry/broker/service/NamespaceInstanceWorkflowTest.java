package com.emc.ecs.cloudfoundry.broker.service;

import com.emc.ecs.cloudfoundry.broker.model.PlanProxy;
import com.emc.ecs.cloudfoundry.broker.model.ServiceDefinitionProxy;
import com.emc.ecs.cloudfoundry.broker.repository.ServiceInstance;
import com.emc.ecs.cloudfoundry.broker.repository.ServiceInstanceRepository;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static com.emc.ecs.common.Fixtures.*;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(Ginkgo4jRunner.class)
public class NamespaceInstanceWorkflowTest {

    private EcsService ecs;
    private ServiceInstanceRepository instanceRepo;
    private Map<String, Object> parameters = new HashMap<>();
    private InstanceWorkflow workflow;
    private ServiceDefinitionProxy serviceProxy = new ServiceDefinitionProxy();
    private PlanProxy planProxy = new PlanProxy();
    private ServiceInstance bucketInstance = serviceInstanceFixture();
    private ArgumentCaptor<ServiceInstance> instCaptor = ArgumentCaptor.forClass(ServiceInstance.class);

    {
        Describe("NamespaceInstanceWorkflow", () -> {
            BeforeEach(() -> {
                ecs = mock(EcsService.class);
                instanceRepo = mock(ServiceInstanceRepository.class);
                workflow = new NamespaceInstanceWorkflow(instanceRepo, ecs);
            });

            Context("#changePlan", () -> {
                BeforeEach(() -> {
                    when(ecs.changeNamespacePlan(NAMESPACE, serviceProxy, planProxy, parameters))
                            .thenReturn(new HashMap<>());
                });

                It("should change the plan", () -> {
                    workflow.changePlan(NAMESPACE, serviceProxy, planProxy, parameters);
                    verify(ecs, times(1))
                            .changeNamespacePlan(NAMESPACE, serviceProxy, planProxy, parameters);
                });
            });
        });
    }
}
