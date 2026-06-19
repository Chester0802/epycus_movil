package es.epycus.app.model;

import com.google.gson.annotations.SerializedName;

public class RespuestaApi<T> {
    @SerializedName("exito")
    private boolean exito;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("datos")
    private T datos;

    @SerializedName("errores")
    private java.util.List<String> errores;

    public boolean isExito() { return exito; }
    public String getMensaje() { return mensaje; }
    public T getDatos() { return datos; }
    public java.util.List<String> getErrores() { return errores; }
}
