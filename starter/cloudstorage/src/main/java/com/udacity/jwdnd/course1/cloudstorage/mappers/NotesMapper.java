package com.udacity.jwdnd.course1.cloudstorage.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface NotesMapper {
        @Insert("INSERT INTO NOTES (notetitle, notedescription, userid) VALUES (#{notetitle}, #{notedescription}, #{userid})")
        @Options(useGeneratedKeys = true, keyProperty = "noteid")
        int insert(NotesMapper notes);

        NotesMapper getNote(Integer noteid);
        int delete(Integer noteid);
        int update(NotesMapper notes);
}
