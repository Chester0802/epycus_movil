package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class MisionCompletarResponse {
    @SerializedName("xpGanado")
    private int xpGanado;

    public int getXpGanado() { return xpGanado; }
}
