package com.example.illook.controller;

import com.example.illook.mapper.CommentMapper;
import com.example.illook.model.Comment;
import com.example.illook.model.User;
import com.example.illook.payload.Response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentMapper commentMapper;

    // 댓글 달기
    @PostMapping("comment")
    public ApiResponse comment(@AuthenticationPrincipal User user, @RequestBody Comment comment){

        comment.setCreateDate(new Date());
        comment.setUserIdx(Integer.parseInt(user.getUserIdx()));
        commentMapper.comment(comment);
        Map map = commentMapper.getComment(comment.getCommentIdx());
        return ApiResponse.createSuccess(map);
    }

    // 댓글 리스트
    @GetMapping("comment/list/{postIdx}/{parent}")
    public ApiResponse commentList(@PathVariable("postIdx") int postIdx, @PathVariable("parent") int parent){
        return ApiResponse.createSuccess(commentMapper.commentList(postIdx, parent));
    }


    // 댓글 삭제
    @DeleteMapping("comment/{id}")
    public ApiResponse commentDelete(@PathVariable int id){
        commentMapper.commentDelete(id);
        return ApiResponse.createSuccessWithNoContent();
    }
}
