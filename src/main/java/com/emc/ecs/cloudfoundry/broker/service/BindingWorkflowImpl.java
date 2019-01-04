package com.emc.ecs.cloudfoundry.broker.service;

import com.emc.ecs.cloudfoundry.broker.EcsManagementClientException;
import com.emc.ecs.cloudfoundry.broker.model.PlanProxy;
import com.emc.ecs.cloudfoundry.broker.model.ServiceDefinitionProxy;
import com.emc.ecs.cloudfoundry.broker.repository.ServiceInstanceBinding;
import com.emc.ecs.cloudfoundry.broker.repository.ServiceInstanceRepository;
//import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
//import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
//import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

abstract public class BindingWorkflowImpl implements BindingWorkflow {
    ServiceInstanceRepository instanceRepository;
    protected final EcsService ecs;
    protected ServiceDefinitionProxy service;
    protected PlanProxy plan;
    String instanceId;
    String bindingId;
    CreateServiceInstanceBindingRequest createRequest;

    BindingWorkflowImpl(ServiceInstanceRepository instanceRepo, EcsService ecs) {
        this.instanceRepository = instanceRepo;
        this.ecs = ecs;
    }

    public BindingWorkflow withCreateRequest(CreateServiceInstanceBindingRequest request) {
        this.instanceId = request.getServiceInstanceId();
        this.bindingId = request.getBindingId();
        this.createRequest = request;
        return(this);
    }

    public BindingWorkflow withDeleteRequest(DeleteServiceInstanceBindingRequest request) {
        this.instanceId = request.getServiceInstanceId();
        this.bindingId = request.getBindingId();
        return(this);
    }

    String getUserInfo(String userSecret) {
        return bindingId + ":" + userSecret;
    }

    public ServiceInstanceBinding getBinding(Map<String, Object> credentials) {
        ServiceInstanceBinding binding = new ServiceInstanceBinding(createRequest);
        binding.setBindingId(bindingId);
        binding.setCredentials(credentials);
        return binding;
    }

//    public CreateServiceInstanceAppBindingResponse getResponse(Map<String, Object> credentials) {
//        return new CreateServiceInstanceAppBindingResponse()
//                .withCredentials(credentials);
//    }

//    This might work???
//    Not confident about this builder being complete
    public CreateServiceInstanceAppBindingResponse getResponse(Map<String, Object> credentials) {
        return CreateServiceInstanceAppBindingResponse.builder()
                .credentials(credentials)
                .build();
    }

    public Map<String, Object> getCredentials(String secretKey)
            throws IOException, EcsManagementClientException {
        Map<String, Object> credentials = new HashMap<>();

        credentials.put("accessKey", ecs.prefix(bindingId));
        credentials.put("secretKey", secretKey);

        return credentials;
    }
}
