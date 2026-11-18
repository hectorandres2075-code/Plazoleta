package com.plazoleta.infrastructure.output.client;

import com.plazoleta.application.port.output.UserServicePort;
import com.plazoleta.domain.exception.UserServiceUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;

/** Llama al microservicio de Usuarios reenviando el mismo token JWT que recibió Plazoleta. */
public class UserRestClientAdapter implements UserServicePort {

    private final RestClient restClient;

    public UserRestClientAdapter(String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<String> findRoleById(Long userId) {
        try {
            Map<String, String> body = restClient.get()
                    .uri("/api/v1/users/{id}/role", userId)
                    .header(HttpHeaders.AUTHORIZATION, currentAuthorizationHeader())
                    .retrieve()
                    .body(Map.class);
            return Optional.ofNullable(body).map(b -> b.get("rol"));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new UserServiceUnavailableException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<String> findPhoneById(Long userId) {
        try {
            Map<String, String> body = restClient.get()
                    .uri("/api/v1/users/{id}/phone", userId)
                    .header(HttpHeaders.AUTHORIZATION, currentAuthorizationHeader())
                    .retrieve()
                    .body(Map.class);
            return Optional.ofNullable(body).map(b -> b.get("celular"));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new UserServiceUnavailableException();
        }
    }

    private String currentAuthorizationHeader() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String header = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        return header == null ? "" : header;
    }
}
