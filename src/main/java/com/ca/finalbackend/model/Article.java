package com.ca.finalbackend.model;

import lombok.Data;
import java.util.Date;

@Data
public class Article {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private Integer readCount;
    private Date createdAt;
    private String nickname;
}
