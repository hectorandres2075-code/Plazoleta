package com.plazoleta.infrastructure.output.client;

import com.plazoleta.application.port.output.MessagingServicePort;
import com.plazoleta.domain.exception.MessagingServiceUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/** Llama al microservicio de Mensajería reenviando el mismo token JWT que recibió Plazoleta. */
public class MessagingRestClientAdapter implements MessagingServicePort {

    private final RestClient restClient;

    public MessagingRestClientAdapter(String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public void sendSms(String celular, String mensaje) {
        try {
            restClient.post()
                    .uri("/api/v1/messages/sms")
                    .header(HttpHeaders.AUTHORIZATION, currentAuthorizationHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("celular", celular, "mensaje", mensaje))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new MessagingServiceUnavailableException();
        }
    }

    private String currentAuthorizationHeader() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String header = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        return header == null ? "" : header;
    }
}
