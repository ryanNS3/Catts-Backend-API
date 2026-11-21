package com.catts.backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

// Conversa com o modelo FLUX.2-flex no Azure Foundry: manda a foto + prompt e recebe a foto editada
@Service
public class FoundryImageService {

    private static final Logger log = LoggerFactory.getLogger(FoundryImageService.class);

    private static final String CAMINHO = "/providers/blackforestlabs/v1/flux-2-flex?api-version=preview";

    private final RestClient restClient;
    private final String endpoint;
    private final String apiKey;

    public FoundryImageService(
            @Value("${foundry.endpoint:}") String endpoint,
            @Value("${foundry.api-key:}") String apiKey) {

        // tira espacos e a barra final do endpoint (so o dominio, ex.: https://xxx.services.ai.azure.com)
        String e = endpoint == null ? "" : endpoint.trim();
        while (e.endsWith("/")) {
            e = e.substring(0, e.length() - 1);
        }
        this.endpoint = e;
        this.apiKey = apiKey == null ? "" : apiKey.trim();

        // a IA demora alguns segundos: timeout de leitura maior que o padrao
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(180));
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    // Recebe os bytes da foto original e devolve os bytes da foto editada (JPEG)
    public byte[] editar(byte[] imagemOriginal, String prompt) {

        if (endpoint.isEmpty() || apiKey.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "A edição de imagem por IA não está configurada.");
        }

        // JSON montado aqui e enviado como bytes: assim o Azure recebe o Content-Length,
        // que ele exige (sem isso responde 400 "no_content_length_header")
        String json = "{\"model\":\"FLUX.2-flex\","
                + "\"prompt\":\"" + escapar(prompt) + "\","
                + "\"input_image\":\"" + Base64.getEncoder().encodeToString(imagemOriginal) + "\","
                + "\"output_format\":\"jpeg\"}";
        byte[] corpo = json.getBytes(StandardCharsets.UTF_8);

        FluxResponse resposta;
        try {
            resposta = restClient.post()
                    .uri(endpoint + CAMINHO)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .body(FluxResponse.class);
        } catch (RestClientResponseException e) {
            // o corpo do erro do Azure nao tem a chave; vai pro log do servidor, nao pro front
            log.error("Foundry respondeu {}: {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 429) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Muitas edições seguidas. Tente de novo em 1 minuto.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "O serviço de IA não conseguiu editar a imagem.");
        } catch (ResourceAccessException e) {
            log.error("Falha de conexao/timeout com o Foundry", e);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT,
                    "A IA demorou demais para responder.");
        }

        if (resposta == null || resposta.data() == null || resposta.data().isEmpty()
                || resposta.data().get(0).b64_json() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "A IA não devolveu a imagem.");
        }

        return Base64.getDecoder().decode(resposta.data().get(0).b64_json());
    }

    // Escapa aspas, barras e quebras de linha para o texto caber dentro de um JSON
    private static String escapar(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Formato da resposta: { "created":..., "data":[{"b64_json":"..."}], "request_meta":{...} }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FluxResponse(List<Item> data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String b64_json) {}
}
