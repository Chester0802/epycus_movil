package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class DiarioEntradaResponse {
    @SerializedName("entrada")
    private EntradaDiarioDto entrada;

    public EntradaDiarioDto getEntrada() { return entrada; }
}
