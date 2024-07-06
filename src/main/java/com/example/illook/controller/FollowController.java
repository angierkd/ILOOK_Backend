package com.example.illook.controller;

import com.example.illook.mapper.FollowMapper;
import com.example.illook.model.User;
import com.example.illook.payload.Response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowMapper followMapper;

    //팔로우
    @PostMapping("follow")
    public ApiResponse follow(@AuthenticationPrincipal User user, @RequestParam int follower){

        followMapper.follow(Integer.parseInt(user.getUserIdx()), follower);
        return ApiResponse.createSuccessWithNoContent();
    }

    @GetMapping("follow1")
    public ApiResponse follow1(@AuthenticationPrincipal User user, @RequestParam int follower){

        followMapper.follow(Integer.parseInt(user.getUserIdx()), follower);
        return ApiResponse.createSuccessWithNoContent();
    }

    //팔로우 취소
    @DeleteMapping("follow")
    public ApiResponse followDelete(@AuthenticationPrincipal User user, @RequestParam int follower){

        followMapper.followDelete(Integer.parseInt(user.getUserIdx()), follower);
        return ApiResponse.createSuccessWithNoContent();
    }

    //팔로우 여부
    @GetMapping("follow/state")
    public ApiResponse followState(@RequestParam int user, @RequestParam int follower){
        return ApiResponse.createSuccess(followMapper.followState(user, follower));
    }

    //팔로우 목록
    @GetMapping("follow/list/{id}")
    public ApiResponse followList(@PathVariable("id") int id){

        return ApiResponse.createSuccess(followMapper.followList(id));
    }

    //팔로잉 목록
    @GetMapping("following/list/{id}")
    public ApiResponse followingList(@PathVariable("id") int id){

        return ApiResponse.createSuccess(followMapper.followingList(id));
    }

}