package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class IaMensajesHoyResponse {
    @SerializedName("cantidad")
    private int cantidad;

    public int getCantidad() { return cantidad; }
}
