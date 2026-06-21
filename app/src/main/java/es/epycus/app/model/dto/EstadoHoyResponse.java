package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class EstadoHoyResponse {
    @SerializedName("estado")
    private Object estado;

    public Object getEstado() { return estado; }
}
