package org.vip.admin.model.fga;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserTuple {

    @JsonProperty("tuple_keys")
    private List<User> tupleKeys;

    public List<User> getTupleKeys() {
        return tupleKeys;
    }

    public void setTupleKeys(List<User> tupleKeys) {
        this.tupleKeys = tupleKeys;
    }
}
