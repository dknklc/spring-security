package com.dekankilic.security.basicauth.security;

import com.dekankilic.security.basicauth.model.Role;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security, HandlerMappingIntrospector introspector) throws Exception {

        MvcRequestMatcher.Builder mvcRequestBuilder = new MvcRequestMatcher.Builder(introspector);

        security
                .headers(x -> x.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(csrfConfig -> csrfConfig.ignoringRequestMatchers(mvcRequestBuilder.pattern("/public/**"))
                        .ignoringRequestMatchers(PathRequest.toH2Console()))
                .authorizeHttpRequests(x -> x
                        .requestMatchers(mvcRequestBuilder.pattern("/public/**")).permitAll()
                        .requestMatchers(mvcRequestBuilder.pattern("/private/admin/**")).hasRole(Role.ROLE_USER.getValue())
                        .requestMatchers(mvcRequestBuilder.pattern("/private/**")).hasAnyRole(Role.ROLE_USER.getValue(), Role.ROLE_ADMIN.getValue(), Role.ROLE_FSK.getValue())
                        .requestMatchers(PathRequest.toH2Console()).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return security.build();
    }
}

// MvcRequestMatcher.Builder mvcRequestBuilder = new MvcRequestMatcher.Builder(introspector) bunu h2console'un açılması için yapıyoruz ve bunu bir kere define ettiğimzde requestMatchers() içerisinde
// artık onu kullanmak zorundayız. Filter Chain'in In-memory örneğinden farklı olan kısmı sadece orası.
// most specific requestmatcher'i permitAll'dan sonra koy. anyRequest().authenticated()'i en sona koy. Yani geri kalanların hepsi authenticated zorunlu.