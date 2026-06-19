package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PerfilResponse {
    @SerializedName("perfil")
    private Perfil perfil;

    @SerializedName("imagenPersonaje")
    private String imagenPersonaje;

    public Perfil getPerfil() { return perfil; }
    public String getImagenPersonaje() { return imagenPersonaje; }

    public static class Perfil {
        @SerializedName("nombre")
        private String nombre;

        @SerializedName("correoElectronico")
        private String correoElectronico;

        @SerializedName("codigoUnico")
        private String codigoUnico;

        @SerializedName("genero")
        private String genero;

        @SerializedName("carreraNombre")
        private String carreraNombre;

        @SerializedName("nivelActual")
        private int nivelActual;

        @SerializedName("xpTotal")
        private int xpTotal;

        @SerializedName("rachaActual")
        private int rachaActual;

        @SerializedName("rachaMaxima")
        private int rachaMaxima;

        @SerializedName("fechaRegistro")
        private String fechaRegistro;

        public String getNombre() { return nombre; }
        public String getCorreoElectronico() { return correoElectronico; }
        public String getCodigoUnico() { return codigoUnico; }
        public String getGenero() { return genero; }
        public String getCarreraNombre() { return carreraNombre; }
        public int getNivelActual() { return nivelActual; }
        public int getXpTotal() { return xpTotal; }
        public int getRachaActual() { return rachaActual; }
        public int getRachaMaxima() { return rachaMaxima; }
        public String getFechaRegistro() { return fechaRegistro; }
    }
}
