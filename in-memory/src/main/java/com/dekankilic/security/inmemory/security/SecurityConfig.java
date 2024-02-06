package com.dekankilic.security.inmemory.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Eğer ben bunu demezsem diagramı hatırlayın, ben securityfilterchain'i uygulayamam.
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(){ // Buna neden in memory diyoruz? Çünkü UserDetailsService'in kullandığı implementasyon InMemoryUserDetailsManager
        UserDetails user1 = User.builder()
                .username("mahmut")
                .password("pass")
                .passwordEncoder(str -> passwordEncoder().encode(str))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("dekan")
                .password("pass")
                .passwordEncoder(str -> passwordEncoder().encode(str))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception{
        security
                .headers(x -> x.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(x -> x.requestMatchers("/public/**", "/auth/**").permitAll())
                .authorizeHttpRequests(x -> x.requestMatchers("/private/user/**").hasRole("USER"))
                .authorizeHttpRequests(x -> x.requestMatchers("/private/admin/**").hasRole("ADMIN"))
                .authorizeHttpRequests(x -> x.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return security.build();
    }
}
