package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.entities.Credentials;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CredentialsMapper {

    @Insert("INSERT INTO CREDENTIALS (url, username, encryptionkey, password, userid) VALUES (#{url}, #{username}, #{encryptionkey}, #{password}, #{userid})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialid")
    int insert(Credentials credential);

    @Select("SELECT * FROM CREDENTIALS WHERE credentialid = #{credentialid}")
    Credentials getCredential(Integer credentialid);

    @Select("SELECT * FROM CREDENTIALS WHERE userid = #{userid}")
    List<Credentials> getCredentialsByUser(Integer userid);

    @Update("UPDATE CREDENTIALS SET url = #{url}, username = #{username}, encryptionkey = #{encryptionkey}, password = #{password} WHERE credentialid = #{credentialid}")
    int update(Credentials credential);

    @Delete("DELETE FROM CREDENTIALS WHERE credentialid = #{credentialid}")
    int delete(Integer credentialid);
}
