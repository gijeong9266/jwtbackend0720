package com.ca.finalbackend.request;

import lombok.Data;
import java.util.Date;

@Data
public class UserRequest {
    private String accountName;
    private String password;
    private String nickname;
    private String email;
    private String gender;
    private Date birth;
    private String phone;
}
