package com.catts.backend.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;


import java.io.IOException;
import java.util.UUID;

@Service
public class AzureBlobService {

    private final BlobContainerClient containerClient;

    public AzureBlobService(
            @Value("${azure.storage.container-name}") String containerName) {

        String connectionString =
                System.getenv("AZURE_STORAGE_CONNECTION_STRING");

        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException(
                    "A variável AZURE_STORAGE_CONNECTION_STRING não foi configurada."
            );
        }

        BlobServiceClient blobServiceClient =
                new BlobServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();

        this.containerClient =
                blobServiceClient.getBlobContainerClient(containerName);
    }

    public String uploadImagemAnimal(MultipartFile arquivo) throws IOException {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "A imagem do animal não pode estar vazia."
            );
        }

        String contentType = arquivo.getContentType();


        // Trocar isso para status, evitar quebrar o back

        if (contentType == null ||
                !contentType.matches("image/(jpeg|png|webp)")) {
            throw new IllegalArgumentException(
                    "Formato inválido. Envie uma imagem JPEG, PNG ou WebP."
            );
        }

        String extensao = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "Formato de imagem não suportado."
            );
        };

        String nomeArquivo =
                "animais/" + UUID.randomUUID() + extensao;

        BlobClient blobClient =
                containerClient.getBlobClient(nomeArquivo);

        BlobHttpHeaders headers =
                new BlobHttpHeaders().setContentType(contentType);

        blobClient.upload(arquivo.getInputStream(), arquivo.getSize());
        blobClient.setHttpHeaders(headers);

        return nomeArquivo;
    }

    // Apaga a foto do Blob (usado quando o banco falha depois do upload, para nao deixar foto sozinha la)
    public void deleteBlob(String blobName) {
        containerClient.getBlobClient(blobName).deleteIfExists();
    }

    // verificador de arquivo Blob
    public boolean blobExiste(String blobName) {
        return containerClient.getBlobClient(blobName).exists();
    }

    // pega o arquivo do Blob convertendo p bytes devolve os bytes
    public byte[] baixarBlob(String blobName) {
        return containerClient.getBlobClient(blobName).downloadContent().toBytes();
    }

    // grava bytes no Blob (sobrescreve se ja existir, ex.: editar a mesma foto de novo)
    public void enviarBytes(String blobName, byte[] dados, String contentType) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.upload(new ByteArrayInputStream(dados), dados.length, true);
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
    }
}