package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroTipResponse {
    @SerializedName("consejo")
    private String consejo;

    public String getConsejo() { return consejo; }
}
