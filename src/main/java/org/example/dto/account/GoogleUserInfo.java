package org.example.dto.account;

import lombok.Data;

@Data
public class GoogleUserInfo {
    private String email;
    private String family_name;
    private String given_name;
    private String id;
    private String locale;
    private String name;
    private String picture;
}