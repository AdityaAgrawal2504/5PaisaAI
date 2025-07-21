package com.example.logininitiation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures application security, including the password encoder and HTTP security rules.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigLIA_9371 {

    /**
     * Defines the PasswordEncoder bean for hashing and verifying passwords.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoderLIA_9371() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     * Disables CSRF and allows public access to the login initiation endpoint.
     */
    @Bean
    public SecurityFilterChain securityFilterChainLIA_9371(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Not recommended for browser-based clients
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/initiate", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Required for H2 console frame to be displayed
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}
```
src/main/java/com/example/logininitiation/enums/ErrorCodeLIA_9371.java
```java