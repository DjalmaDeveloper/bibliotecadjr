package com.library.sistema_biblioteca.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI (Swagger) para documentação da API
 * 
 * Esta classe configura a documentação interativa da API usando SpringDoc OpenAPI 3.0
 * 
 * @author Sistema Biblioteca
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Sistema Biblioteca API}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configura a documentação OpenAPI da API
     * 
     * @return Objeto OpenAPI configurado
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Insira o token JWT obtido no endpoint /api/auth/login")
                        )
                );
    }

    /**
     * Define as informações básicas da API
     */
    private Info apiInfo() {
        return new Info()
                .title("Sistema Biblioteca API")
                .version("1.0.0")
                .description("""
                        # Sistema de Gerenciamento de Biblioteca
                        
                        API RESTful para gerenciamento completo de biblioteca, incluindo:
                        
                        ## Recursos Disponíveis
                        
                        ### 🔐 Autenticação
                        - Registro de novos usuários
                        - Login e autenticação JWT
                        - Controle de acesso baseado em perfis (USER/ADMIN)
                        
                        ### 👥 Usuários
                        - Gerenciamento de usuários
                        - Controle de perfis e permissões
                        - Ativação/desativação de contas
                        
                        ### 📚 Livros
                        - Cadastro, consulta, atualização e exclusão de livros
                        - Busca por título, autor e ISBN
                        - Controle de disponibilidade
                        
                        ### ✍️ Autores
                        - Gerenciamento completo de autores
                        - Informações biográficas
                        - Relacionamento com livros
                        
                        ### 📖 Empréstimos
                        - Controle de empréstimos de livros
                        - Gestão de devoluções
                        - Histórico de empréstimos
                        
                        ## Autenticação
                        
                        Esta API usa autenticação JWT (JSON Web Token). Para acessar endpoints protegidos:
                        
                        1. **Registre-se** usando `/api/auth/register` ou faça **login** com `/api/auth/login`
                        2. Copie o **token** recebido na resposta
                        3. Clique no botão **"Authorize" 🔓** no topo desta página
                        4. Cole o token no campo que aparecerá (sem adicionar "Bearer")
                        5. Agora você pode testar os endpoints protegidos!
                        
                        ### Perfis de Acesso
                        - **USER**: Acesso básico (consultas e empréstimos)
                        - **ADMIN**: Acesso total (gerenciamento completo do sistema)
                        
                        ## Tecnologias
                        - Spring Boot 3.5.7
                        - Spring Security + JWT
                        - Java 17
                        - PostgreSQL
                        - SpringDoc OpenAPI 3.0
                        
                        ## Como Usar
                        1. Registre-se ou faça login
                        2. Copie o token JWT
                        3. Clique em "Authorize" e cole o token
                        4. Explore os endpoints disponíveis
                        5. Clique em "Try it out" para testar
                        6. Preencha os parâmetros necessários
                        7. Clique em "Execute"
                        
                        ## Códigos de Status
                        - `200`: Sucesso
                        - `201`: Criado
                        - `204`: Sucesso (sem conteúdo)
                        - `400`: Requisição inválida
                        - `401`: Não autenticado
                        - `403`: Sem permissão
                        - `404`: Recurso não encontrado
                        - `500`: Erro interno do servidor
                        """)
                .contact(apiContact())
                .license(apiLicense());
    }

    /**
     * Define as informações de contato
     */
    private Contact apiContact() {
        return new Contact()
                .name("Sistema Biblioteca - Suporte")
                .email("suporte@biblioteca.com")
                .url("https://github.com/DjalmaDeveloper/bibliotecadjr");
    }

    /**
     * Define a licença da API
     */
    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Define os servidores disponíveis
     */
    private List<Server> apiServers() {
        // Servidor de Produção (Render)
        Server prodServer = new Server()
                .url("https://sistema-biblioteca-api.onrender.com")
                .description("Servidor de Produção (Render)");

        // Servidor Local
        Server devServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Servidor Local (Desenvolvimento)");

        return List.of(prodServer, devServer);
    }
}

