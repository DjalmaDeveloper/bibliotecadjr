package com.library.sistema_biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração global de CORS (Cross-Origin Resource Sharing)
 * 
 * Esta classe centraliza todas as configurações de CORS da aplicação,
 * permitindo que o frontend hospedado em diferentes domínios acesse a API.
 * 
 * @author Sistema Biblioteca
 * @version 1.0
 */
@Configuration
public class CorsConfig {

    /**
     * Define as origens permitidas para acessar a API
     */
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "https://sistema-biblioteca-api.onrender.com", // API em Produção (Render) - Swagger UI
            "https://bibliotecadjr.pages.dev",              // Frontend em Produção (Cloudflare Pages)
            "http://localhost:3000",                        // Frontend Local (React/Next.js)
            "http://localhost:5173",                        // Frontend Local (Vite)
            "http://localhost:8080",                        // API Local
            "http://127.0.0.1:3000",                       // Frontend Local (alternativo)
            "http://127.0.0.1:5173",                       // Frontend Local (Vite alternativo)
            "http://127.0.0.1:8080"                        // API Local (alternativo)
    );

    /**
     * Define os métodos HTTP permitidos
     */
    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS",
            "PATCH"
    );

    /**
     * Define os headers permitidos nas requisições
     */
    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Requested-With",
            "X-Request-ID"
    );

    /**
     * Define os headers expostos nas respostas
     */
    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Location",
            "Content-Disposition"
    );

    /**
     * Configura o filtro CORS global para a aplicação
     * 
     * @return CorsFilter configurado
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfigurationSource source = corsConfigurationSource();
        return new CorsFilter(source);
    }

    /**
     * Configura a fonte de configuração CORS
     * 
     * @return CorsConfigurationSource configurado
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciais apenas para origens específicas (não file://)
        // file:// não suporta credentials
        config.setAllowCredentials(false);
        
        // Configurar origens permitidas
        config.setAllowedOrigins(ALLOWED_ORIGINS);
        
        // Permitir qualquer origem para desenvolvimento (file://)
        // Comentar em produção por segurança
        config.addAllowedOriginPattern("*");
        
        // Configurar métodos HTTP permitidos
        config.setAllowedMethods(ALLOWED_METHODS);
        
        // Configurar headers permitidos
        config.setAllowedHeaders(ALLOWED_HEADERS);
        
        // Configurar headers expostos
        config.setExposedHeaders(EXPOSED_HEADERS);
        
        // Tempo de cache da configuração CORS (em segundos)
        // 3600 segundos = 1 hora
        config.setMaxAge(3600L);
        
        // Registrar configuração para todos os endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return source;
    }
}

