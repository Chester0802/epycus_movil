package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class RegistroRequestDto {
    @SerializedName("nombre")
    private String nombre;

    @SerializedName("correoElectronico")
    private String correoElectronico;

    @SerializedName("contrasena")
    private String contrasena;

    @SerializedName("confirmarContrasena")
    private String confirmarContrasena;

    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    @SerializedName("genero")
    private String genero;

    @SerializedName("carreraId")
    private int carreraId;

    @SerializedName("aceptoTerminos")
    private boolean aceptoTerminos;

    public RegistroRequestDto(String nombre, String correoElectronico, String contrasena,
                              String confirmarContrasena, String fechaNacimiento,
                              String genero, int carreraId, boolean aceptoTerminos) {
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
        this.confirmarContrasena = confirmarContrasena;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.carreraId = carreraId;
        this.aceptoTerminos = aceptoTerminos;
    }
}
