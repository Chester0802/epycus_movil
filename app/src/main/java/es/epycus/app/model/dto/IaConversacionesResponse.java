package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class IaConversacionesResponse {
    @SerializedName("conversaciones")
    private Object conversaciones;

    public Object getConversaciones() { return conversaciones; }
}
