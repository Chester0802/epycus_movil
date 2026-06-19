package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PreguntaGuiaResponse {
    @SerializedName("pregunta")
    private String pregunta;

    public String getPregunta() { return pregunta; }
}
