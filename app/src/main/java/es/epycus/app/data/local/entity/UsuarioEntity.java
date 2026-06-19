package es.epycus.app.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class UsuarioEntity {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "correo_electronico")
    private String correoElectronico;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "refresh_token")
    private String refreshToken;

    @ColumnInfo(name = "fecha_registro")
    private String fechaRegistro;

    public UsuarioEntity(int id, String nombre, String correoElectronico,
                         String token, String refreshToken, String fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
        this.token = token;
        this.refreshToken = refreshToken;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public String getFechaRegistro() { return fechaRegistro; }
}
