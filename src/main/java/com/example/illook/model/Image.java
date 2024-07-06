package com.example.illook.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image {

    int imageIdx;
    String path;
    String name;
    int postIdx;
}
