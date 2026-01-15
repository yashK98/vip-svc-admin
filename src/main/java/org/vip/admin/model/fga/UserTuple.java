package org.vip.admin.model.fga;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserTuple {
    @JsonProperty("tuple_keys")
    private List<User> tupleKeys;
}
