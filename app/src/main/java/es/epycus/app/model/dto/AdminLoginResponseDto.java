package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class AdminLoginResponseDto {
    @SerializedName("token")
    private String token;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("mensaje")
    private String mensaje;

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public String getMensaje() { return mensaje; }
}
