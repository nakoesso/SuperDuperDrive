package com.udacity.jwdnd.course1.cloudstorage.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notes {
    private Integer noteid;
    private String notetitle;
    private String notedescription;
    private Integer userid;
}
