package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class RefreshDto {
    @SerializedName("refreshToken")
    private String refreshToken;

    public RefreshDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
