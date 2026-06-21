package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class MensajeResponseDto {
    @SerializedName("mensaje")
    private String mensaje;

    public String getMensaje() { return mensaje; }
}
