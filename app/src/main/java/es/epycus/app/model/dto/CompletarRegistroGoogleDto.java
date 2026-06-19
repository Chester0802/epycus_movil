package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class CompletarRegistroGoogleDto {
    @SerializedName("nombre")
    private String nombre;

    @SerializedName("correoElectronico")
    private String correoElectronico;

    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    @SerializedName("genero")
    private String genero;

    @SerializedName("carreraId")
    private int carreraId;

    @SerializedName("aceptoTerminos")
    private boolean aceptoTerminos;

    @SerializedName("googleId")
    private String googleId;

    @SerializedName("fotoGoogleUrl")
    private String fotoGoogleUrl;

    public CompletarRegistroGoogleDto(String nombre, String correoElectronico,
                                       String fechaNacimiento, String genero,
                                       int carreraId, boolean aceptoTerminos,
                                       String googleId, String fotoGoogleUrl) {
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.carreraId = carreraId;
        this.aceptoTerminos = aceptoTerminos;
        this.googleId = googleId;
        this.fotoGoogleUrl = fotoGoogleUrl;
    }
}
