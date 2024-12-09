package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /**
     * Configuration related to generating jwt tokens for management staff
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    /**
     * User-side WeChat user generates jwt token related configuration
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
