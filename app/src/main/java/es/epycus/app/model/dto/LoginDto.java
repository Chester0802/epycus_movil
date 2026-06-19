package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class LoginDto {
    @SerializedName("correo")
    private String correo;

    @SerializedName("contrasena")
    private String contrasena;

    public LoginDto(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }
}
