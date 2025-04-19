package com.only.www.onlyseclabs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {
    private String  id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private Integer status;
    private String CreateTime;

}
