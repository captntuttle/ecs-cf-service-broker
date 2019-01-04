package com.emc.ecs.cloudfoundry.broker.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.instance.OperationState;

@SuppressWarnings("unused")
public class LastOperationSerializer {

    @JsonSerialize
    @JsonProperty("operation_state")
    private OperationState operationState;

    @JsonSerialize
    private String description;

    @JsonSerialize
    @JsonProperty("delete_operation")
    private boolean deleteOperation;

    public LastOperationSerializer() {
        super();
    }

    LastOperationSerializer(final OperationState operationState,
            final String description, final boolean deleteOperation) {
        super();
        this.setOperationState(operationState);
        this.setDescription(description);
        this.setDeleteOperation(deleteOperation);
    }

    public OperationState getOperationState() {
        return operationState;
    }

    private void setOperationState(OperationState operationState) {
        this.operationState = operationState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleteOperation() {
        return deleteOperation;
    }

    private void setDeleteOperation(boolean deleteOperation) {
        this.deleteOperation = deleteOperation;
    }
}