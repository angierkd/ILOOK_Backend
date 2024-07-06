package com.example.illook.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ImageMapper {

    @Select("SELECT path FROM image WHERE post_post_idx=#{id}")
    List<String> getImage(@Param("id") int id);

    //날짜 순서대로
    @Select("SELECT post_idx, path, DATEDIFF(deadline, now()) AS date FROM ilook.post p\n" +
            "INNER JOIN image i\n" +
            "ON p.post_idx = i.post_post_idx\n" +
            "WHERE category=#{category} AND path LIKE CONCAT('%', 'thumbnail', '%')\n" +
            "ORDER BY create_date desc LIMIT #{offset},#{limit}")
    List<Map> getMainImages(@Param("category") String category,@Param("offset") int offset,@Param("limit") int limit);


    @Select("SELECT path FROM post p LEFT JOIN image i ON p.post_idx = i.post_post_idx WHERE p.post_idx = #{id};")
    List<Map> getImagePost(@Param("id") int id);

    @Select("SELECT thumbnail FROM post WHERE user_user_idx=#{id}")
    List<Map> getImageProfile(@Param("id") int id);
}
