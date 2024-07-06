package com.example.illook.mapper;

import com.example.illook.model.User;
import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user(id,email,password,nickname,profile_image,role) VALUES(#{user.id},#{user.email},#{user.password},#{user.nickname}, #{user.profileImage},#{user.role})")
    int saveUser(@Param("user") User user);


    @Select("SELECT * FROM user WHERE id=#{id}")
    Boolean checkIdDuplication(@Param("id") String id);

    @Select("SELECT * FROM user WHERE nickname=#{nickname}")
    Boolean checkNicknameDuplicate(String nickname);

    @Select("SELECT * FROM user WHERE email=#{email}")
    Boolean checkEmailDuplicate(String email);

    //회원탈퇴
    @Delete("DELETE FROM user WHERE user_idx=#{id}")
    int deleteUser(@Param("id") int id);


    @Select("SELECT * FROM USER WHERE id=#{username}")
    User findById(@Param("username") String username);

    @Select("SELECT id, role, refresh_token FROM USER WHERE id=#{id}")
    Map getUserRefreshToken(@Param("id") String id);

    @Insert("INSERT INTO user(email,nickname) VALUES(#{user.email},#{user.nickname})")
    void saveOAuth2User(@Param("user") User user);

    @Insert("UPDATE user SET refresh_token=#{refreshToken} WHERE id=#{id}")
    void saveRefreshToken(@Param("refreshToken") String refreshToken, @Param("id") String id);

    @Select("SELECT email FROM user WHERE id=#{userPk}")
    String findEmail(@Param("userPk") String userPK);

    @Select("SELECT id FROM user WHERE email=#{email}")
    String findId(@Param("email") String email);

    @Select("SELECT refresh_token WHERE id=#{id} AND refresh_token=#{refreshToken}")
    boolean existRefreshToken(String refreshToken);

    //유저 프로필 가져오기
    @Select("SELECT user_idx, profile_image, nickname, count(post_idx) AS postCnt,\n" +
            "(SELECT count(user_user_idx2) FROM follow WHERE follow.user_user_idx1=#{userIdx}) AS followerCnt,\n" +
            "(SELECT count(user_user_idx1) FROM follow WHERE follow.user_user_idx2=#{userIdx}) AS followedCnt\n" +
            "FROM ilook.user u\n" +
            "LEFT JOIN post p\n" +
            "ON u.user_idx = p.user_user_idx\n" +
            "WHERE user_idx = #{userIdx};")
    Map getUserProfile(@Param("userIdx") int userIdx);


    //사용자 수정
    @Update("UPDATE user SET nickname=#{nickname}, profile_image=#{image} WHERE user_idx=#{id}")
    void updateUser(@Param("nickname") String nickname,@Param("image") String image,@Param("id") int id);

    //비밀번호 변경
    @Update("UPDATE user SET password=#{encode} WHERE id=#{id}")
    void changePwd(@Param("encode") String encode,@Param("id") String id);

    @Select("SELECT * FROM ilook.user WHERE id=#{id} AND email=#{email}")
    Boolean checkUser(@Param("email") String email,@Param("id")  String id);

    @Select("SELECT exists (SELECT count(user_user_idx1) FROM ilook.follow WHERE follow.user_user_idx2=#{profileUserIdx} AND user_user_idx1=#{userIdx}) AS follow;")
    int getFollow(@Param("profileUserIdx") int profileUserIdx, @Param("userIdx") int userIdx);
}
