package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class RegistroSemanaDto {
    @SerializedName("dia")
    private String dia;

    @SerializedName("estado")
    private String estado;

    public String getDia() { return dia; }
    public String getEstado() { return estado; }
}
