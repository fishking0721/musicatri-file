package org.fishking0721.oss.pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PermissionResponse {
    private Data data;
    private String massage;

    @lombok.Data
    public static class Data implements Serializable {
        private List<String> roles;
        @JsonProperty("user_id")
        private String userId;
    }

}
