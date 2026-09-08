package com.esprit.projectservice.services;


import com.esprit.projectservice.dto.UserEventDTO;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserCacheService {
    private final Map<Integer, UserEventDTO> cache = new ConcurrentHashMap<>();

    public void put(UserEventDTO dto) {
        cache.put(dto.getId(), dto);
    }

    public Optional<UserEventDTO> get(Integer userId) {
        return Optional.ofNullable(cache.get(userId));
    }
}