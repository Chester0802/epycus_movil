package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("respuesta")
    private String respuesta;

    @SerializedName("conversacionId")
    private String conversacionId;

    public String getRespuesta() { return respuesta; }
    public String getConversacionId() { return conversacionId; }
}
