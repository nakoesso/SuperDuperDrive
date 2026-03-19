package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.entities.Files;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FilesMapper {

        @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) VALUES (#{filename}, #{contenttype}, #{filesize}, #{userid}, #{filedata})")
        @Options(useGeneratedKeys = true, keyProperty = "fileId")
        int insert(Files file);

        @Select("SELECT * FROM FILES WHERE fileId = #{fileId}")
        Files getFile(Integer fileId);

        @Select("SELECT * FROM FILES WHERE filename = #{filename} AND userid = #{userid}")
        Files getFileByNameAndUser(@Param("filename") String filename, @Param("userid") Integer userid);

        @Select("SELECT * FROM FILES WHERE userid = #{userid}")
        List<Files> getFilesByUser(Integer userid);

        @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
        int delete(Integer fileId);
}
