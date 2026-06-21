package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroCicloCompletadoResponse {
    @SerializedName("xpGanado")
    private int xpGanado;

    @SerializedName("sugerirDescanso")
    private boolean sugerirDescanso;

    @SerializedName("pausaActiva")
    private String pausaActiva;

    public int getXpGanado() { return xpGanado; }
    public boolean isSugerirDescanso() { return sugerirDescanso; }
    public String getPausaActiva() { return pausaActiva; }
}
