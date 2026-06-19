package es.epycus.app.model.entidades;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    @SerializedName("id")
    private int id;

    @SerializedName("codigoUnico")
    private String codigoUnico;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("correoElectronico")
    private String correoElectronico;

    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    @SerializedName("genero")
    private String genero;

    @SerializedName("correoVerificado")
    private boolean correoVerificado;

    @SerializedName("aceptoTerminos")
    private boolean aceptoTerminos;

    @SerializedName("estaActivo")
    private boolean estaActivo;

    @SerializedName("fechaRegistro")
    private String fechaRegistro;

    @SerializedName("ultimoAcceso")
    private String ultimoAcceso;

    @SerializedName("googleId")
    private String googleId;

    @SerializedName("fotoGoogleUrl")
    private String fotoGoogleUrl;

    @SerializedName("rolId")
    private int rolId;

    @SerializedName("carreraId")
    private int carreraId;

    public int getId() { return id; }
    public String getCodigoUnico() { return codigoUnico; }
    public String getNombre() { return nombre; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getGenero() { return genero; }
    public boolean isCorreoVerificado() { return correoVerificado; }
    public boolean isEstaActivo() { return estaActivo; }
    public String getFechaRegistro() { return fechaRegistro; }
    public String getUltimoAcceso() { return ultimoAcceso; }
    public String getGoogleId() { return googleId; }
    public String getFotoGoogleUrl() { return fotoGoogleUrl; }
    public int getRolId() { return rolId; }
    public int getCarreraId() { return carreraId; }
}
