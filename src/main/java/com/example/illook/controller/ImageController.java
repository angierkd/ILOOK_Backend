package com.example.illook.controller;

import com.example.illook.mapper.ImageMapper;
import com.example.illook.mapper.PostMapper;
import com.example.illook.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ImageController {


    private final ImageMapper imageMapper;
    private final ImageService imageService;
    private final PostMapper postMapper;

    @GetMapping(value = "/image/{id}")
    public ResponseEntity<List<byte[]>> getImage(@PathVariable int id) throws IOException {

        List<String> ImagePathList = new ArrayList<>();
        ImagePathList = imageService.getImagePath(id);
        String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

        List<byte[]> imageByteArrayList = new ArrayList<>();
        for(String imagePath : ImagePathList) {
            InputStream imageStream = new FileInputStream(absolutePath + imagePath);
            byte[] imageByteArray = IOUtils.toByteArray(imageStream);
            imageByteArrayList.add(imageByteArray);
            imageStream.close();
        }


        return new ResponseEntity<>(imageByteArrayList, HttpStatus.OK);
    }

    //사진이 저장된 url
    @RequestMapping(value = "/pictures", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody
    byte[] getImageWithMediaType(HttpServletRequest request, @RequestParam String url) throws IOException {

        String check_id = request.getParameter("id");
        Map<String, Object> param = new HashMap<String, Object>();

        //%2F 슬래시 대신로 전달 받아야함..!!
        String res = "C:\\Users\\82107\\Downloads\\illook\\illook\\"+url;

        InputStream in = new FileInputStream(res);

        return IOUtils.toByteArray(in);
    }

}
