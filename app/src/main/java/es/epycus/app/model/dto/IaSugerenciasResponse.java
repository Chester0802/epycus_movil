package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class IaSugerenciasResponse {
    @SerializedName("sugerencias")
    private Object sugerencias;

    public Object getSugerencias() { return sugerencias; }
}
