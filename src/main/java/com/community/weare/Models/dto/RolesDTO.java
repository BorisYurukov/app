package com.community.weare.Models.dto;

public class RolesDTO {

   private String authority;

    public RolesDTO(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
