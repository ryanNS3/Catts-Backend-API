package com.catts.backend.config;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.catts.backend.repository.AdopterRepository;
import com.catts.backend.repository.OngRepository;
import com.catts.backend.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final AdopterRepository adotanteRepository;


    private final OngRepository ongRepository;

    public SecurityFilter(TokenService tokenService, AdopterRepository adotanteRepository, OngRepository ongRepository) {
        this.tokenService = tokenService;
        this.adotanteRepository = adotanteRepository;
        this.ongRepository = ongRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // =========================================================================
        // 1. EXTRAÇÃO DO TOKEN DO CABEÇALHO HTTP
        // =========================================================================
        // Tenta pegar o token enviado pelo Angular no header "Authorization: Bearer <token>"
        String tokenJWT = recuperarToken(request);

        // Se a requisição veio sem token (ex: rotas públicas /api/auth/google ou /v1/public),
        // este bloco 'if' é ignorado e a requisição segue direto para o Passo 5.
        if (tokenJWT != null) {

            // =========================================================================
            // 2. VALIDAÇÃO E DECODIFICAÇÃO DO JWT (Usando o TokenService do Passo 2)
            // =========================================================================
            DecodedJWT decodedJWT = tokenService.validarToken(tokenJWT);

            // Se decodedJWT for diferente de null, significa que a assinatura é válida e não expirou
            if (decodedJWT != null) {
                String email = decodedJWT.getSubject();               // O e-mail salvo no token
                String tipo = decodedJWT.getClaim("tipo").asString(); // "ADOTANTE" ou "ONG"

                Object usuarioPrincipal = null;
                String role = null;

                // =========================================================================
                // 3. BUSCA DO USUÁRIO NA TABELA CORRETA (ADOTANTE OU ONG)
                // =========================================================================
                if ("ADOTANTE".equals(tipo)) {
                    usuarioPrincipal = adotanteRepository.findByEmail(email).orElse(null);
                    role = "ROLE_ADOTANTE";
                } else if ("ONG".equals(tipo)) {
                    usuarioPrincipal = ongRepository.findByEmail(email).orElse(null);
                    role = "ROLE_ONG";
                }

                // =========================================================================
                // 4. REGISTRO DO USUÁRIO AUTENTICADO NO SPRING SECURITY
                // =========================================================================
                if (usuarioPrincipal != null) {
                    // Define o perfil de acesso (ROLE_ADOTANTE ou ROLE_ONG)
                    var authorities = List.of(new SimpleGrantedAuthority(role));

                    // Cria o objeto de autenticação usando o construtor de 3 PARÂMETROS:
                    // - 1º (usuarioPrincipal): O próprio objeto Adotante ou Ong vindo do banco.
                    // - 2º (null): Credenciais/Senha (null porque o token já provou quem ele é).
                    // - 3º (authorities): Permissões. Usar esse construtor marca o usuário como AUTENTICADO.
                    var authentication = new UsernamePasswordAuthenticationToken(
                            usuarioPrincipal,
                            null,
                            authorities
                    );

                    // Guarda essa autenticação no contexto da requisição atual.
                    // A partir desta linha, o Spring Security libera o acesso às rotas protegidas!
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // =========================================================================
        // 5. CONTINUAÇÃO DO FLUXO DA REQUISIÇÃO
        // =========================================================================
        // Passa a requisição adiante (para os próximos filtros do Spring e, por fim, para o Controller).
        // Se a rota exigir autenticação e o passo 4 não tiver sido executado, o Spring bloqueia com 403 Forbidden.
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
