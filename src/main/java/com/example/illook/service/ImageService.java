package com.example.illook.service;

import com.example.illook.mapper.ImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {


    private final ImageMapper imageMapper;

    //이미지 개별 조회
    public List<String> getImagePath(int id){

        List<String> imagePathList = new ArrayList<>();
        imagePathList = imageMapper.getImage(id);

        return imagePathList;
    }


    String uploadPath =  new File("").getAbsolutePath() + File.separator + File.separator + "images" + File.separator + "20230119";

    public Path load(String filename) {
        return Paths.get(uploadPath).resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }
}
