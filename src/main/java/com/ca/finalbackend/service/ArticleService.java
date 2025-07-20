package com.ca.finalbackend.service;

import com.ca.finalbackend.model.Article;
import com.ca.finalbackend.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    public void write(Article article) {
        articleMapper.insertArticle(article);
    }

    public Article findById(Integer id) {
        return articleMapper.findById(id);
    }

    public List<Article> findAll() {
        return articleMapper.findAll();
    }

    public void update(Article article) {
        articleMapper.updateArticle(article);
    }

    public void delete(Integer id) {
        articleMapper.deleteArticle(id);
    }

    public void increaseReadCount(Integer id) {
        articleMapper.increaseReadCount(id);
    }
}
