package com.huyntd.superapp.gundamshopmobilefe.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AuthenticationRequest {

    // cái này dùng khi phía server yêu cầu tên khác thì đổi lại
    @SerializedName("email")
    String email;

    String password;

}
