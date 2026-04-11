package com.esprit.userservice.controllers;

import com.esprit.userservice.dto.UserEventDTO;
import com.esprit.userservice.entities.User;
import com.esprit.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getUserEmail(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id).getEmail());
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        service.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(@RequestParam("query") String query) {
        List<Map<String, Object>> result = service.searchByName(query).stream()
                .map(u -> Map.of(
                        "id",       (Object) u.getId(),
                        "name",     u.getName(),
                        "lastName", u.getLastName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> result = service.getAll().stream()
                .map(u -> Map.of(
                        "id",       (Object) u.getId(),
                        "name",     u.getName(),
                        "lastName", u.getLastName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint interne utilisé par publication-service au démarrage
     * pour charger tous les utilisateurs dans son cache local RabbitMQ.
     * Accessible sans token (inter-service uniquement).
     */
    @GetMapping("/all-for-cache")
    public ResponseEntity<List<UserEventDTO>> getAllForCache() {
        List<UserEventDTO> list = service.getAll().stream()
                .map(u -> new UserEventDTO(
                        u.getId(),
                        u.getName(),
                        u.getLastName(),
                        u.getEmail(),
                        u.isEnabled()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}