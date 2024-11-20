package com.example.config;

import com.example.task.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserClient userClient;

    public String getLoggedInUserLogin() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return login -> {
            if (login == null || login.isEmpty()) {
                throw new UsernameNotFoundException("Login is empty or null");
            }
            return User.withUsername(login)
                    .password("")
                    .authorities("ROLE_USER", "ROLE_EDITOR", "ROLE_ADMIN")
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return null;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return false;
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Europe/Warsaw"));
    }
}

