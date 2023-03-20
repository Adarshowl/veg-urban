package wrteam.ecart.shop.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QRCodeInitResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private QRCodeResponse qrCodeResponse;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public QRCodeResponse getQrCodeResponse() {
        return qrCodeResponse;
    }

    public void setQrCodeResponse(QRCodeResponse qrCodeResponse) {
        this.qrCodeResponse = qrCodeResponse;
    }
}
