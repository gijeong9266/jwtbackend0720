package com.ca.finalbackend.mapper;

import com.ca.finalbackend.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ArticleMapper {
    void insertArticle(Article article);
    Article findById(@Param("id") Integer id);
    List<Article> findAll();
    void updateArticle(Article article);
    void deleteArticle(@Param("id") Integer id);
    void increaseReadCount(@Param("id") Integer id);
}
