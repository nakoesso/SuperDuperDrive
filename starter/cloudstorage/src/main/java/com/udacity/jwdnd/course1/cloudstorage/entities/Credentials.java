package com.udacity.jwdnd.course1.cloudstorage.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {
    private Integer credentialid;
    private String url;
    private String username;
    private String encryptionkey;
    private String password;
    private Integer userid;
}
