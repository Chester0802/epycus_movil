package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class EntradaDiarioDto {
    @SerializedName("id")
    private int id;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("estadoAnimo")
    private int estadoAnimo;

    @SerializedName("nivelEnergia")
    private int nivelEnergia;

    @SerializedName("diarioTexto")
    private String diarioTexto;

    @SerializedName("preguntaGuia")
    private String preguntaGuia;

    @SerializedName("respuestaGuia")
    private String respuestaGuia;

    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public int getEstadoAnimo() { return estadoAnimo; }
    public int getNivelEnergia() { return nivelEnergia; }
    public String getDiarioTexto() { return diarioTexto; }
    public String getPreguntaGuia() { return preguntaGuia; }
    public String getRespuestaGuia() { return respuestaGuia; }
}
