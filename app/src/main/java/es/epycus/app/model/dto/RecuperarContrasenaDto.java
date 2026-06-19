package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class RecuperarContrasenaDto {
    @SerializedName("correo")
    private String correo;

    public RecuperarContrasenaDto(String correo) {
        this.correo = correo;
    }
}
