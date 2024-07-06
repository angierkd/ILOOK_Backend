package com.example.illook.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface FollowMapper {

    //팔로우
    @Insert("INSERT INTO follow VALUES(0,#{user},#{follower})")
    void follow(@Param("user") int user, @Param("follower") int follower);

    @Select("SELECT exists (select * FROM follow f WHERE f.user_user_idx1=#{user} AND f.user_user_idx2 =#{follower} limit 1) as success;")
    int followState(@Param("user") int user, @Param("follower") int follower);

    @Select("SELECT user_idx, profile_image, nickname FROM ilook.follow f, user u WHERE f.user_user_idx2 = u.user_idx AND user_user_idx1=#{id};")
    List<Map> followList(@Param("id") int id);

    @Select("SELECT user_idx, profile_image, nickname FROM ilook.follow f, user u WHERE f.user_user_idx1 = u.user_idx AND user_user_idx2=#{id}")
    List<Map> followingList(@Param("id") int id);

    @Delete("DELETE FROM follow WHERE user_user_idx1 =#{user} AND user_user_idx2=#{follower}")
    void followDelete(@Param("user") int user,@Param("follower") int follower);

}
