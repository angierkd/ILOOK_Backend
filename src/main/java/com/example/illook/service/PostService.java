package com.example.illook.service;

import com.example.illook.mapper.PostMapper;
import com.example.illook.model.Image;
import com.example.illook.model.Post;
import com.example.illook.model.Product;
import com.example.illook.model.User;
import com.example.illook.payload.BoardRequestDto.BoardFileVo;
import com.example.illook.payload.BoardRequestDto.PickFileVo;
import com.example.illook.util.FileHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final FileHandler fileHandler;
    private final PostMapper postMapper;

  /*  public Long updatePost(Post post, List<MultipartFile> files, int id) throws Exception{

        List<Image> addImages = new ArrayList<>();
        List<Image> imageList = mapper.findImageList(id);

        for(Image image : imageList){
            String imageName = image.getName();
            if(!files.contains(imageName)){
                System.out.println("aaaaaaa");
                mapper.deleteImage(image);
            }
        }

        List<Image> imageList2  = fileHandler.parseFileInfo(files, id);
        for(Image image : imageList){
            if(!imageList.contains(image)){
                System.out.println("bbbbbbbbbbbbb");
                mapper.addImage(image);
            }
        }

        for(Image image : imageList){
            mapper.deleteImage(image);
        }
        for(Image image : fileHandler.parseFileInfo(files, id)){
            mapper.saveImages(image);
        }
        mapper.updatePost(post, id);

        return 1L;
    }*/


    @Transactional
    public List<Image> createImage(List<MultipartFile> files, String key) throws Exception {
        //DB는 롤백이 가능하지만 저장된 파일은 롤백이 안됨 -> 삭제처리 필요
        List<Image> imageList = fileHandler.parseFileInfo(files, Integer.parseInt(key));
            for (Image image : imageList) {
                postMapper.saveImages(image);
            }
        return imageList;

    }

    @Transactional
    public int createOOTD(BoardFileVo boardFileVo, int userIdx) {

        Post post = Post.builder()
                .userIdx(userIdx)
                .content(boardFileVo.getContent())
                .createDate(new Date())
                .deadline(new Date())
                .category(boardFileVo.getCategory())
                .advertise(boardFileVo.getAdvertise())
                .build();
        postMapper.savePost(post);
        for (Product product : boardFileVo.getProducts()) {
            postMapper.saveProduct(product, post.getPostIdx());
        }
        return post.getPostIdx();
    }

    @Transactional
    public int createPICK(PickFileVo pickFileVo, int userIdx) {

        Post post = Post.builder()
                .userIdx(userIdx)
                .content(pickFileVo.getContent())
                .createDate(new Date())
                .deadline(pickFileVo.getDate())
                .category(pickFileVo.getCategory())
                .advertise(-1)
                .build();

        postMapper.savePost(post);
        return post.getPostIdx();
    }

    public void updatePost(BoardFileVo boardFileVo) {
        //내용, 광고성 수정
        Post post = Post.builder()
                .postIdx(boardFileVo.getPostIdx())
                .content(boardFileVo.getContent())
                .advertise(boardFileVo.getAdvertise())
                .build();

        System.out.println(post);
        postMapper.updatePost(post);

        //착용상품 수정(삭제,생성)

        postMapper.deleteProduct(post.getPostIdx());
        for (Product product : boardFileVo.getProducts()) {
            postMapper.saveProduct(product, post.getPostIdx());
        }

    }

    public void updateImage(List<MultipartFile> files, String key) throws Exception {
        //이미지 삭제 및 저장
        postMapper.deleteImageP(Integer.parseInt(key));
        createImage(files, key);
    }

    //게시글 상세 얻어오기
    public Map getPostDetail(User user, int id) {

        log.info("User is "+user+"(if null, user is guest)");

        Map postDetail = postMapper.getPostDetail1(id);
        List<Map> postProducts = postMapper.getProducts(id);
        List<Map> postImages = postMapper.getImages(id);

        if(user != null){
            //게스트가 아닐 경우
            Map postDetail2 = postMapper.getPostDetail2(user.getUserIdx(), id);
            postDetail.putAll(postDetail2);
        }

        postDetail.put("product", postProducts);
        postDetail.put("image", postImages);

        return postDetail;
    }

}
