package com.esprit.userservice.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service qui interagit avec l'Admin REST API de Keycloak.
 *
 * Responsabilités :
 *  - Créer un utilisateur dans Keycloak lors du register
 *  - Assigner le rôle realm (ADMIN / CLIENT / FREELANCER)
 *  - Vérifier qu'un utilisateur existe dans Keycloak (pour le login)
 */
@Service
@Slf4j
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private Keycloak keycloakAdmin;

    @PostConstruct
    public void init() {
        // Connexion via client_credentials (api-gateway) dans ProlanceRealm
        // Plus besoin de username/password admin
        keycloakAdmin = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)                              // ProlanceRealm
                .clientId(clientId)                        // api-gateway
                .clientSecret(clientSecret)                // le secret dans application.properties
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    /**
     * Crée un utilisateur dans Keycloak et lui assigne le rôle donné.
     *
     * @param email     email (= username dans Keycloak)
     * @param password  mot de passe en clair (Keycloak le hashera)
     * @param firstName prénom
     * @param lastName  nom
     * @param roleName  ADMIN | CLIENT | FREELANCER
     * @return l'ID Keycloak du nouvel utilisateur
     */
    public String createUser(String email, String password, String firstName,
                             String lastName, String roleName) {

        RealmResource realmResource = keycloakAdmin.realm(realm);
        UsersResource usersResource = realmResource.users();

        // Construction de la représentation utilisateur
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailVerified(true);

        // Mot de passe
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        user.setCredentials(Collections.singletonList(credential));

        // Création dans Keycloak
        try (Response response = usersResource.create(user)) {
            int status = response.getStatus();
            if (status != 201) {
                String body = response.readEntity(String.class);
                throw new RuntimeException("Impossible de créer l'utilisateur dans Keycloak. Status=" + status + " Body=" + body);
            }

            // Récupération de l'ID depuis le header Location
            String locationHeader = response.getHeaderString("Location");
            String keycloakUserId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

            // Assignation du rôle realm
            assignRealmRole(realmResource, keycloakUserId, roleName);

            log.info("Utilisateur créé dans Keycloak: email={} role={} id={}", email, roleName, keycloakUserId);
            return keycloakUserId;
        }
    }

    /**
     * Assigne un rôle realm (ADMIN, CLIENT, FREELANCER) à un utilisateur Keycloak.
     */
    private void assignRealmRole(RealmResource realmResource, String userId, String roleName) {
        try {
            RoleRepresentation role = realmResource.roles().get(roleName.toUpperCase()).toRepresentation();
            realmResource.users().get(userId).roles().realmLevel()
                    .add(Collections.singletonList(role));
            log.info("Rôle '{}' assigné à l'utilisateur {}", roleName, userId);
        } catch (Exception e) {
            log.error("Erreur lors de l'assignation du rôle '{}': {}", roleName, e.getMessage());
            throw new RuntimeException("Rôle introuvable dans Keycloak : " + roleName);
        }
    }

    /**
     * Vérifie qu'un utilisateur avec cet email existe dans Keycloak et est activé.
     * Utilisé lors du login pour s'assurer que seuls les users Keycloak peuvent se connecter.
     */
    public boolean userExistsInKeycloak(String email) {
        List<UserRepresentation> users = keycloakAdmin.realm(realm).users()
                .searchByEmail(email, true); // exact match
        return users != null && !users.isEmpty() && Boolean.TRUE.equals(users.get(0).isEnabled());
    }

    /**
     * Récupère le rôle realm principal d'un utilisateur Keycloak.
     */
    public Optional<String> getUserRole(String email) {
        List<UserRepresentation> users = keycloakAdmin.realm(realm).users()
                .searchByEmail(email, true);

        if (users == null || users.isEmpty()) return Optional.empty();

        String userId = users.get(0).getId();
        List<RoleRepresentation> roles = keycloakAdmin.realm(realm).users()
                .get(userId).roles().realmLevel().listEffective();

        return roles.stream()
                .map(RoleRepresentation::getName)
                .filter(r -> r.equals("ADMIN") || r.equals("CLIENT") || r.equals("FREELANCER"))
                .findFirst();
    }

    /**
     * Désactive un utilisateur dans Keycloak (cohérence avec deactivate local).
     */
    public void disableUser(String email) {
        List<UserRepresentation> users = keycloakAdmin.realm(realm).users()
                .searchByEmail(email, true);
        if (users != null && !users.isEmpty()) {
            UserRepresentation user = users.get(0);
            user.setEnabled(false);
            keycloakAdmin.realm(realm).users().get(user.getId()).update(user);
            log.info("Utilisateur désactivé dans Keycloak: {}", email);
        }
    }
}