package com.catts.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleAuthService {

    // Injeta o valor da chave 'google.client.id' que você definiu no application.properties.
    // Ele identifica o seu aplicativo no Google Cloud Console.
    @Value("${google.client.id}")
    private String googleClientId;
    public GoogleIdToken.Payload validarTokenGoogle(String idTokenString) {
        try {
            // =========================================================================
            // 1. CONFIGURAÇÃO DO VERIFICADOR DO GOOGLE
            // =========================================================================
            // Monta o objeto que sabe validar a assinatura criptográfica do Google:
            // - NetHttpTransport: usado para baixar as chaves públicas do Google (e fazer cache delas).
            // - GsonFactory: usado para ler o JSON interno do token.
            // - setAudience: GARANTE que o token foi gerado exclusivamente para o SEU app
            //   (impede que alguém pegue um token válido de outro site e tente na sua API).
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .setAcceptableTimeSkewSeconds(300) // tolera diferença de relógio entre Azure e Google
                    .build();

            // =========================================================================
            // 2. VERIFICAÇÃO DA ASSINATURA E VALIDADE
            // =========================================================================
            // Checa se a assinatura é verdadeira, se o emissor é 'accounts.google.com'
            // e se o token ainda não expirou. Se algo falhar, retorna null.
            GoogleIdToken idToken = verifier.verify(idTokenString);

            // =========================================================================
            // 3. EXTRAÇÃO DOS DADOS DO USUÁRIO (PAYLOAD)
            // =========================================================================
            if (idToken != null) {
                // O Payload contém o e-mail (getEmail()), nome ("name"), foto ("picture"), etc.
                return idToken.getPayload();
            }

            throw new IllegalArgumentException("Token do Google inválido ou expirado.");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar autenticação com o Google: " + e.getMessage(), e);
        }
    }
}
