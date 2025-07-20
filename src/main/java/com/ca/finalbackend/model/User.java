package com.ca.finalbackend.model;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Integer id;
    private String accountName;
    private String password;
    private String nickname;
    private String email;
    private String gender;
    private Date birth;
    private String phone;
    private Date createdAt;
}
