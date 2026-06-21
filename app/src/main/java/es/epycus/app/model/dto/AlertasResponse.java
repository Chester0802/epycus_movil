package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class AlertasResponse {
    @SerializedName("alertas")
    private Object alertas;

    public Object getAlertas() { return alertas; }
}
