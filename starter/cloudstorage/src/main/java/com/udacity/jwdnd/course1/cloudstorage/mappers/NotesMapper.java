package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.entities.Notes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotesMapper {

        @Insert("INSERT INTO NOTES (notetitle, notedescription, userid) VALUES (#{notetitle}, #{notedescription}, #{userid})")
        @Options(useGeneratedKeys = true, keyProperty = "noteid")
        int insert(Notes note);

        @Select("SELECT * FROM NOTES WHERE noteid = #{noteid}")
        Notes getNote(Integer noteid);

        @Select("SELECT * FROM NOTES WHERE userid = #{userid}")
        List<Notes> getNotesByUser(Integer userid);

        @Update("UPDATE NOTES SET notetitle = #{notetitle}, notedescription = #{notedescription} WHERE noteid = #{noteid}")
        int update(Notes note);

        @Delete("DELETE FROM NOTES WHERE noteid = #{noteid}")
        int delete(Integer noteid);
}
