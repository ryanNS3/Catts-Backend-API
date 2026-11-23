package com.catts.backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.http.HttpClient;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SpringConfig {

    private final SecurityFilter securityFilter;

    public SpringConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception{
        return http.
                // Permite que diferentes fontes acessem o backend
                cors(cors -> cors.configurationSource(corsConfigurationSource))
                // Tira o bloqueio de uso dos métodos POST e Put
                .csrf(AbstractHttpConfigurer::disable)
                // Muda o status do back para Stateles, isso faz com que o spring não faca uma sessão para login e sim guarde o JWT
                .sessionManagement( sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "/api/auth/google").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/animal").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/animais/**").hasRole("ONG")
                        .requestMatchers(HttpMethod.DELETE, "/api/animais/**").hasRole("ONG")
                        .requestMatchers(HttpMethod.POST, "/api/auth/completar-cadastro/adotante").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/completar-cadastro/ong").permitAll()
                        .requestMatchers("/v1/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                //Diz ao Spring Security para rodar o NOSSO SecurityFilter ANTES
                // do filtro padrão de usuário/senha (UsernamePasswordAuthenticationFilter).
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        // instancia para modificar as configuracoes do cors
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
