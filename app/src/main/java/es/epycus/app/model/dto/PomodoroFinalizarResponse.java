package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroFinalizarResponse {
    @SerializedName("xpTotal")
    private int xpTotal;

    @SerializedName("sesionGuardada")
    private boolean sesionGuardada;

    public int getXpTotal() { return xpTotal; }
    public boolean isSesionGuardada() { return sesionGuardada; }
}
