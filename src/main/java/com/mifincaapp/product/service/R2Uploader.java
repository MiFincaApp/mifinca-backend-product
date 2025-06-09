package com.mifincaapp.product.service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class R2Uploader {

    private final String ACCESS_KEY_ID;
    private final String SECRET_ACCESS_KEY;
    private final String CLOUDFLARE_ENDPOINT;
    private final String CLOUDFLARE_REGION;
    private final String BUCKET_NAME;

    private final String CLOUDFLARE_API_TOKEN;
    private final String CLOUDFLARE_ZONE_ID;

    private final S3Client s3Client;

    public R2Uploader(DotenvService dotenvService) {
        this.ACCESS_KEY_ID = dotenvService.getOrThrow("ACCESS_KEY_ID");
        this.SECRET_ACCESS_KEY = dotenvService.getOrThrow("SECRET_ACCESS_KEY");
        this.CLOUDFLARE_ENDPOINT = dotenvService.getOrThrow("CLOUDFLARE_ENDPOINT");
        this.CLOUDFLARE_REGION = dotenvService.getOrThrow("CLOUDFLARE_REGION");
        this.BUCKET_NAME = dotenvService.getOrThrow("BUCKET_NAME");
        this.CLOUDFLARE_API_TOKEN = dotenvService.getOrThrow("CLOUDFLARE_API_TOKEN");
        this.CLOUDFLARE_ZONE_ID = dotenvService.getOrThrow("CLOUDFLARE_ZONE_ID");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
        this.s3Client = S3Client.builder()
                .endpointOverride(java.net.URI.create(CLOUDFLARE_ENDPOINT))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(CLOUDFLARE_REGION))
                .forcePathStyle(true)
                .build();
    }

    // Sube un archivo a R2
    public void uploadFile(String keyName, MultipartFile multipartFile) throws IOException {
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(keyName)
                .contentType(multipartFile.getContentType())
                .build();

        s3Client.putObject(putReq, RequestBody.fromBytes(multipartFile.getBytes()));
        System.out.println("Archivo subido exitosamente con key: " + keyName);
    }

    // Elimina un archivo y purga el caché
    public void deleteFile(String keyName) {
        try {
            DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(keyName)
                    .build();

            s3Client.deleteObject(delReq);
            System.out.println("Archivo eliminado exitosamente: " + keyName);

            // Purgar caché
            String fileUrl = "https://storage.mifincaapp.com/" + keyName;
            purgeCache(fileUrl);

        } catch (Exception e) {
            System.err.println("Error al eliminar el archivo: " + e.getMessage());
        }
    }

    // Purgar caché en Cloudflare
    public void purgeCache(String fileUrl) {
        try {
            String apiUrl = "https://api.cloudflare.com/client/v4/zones/" + CLOUDFLARE_ZONE_ID + "/purge_cache";
            String jsonPayload = "{ \"files\": [ \"" + fileUrl + "\" ] }";

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + CLOUDFLARE_API_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (var os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("Caché purgada exitosamente para: " + fileUrl);
            } else {
                System.err.println("Error al purgar caché. Código HTTP: " + code);
            }

        } catch (Exception e) {
            System.err.println("Error al purgar caché en Cloudflare: " + e.getMessage());
        }
    }
}
