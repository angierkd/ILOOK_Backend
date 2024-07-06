package com.example.illook.util;

import com.example.illook.model.Image;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

@Component
public class FileHandler {

    public List<Image> parseFileInfo(List<MultipartFile> multipartFiles, Integer postIdx)throws Exception {

        // 반환할 파일 리스트
        List<Image> fileList = new ArrayList<>();

        // 전달되어 온 파일이 존재할 경우
        if(!CollectionUtils.isEmpty(multipartFiles)) {
            // 파일명을 업로드 한 날짜로 변환하여 저장'
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern("yyyyMMdd");
            String current_date = now.format(dateTimeFormatter);

            // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
            // 경로 구분자 File.separator 사용
            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;
            System.out.println("absolutePaht"+absolutePath);

            // 파일을 저장할 세부 경로 지정
            String path = "images" + File.separator + current_date;
            String thumbnailPath = "images"  + File.separator + "thumbnail" + File.separator + current_date;
            String profilePath = "images" + File.separator + "profile" + File.separator + current_date;
            File file = new File(path);
            File fileT = new File(thumbnailPath);
            File fileP = new File(profilePath);

            if(!fileP.exists()) {
                boolean wasSuccessful = fileP.mkdirs();

                // 디렉터리 생성에 실패했을 경우
                if(!wasSuccessful)
                    System.out.println("file: was not successful");
            }

            if(!fileT.exists()) {
                boolean wasSuccessful = fileT.mkdirs();

                // 디렉터리 생성에 실패했을 경우
                if(!wasSuccessful)
                    System.out.println("file: was not successful");
            }

            // 디렉터리가 존재하지 않을 경우
            if(!file.exists()) {
                boolean wasSuccessful = file.mkdirs();

                // 디렉터리 생성에 실패했을 경우
                if(!wasSuccessful)
                    System.out.println("file: was not successful");
            }


            // 다중 파일 처리
            for(int i = 0; i<multipartFiles.size(); i++) {

                MultipartFile multipartFile = multipartFiles.get(i);
                // 파일의 확장자 추출
                String originalFileExtension;
                String contentType = multipartFile.getContentType();

                // 확장자명이 존재하지 않을 경우 처리 x
                if(ObjectUtils.isEmpty(contentType)) {
                    break;
                }
                else {  // 확장자가 jpeg, png인 파일들만 받아서 처리
                    if(contentType.contains("image/jpeg"))
                        originalFileExtension = ".jpg";
                    else if(contentType.contains("image/png"))
                        originalFileExtension = ".png";
                    else  // 다른 확장자일 경우 처리 x
                        break;
                }

                // 파일명 중복 피하고자 나노초까지 얻어와 지정
                String new_file_name = System.nanoTime() + originalFileExtension;

                Image image;

                if(postIdx == null) {
                    image = Image.builder()
                            .name(multipartFile.getOriginalFilename())
                            .path(profilePath + File.separator + new_file_name)
                            .build();
                }else {
                    if (i == 0) {
                        image = Image.builder()
                                .name(multipartFile.getOriginalFilename())
                                .path(thumbnailPath + File.separator + new_file_name)
                                .postIdx(postIdx)
                                .build();

                    } else {
                        image = Image.builder()
                                .name(multipartFile.getOriginalFilename())
                                .path(path + File.separator + new_file_name)
                                .postIdx(postIdx)
                                .build();
                    }
                }
                fileList.add(image);

                // 업로드 한 파일 데이터를 지정한 파일에 저장 //업로드 해버림
                if (postIdx == null) {
                    file = new File(absolutePath + profilePath + File.separator + new_file_name);
                }else{
                if(i == 0){
                    file = new File(absolutePath + thumbnailPath + File.separator + new_file_name);
                }else {
                    file = new File(absolutePath + path + File.separator + new_file_name);
                }
                }
                multipartFile.transferTo(file);

                // 파일 권한 설정(쓰기, 읽기)
                file.setWritable(true);
                file.setReadable(true);
            }
        }

        return fileList;
    }

    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }
}
