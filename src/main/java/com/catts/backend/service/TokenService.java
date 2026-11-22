package com.catts.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.util.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {
    // Injeta a chave secreta do application.properties.
            // É essa senha que assina o token: se alguém tentar alterar qualquer dado dentro do JWT
            // no navegador, a assinatura quebra e a API rejeita o token.
    @Value("${api.security.token.secret}")
    private String secret;

    // 1. GERAÇÃO DO TOKEN JWT (Chamado no Login e ao Finalizar o Cadastro)

    public String gerarToken(Long id, String email, String tipoUsuario) {
        try {
            // Define o algoritmo de assinatura HMAC256 usando a sua chave secreta
            Algorithm algoritmo = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("catts-api")          // Identifica quem emitiu o token (sua API)
                    .withSubject(email)               // Dono do token (identificador principal: o e-mail)
                    .withClaim("id", id)              // Dado extra: ID do usuário no banco
                    .withClaim("tipo", tipoUsuario)   // Dado extra: "ADOTANTE" ou "ONG" (para sabermos em qual tabela buscar)
                    .withExpiresAt(dataExpiracao())   // Define até quando o token é válido
                    .sign(algoritmo);                 // Assina e gera a String final do JWT

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }


    // 2. VALIDAÇÃO DO TOKEN JWT (Chamado pelo SecurityFilter em cada requisição)

    public DecodedJWT validarToken(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);

            // Verifica se a assinatura bate com o 'secret', se o emissor é "catts-api"
            // e se o token não passou da data de expiração.
            // Se estiver tudo certo, devolve o token decodificado para lermos o 'subject' e as 'claims'.
            return JWT.require(algoritmo)
                    .withIssuer("catts-api")
                    .build()
                    .verify(tokenJWT);

        } catch (JWTVerificationException exception) {
            // Se o token foi adulterado, está malformado ou expirou, cai aqui e retorna null
            return null;
        }
    }

    // 3. CÁLCULO DO TEMPO DE EXPIRAÇÃO
    private Instant dataExpiracao() {
        // Pega a hora atual, soma 24 horas e converte para o fuso horário de Brasília (-03:00).
        // Depois desse prazo, o usuário precisará fazer login novamente.
        return LocalDateTime.now()
                .plusHours(24)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}


