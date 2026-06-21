package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class SuccessResponseDto {
    @SerializedName("success")
    private boolean success;

    public boolean isSuccess() { return success; }
}
