package com.example.quizmaster.service;

import com.example.quizmaster.dto.LoginRequest;
import com.example.quizmaster.dto.LoginResponse;
import com.example.quizmaster.dto.MessageResponse;
import com.example.quizmaster.dto.SignUpRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.Base64;

@Service
public class UserService {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public UserService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public LoginResponse login(LoginRequest request) {
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        // Let WebClientResponseException propagate to GlobalExceptionHandler
        String responseBody = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", request.getEmail())
                        .with("password", request.getPassword()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode response = objectMapper.readTree(responseBody);

            String accessToken = response.get("access_token").asText();

            // Decode token to get user details
            String[] chunks = accessToken.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            JsonNode payloadNode = objectMapper.readTree(payload);

            String userId = payloadNode.has("sub") ? payloadNode.get("sub").asText() : "";

            String firstName = payloadNode.has("given_name") ? payloadNode.get("given_name").asText() : "";
            String lastName = payloadNode.has("family_name") ? payloadNode.get("family_name").asText() : "";

            return new LoginResponse(accessToken, userId, firstName, lastName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process login response: " + e.getMessage());
        }
    }

    public MessageResponse register(SignUpRequest request) {
        try (Keycloak keycloak = getKeycloakInstance()) {

            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(request.getEmail());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmailVerified(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);

            user.setCredentials(Collections.singletonList(credential));

            Response response = keycloak.realm(realm).users().create(user);

            if (response.getStatus() == 201) {
                return new MessageResponse("User registered successfully");
            } else if (response.getStatus() == 409) {
                throw new com.example.quizmaster.exception.ApiException("User already exists",
                        org.springframework.http.HttpStatus.CONFLICT);
            } else {
                throw new com.example.quizmaster.exception.ApiException(
                        "Failed to register user. Status: " + response.getStatus(),
                        org.springframework.http.HttpStatus.valueOf(response.getStatus()));
            }
        } catch (com.example.quizmaster.exception.ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public java.util.Optional<com.example.quizmaster.entity.User> findByEmail(String email) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            java.util.List<UserRepresentation> users = keycloak.realm(realm).users().searchByEmail(email, true);
            if (users.isEmpty()) {
                return java.util.Optional.empty();
            }
            UserRepresentation keycloakUser = users.get(0);
            com.example.quizmaster.entity.User user = new com.example.quizmaster.entity.User();
            user.setId(keycloakUser.getId());
            user.setEmail(keycloakUser.getEmail());
            user.setFirstName(keycloakUser.getFirstName());
            user.setLastName(keycloakUser.getLastName());
            return java.util.Optional.of(user);
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType("client_credentials")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}
