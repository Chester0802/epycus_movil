package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class RestablecerContrasenaDto {
    @SerializedName("token")
    private String token;

    @SerializedName("nuevaContrasena")
    private String nuevaContrasena;

    public RestablecerContrasenaDto(String token, String nuevaContrasena) {
        this.token = token;
        this.nuevaContrasena = nuevaContrasena;
    }
}
