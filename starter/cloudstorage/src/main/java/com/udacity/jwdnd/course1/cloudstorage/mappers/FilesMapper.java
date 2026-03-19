package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.entities.Files;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface FilesMapper {
        @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) VALUES (#{filename}, #{contenttype}, #{filesize}, #{userid}, #{filedata})")
        @Options(useGeneratedKeys = true, keyProperty = "fileId")
        int insertFile(Integer fileId, String filename, String contenttype, String filesize, Integer userid, byte[] filedata);

        @Select("SELECT * FROM FILES WHERE fileId = #{fileId}")
        Files getFile(Integer fileId);

        @Select("SELECT * FROM FILES WHERE filename = #{filename}")
        Files getFileByName(String filename);

        @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
        int deleteFile(Integer fileId);
}
