package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class DiarioPromedioMesResponse {
    @SerializedName("promedio")
    private Double promedio;

    public Double getPromedio() { return promedio; }
}
