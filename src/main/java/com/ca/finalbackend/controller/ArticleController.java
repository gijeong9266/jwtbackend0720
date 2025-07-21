package com.ca.finalbackend.controller;

import com.ca.finalbackend.model.Article;
import com.ca.finalbackend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ca.finalbackend.request.ArticleRequest;
import com.ca.finalbackend.response.ApiResponse;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping("")
    public ResponseEntity<?> write(@AuthenticationPrincipal Integer userId, @RequestBody ArticleRequest request) {
        Article article = new Article();
        article.setUserId(userId);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setReadCount(0);
        articleService.write(article);
        return ResponseEntity.ok(new ApiResponse(true, "글 작성 성공"));
    }

    @GetMapping("")
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
        Article article = articleService.findById(id);
        if (article == null) return ResponseEntity.status(404).body(new ApiResponse(false, "글을 찾을 수 없습니다."));
        articleService.increaseReadCount(id);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @AuthenticationPrincipal Integer userId, @RequestBody ArticleRequest request) {
        Article article = articleService.findById(id);
        if (article == null) return ResponseEntity.status(404).body(new ApiResponse(false, "글을 찾을 수 없습니다."));
        // 필요시 userId로 권한 체크 가능
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        articleService.update(article);
        return ResponseEntity.ok(new ApiResponse(true, "글 수정 성공"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id, @AuthenticationPrincipal Integer userId) {
        // 필요시 userId로 권한 체크 가능
        articleService.delete(id);
        return ResponseEntity.ok(new ApiResponse(true, "글 삭제 성공"));
    }
}
