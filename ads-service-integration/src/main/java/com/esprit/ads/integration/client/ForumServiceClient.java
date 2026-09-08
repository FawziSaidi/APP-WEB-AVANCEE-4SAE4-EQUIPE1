package com.esprit.ads.integration.client;

import com.esprit.ads.integration.config.FeignClientConfig;
import com.esprit.ads.integration.dto.ForumPostDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "forum-service",
        url = "${services.forum-service.url:http://forum-service:8083}",
        configuration = FeignClientConfig.class
)
public interface ForumServiceClient {

    @GetMapping("/posts")
    List<ForumPostDto> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size);

    @GetMapping("/posts/{id}")
    ForumPostDto getPostById(@PathVariable("id") Long postId);

    @GetMapping("/posts/user/{userId}")
    List<ForumPostDto> getPostsByUser(@PathVariable("userId") String userId);
}
