package com.example.illook.mapper;

import com.example.illook.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {


    //댓글 달기
    @Insert("INSERT INTO comment VALUES(0, #{comment.comment},#{comment.createDate} , #{comment.parent}, #{comment.userIdx}, #{comment.postIdx})")
    @Options(useGeneratedKeys = true, keyProperty = "commentIdx")
    int comment(@Param("comment") Comment comment);

    @Select("SELECT * FROM ilook.comment c \n" +
            "INNER JOIN user u \n" +
            "ON c.user_user_idx = u.user_idx\n" +
            "WHERE comment_idx=#{commentId}")
    Map getComment( @Param("commentId") int commentId);

    //댓글 리스트

    //(답)댓글 삭제
    @Delete("DELETE FROM comment WHERE comment_idx=#{id}")
    void commentDelete(@Param("id") int id);

    @Select("SELECT comment_idx, user_idx, profile_image, nickname, comment, create_date FROM ilook.comment c\n" +
            "INNER JOIN user u\n" +
            "ON c.user_user_idx = u.user_idx\n" +
            "WHERE post_post_idx=#{postIdx} AND parent = #{parent} ORDER BY create_date;")
    List<Map> commentList(@Param("postIdx") int postIdx, @Param("parent") int parent);
}
