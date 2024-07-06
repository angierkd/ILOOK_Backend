package com.example.illook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    int commentIdx;
    String comment;
    Date createDate;
    int parent;
    int userIdx;
    int postIdx;
}
