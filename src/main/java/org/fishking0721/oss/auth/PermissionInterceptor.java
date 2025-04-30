package org.fishking0721.oss.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fishking0721.oss.client.AuthServerClient;
import org.fishking0721.oss.pojo.dto.ValidateRequestDTO;
import org.fishking0721.oss.pojo.model.PermissionResponse;
import org.fishking0721.oss.redis.RedisUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    @Lazy  //延迟注入避免循环依赖
    private AuthServerClient authServerClient;

    @Autowired
    private PermissionCacheService permissionCacheService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 从请求头获取JWT
        String jwt = extractJwt(request);

//        PermissionResponse.Data permissionData = permissionCacheService.getPermission(jwt);
        //get or load方法，缓存中有直接加载，没有就去auth-server拉取
        PermissionResponse.Data permissionData = redisUtil.getOrLoad("auth:permission:token:" + jwt, 1,
                () -> authServerClient.getuserId(jwt, new ValidateRequestDTO("")).getBody().getData()
        );

//        if (permissionData == null) {
//            // 缓存中没有，去auth-server拉取
//            ValidateRequestDTO body = new ValidateRequestDTO("");
//            ResponseEntity<PermissionResponse> result = authServerClient.getuserId(jwt, body);
//
//            if (result.getStatusCode() == HttpStatus.OK) {
//                PermissionResponse responseBody = result.getBody();
//                permissionData = responseBody.getData();
//                // 缓存到redis
////                permissionCacheService.cachePermission(jwt, permissionData, 1); //缓存1分钟
//            } else {
//                response.setStatus(result.getStatusCodeValue());
//                response.getWriter().write("Permission check failed");
//                return false;
//            }
//        }
        // 设置安全上下文
        setSecurityContext(permissionData);
        return true;
    }

    private String extractJwt(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        return header;
//        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
//            return header.substring(7); // 去除"Bearer "前缀
//        }
//        throw new Exception("Missing a valid JWT token");
    }

    //设置安全上下文，供权限自定义注解处理,固定用法
    private void setSecurityContext(PermissionResponse.Data data) {
        // 转换角色为 GrantedAuthority 集合
        List<GrantedAuthority> authorities = data.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        /**
         Authentication authentication = new UsernamePasswordAuthenticationToken(
               principal,   // 用户身份（如用户名、UserDetails对象）
               credentials, // 凭证（如密码，可为null）
               authorities  // 权限列表（List<GrantedAuthority>）
        );
         **/
        Authentication auth = new UsernamePasswordAuthenticationToken(
                data.getUserId(),
                null,
                authorities
        );
        // 存入安全上下文
        SecurityContextHolder.getContext().setAuthentication(auth);

        MDC.put("userId", data.getUserId());
    }
}
