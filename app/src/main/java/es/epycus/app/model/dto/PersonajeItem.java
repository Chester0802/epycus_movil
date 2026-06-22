package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PersonajeItem {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("genero")
    private String genero;

    @SerializedName("esSeleccionado")
    private boolean esSeleccionado;

    @SerializedName("esPlaceholder")
    private boolean esPlaceholder;

    @SerializedName("imagenPreviewUrl")
    private String imagenPreviewUrl;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getGenero() { return genero; }
    public boolean isEsSeleccionado() { return esSeleccionado; }
    public boolean isEsPlaceholder() { return esPlaceholder; }
    public String getImagenPreviewUrl() { return imagenPreviewUrl; }
}
