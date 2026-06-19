package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {
    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("conversacionId")
    private String conversacionId;

    public ChatRequest(String mensaje) {
        this.mensaje = mensaje;
    }

    public ChatRequest(String mensaje, String conversacionId) {
        this.mensaje = mensaje;
        this.conversacionId = conversacionId;
    }
}
