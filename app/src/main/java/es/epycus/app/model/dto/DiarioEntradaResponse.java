package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class DiarioEntradaResponse {
    @SerializedName("entrada")
    private Object entrada;

    public Object getEntrada() { return entrada; }
}
