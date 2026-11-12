package com.catts.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

// "Vigia" de todos os controllers: transforma excecoes em respostas com o status HTTP certo
@RestControllerAdvice
public class ApiExceptionHandler {

    // Formato unico de erro: o front sempre recebe status, erro e mensagem
    public record ErroResponse(int status, String erro, String mensagem) {}

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status)
                .body(new ErroResponse(status.value(), status.getReasonPhrase(), mensagem));
    }

    // Dado invalido (nome vazio, foto que nao e imagem...) -> 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> badRequest(IllegalArgumentException e) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // Erros que o service lanca ja com status (ex.: ONG nao encontrada -> 404)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> comStatus(ResponseStatusException e) {
        return resposta(HttpStatus.valueOf(e.getStatusCode().value()), e.getReason());
    }

    // Foto acima do limite do application.properties -> 413
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErroResponse> arquivoGrande(MaxUploadSizeExceededException e) {
        return resposta(HttpStatus.valueOf(413), "A imagem excede o limite de 10 MB.");
    }
}
