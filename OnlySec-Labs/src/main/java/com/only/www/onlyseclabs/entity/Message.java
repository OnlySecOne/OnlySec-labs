package com.only.www.onlyseclabs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Message {
    private int id;
    private String content;
    private LocalDateTime createTime;
    private int userId;
}
