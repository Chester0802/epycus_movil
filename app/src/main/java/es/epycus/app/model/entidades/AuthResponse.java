package es.epycus.app.model.entidades;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
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
