package com.example.illook.mapper;

import com.example.illook.model.Image;
import com.example.illook.model.Post;
import com.example.illook.model.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    //게시글 저장
    @Insert("INSERT INTO post " +
            "VALUES(0, #{post.content}, #{post.advertise}, #{post.createDate}, #{post.category}, #{post.deadline}, #{post.userIdx})")
    @Options(useGeneratedKeys = true, keyProperty = "postIdx")
    int savePost(@Param("post") Post post);

    //게시글 이미지 저장
    @Insert("INSERT INTO image(path, name, post_post_idx) VALUES(#{image.path}, #{image.name}, #{image.postIdx})")
    void saveImages(@Param("image") Image image);


    @Delete("DELETE FROM post WHERE post_idx=#{id}")
    void deletePost(@Param("id") int id);

    @Delete("DELETE FROM product WHERE post_post_idx=#{id};")
    void deleteProduct(@Param("id") int id);

    @Delete("DELETE FROM image WHERE post_post_idx=#{id};")
    void deleteImageP(@Param("id") int id);

    //게시글 수정

    //게시글 이미지 삭제
    @Delete("DELETE FROM image WHERE path=#{image.path} AND name=#{image.name}")
    void deleteImage(@Param("image") Image image);

    //게시글 이미지 추가
    @Insert("INSERT INTO image(path, name, post_post_idx) VALUES(#{image.path},#{image.name},#{image.postIdx})")
    void addImage(@Param("image") Image image);

    @Update("UPDATE post SET content=#{post.content},advertise=#{post.advertise} WHERE post_idx=#{post.postIdx}")
    void updatePost(@Param("post") Post post);

    @Insert("INSERT INTO product VALUES(0, #{product.category}, #{product.brand},#{product.name},#{product.size},#{postIdx}) ")
    void saveProduct(@Param("product") Product product, @Param("postIdx") int postIdx);

   /* @Select("SELECT * FROM post WHERE post_idx=#{id}")
    @Results({
            @Result(property = "profile", column = "content"),
            @Result(property = "nickname", column = "advertise"),
            @Result(property = "likes", column = "create_date"),
            @Result(property = "date", column = "category"),
            @Result(property = "userIdx", column = "user_user_idx")
    })
    PostDetial getPost(@Param("id") int id);*/

    //좋아요
    @Insert("INSERT INTO ilook.like VALUES(0,#{user},#{post})")
    void createLike(@Param("user") int user,@Param("post") int post);

    //좋아요 취소
    @Delete("DELETE FROM ilook.like WHERE user_user_idx=#{user} AND post_post_idx=#{post}")
    void deleteLike(@Param("user") int user,@Param("post") int post);


    @Select("SELECT deadline, category, path, post_idx FROM ilook.image i\n" +
            "INNER JOIN post p \n" +
            "WHERE i.post_post_idx = p.post_idx AND user_user_idx = #{id} AND path LIKE CONCAT('%', 'thumbnail', '%')")
    List<Map> getImage(@Param("id") int id);

   /* @Select("SELECT brand, name, size FROM post po, product pr WHERE po.post_idx = pr.post_post_idx AND post_idx=#{id}")
    @Results({
            @Result(property = "brand", column = "brand"),
            @Result(property = "name", column = "name"),
            @Result(property = "size", column = "size"),
    })

    List<Product> getProduct(@Param("id") int id);
*/

    //게시글 상세

    @Select("SELECT (EXISTS (SELECT like_idx FROM ilook.like WHERE user_user_idx=#{userIdx} AND post_post_idx=#{id} limit 1))\n" +
            "AS like_exsist,\n" +
            "(SELECT EXISTS (SELECT follow_idx FROM ilook.follow WHERE user_user_idx1=#{userIdx} AND user_user_idx2=\n" +
            "(SELECT user_user_idx FROM post WHERE post_idx=#{id}) limit 1))\n" +
            "AS follow_exsist,\n" +
            "(SELECT #{userIdx} = (SELECT user_user_idx FROM post WHERE post_idx=#{id}))\n" +
            "AS identification;")
    Map getPostDetail2(@Param("userIdx") String userIdx,@Param("id") int id);

    @Select("SELECT COUNT(l.like_idx) AS like_count,\n" +
            "       p.post_idx,\n" +
            "       p.advertise,\n" +
            "       p.user_user_idx,\n" +
            "       u.profile_image,\n" +
            "       u.nickname,\n" +
            "       DATE_FORMAT(p.create_date, '%Y.%m.%d') AS create_date,\n" +
            "       DATE_FORMAT(p.deadline, '%Y.%m.%d') AS deadline,\n" +
            "       p.content\n" +
            "FROM ilook.post p\n" +
            "INNER JOIN ilook.user u ON p.user_user_idx = u.user_idx\n" +
            "LEFT JOIN ilook.like l ON p.post_idx = l.post_post_idx\n" +
            "WHERE p.post_idx = #{id}\n" +
            "GROUP BY p.post_idx;")
    Map getPostDetail1(@Param("id") int id);

    @Select("SELECT product_idx, p1.category, brand, name, size FROM product p1\n" +
            "            JOIN post p2 ON p2.post_idx = p1.post_post_idx\n" +
            "            WHERE p2.post_idx = #{id}")
    List<Map> getProducts(@Param("id") int id);

    @Select("SELECT path FROM image WHERE post_post_idx = #{id}")
    List<Map> getImages(@Param("id") int id);

    //게시글 수정


}
