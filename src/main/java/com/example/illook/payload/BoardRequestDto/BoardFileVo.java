package com.example.illook.payload.BoardRequestDto;

import com.example.illook.model.Product;
import lombok.Data;

import java.util.List;

@Data
public class BoardFileVo {

    private int postIdx;
    private String content;
    private int advertise;
    private String category;
    private List<Product> products;
}
