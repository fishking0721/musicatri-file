package org.fishking0721.oss.client;

import org.fishking0721.oss.pojo.dto.ValidateRequestDTO;
import org.fishking0721.oss.pojo.model.PermissionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "server-auth")
public interface AuthServerClient {

    @PostMapping("/api/v2/auth/validate")
    ResponseEntity<PermissionResponse> getuserId(@RequestHeader("Authorization") String token,
                                                 @RequestBody ValidateRequestDTO body
    );

     @PostMapping("/api/v2/auth/service/validate")
    ResponseEntity<PermissionResponse> getserviceId(@RequestHeader("Authorization") String token,
                                                    @RequestBody ValidateRequestDTO body);

}
