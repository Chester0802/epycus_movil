package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class GoogleAuthDto {
    @SerializedName("googleId")
    private String googleId;

    @SerializedName("correo")
    private String correo;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("fotoUrl")
    private String fotoUrl;

    public GoogleAuthDto(String googleId, String correo, String nombre, String fotoUrl) {
        this.googleId = googleId;
        this.correo = correo;
        this.nombre = nombre;
        this.fotoUrl = fotoUrl;
    }
}
