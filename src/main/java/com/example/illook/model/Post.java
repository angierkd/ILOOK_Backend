package com.example.illook.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Post {

    int postIdx;
    String thumbnail;
    String content;
    int advertise;
    Date createDate;
    String category;
    Date deadline;
    int userIdx;

}
