package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class IaHistorialResponse {
    @SerializedName("historial")
    private Object historial;

    public Object getHistorial() { return historial; }
}
