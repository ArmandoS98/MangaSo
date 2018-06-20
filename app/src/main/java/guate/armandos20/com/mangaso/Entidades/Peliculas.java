package guate.armandos20.com.mangaso.Entidades;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Peliculas implements Parcelable {
    private String nombre;
    private String sinopsis;
    private @ServerTimestamp Date fecha;
    private String id_pelicula;
    private String estado;
    private String url_imagen;

    public Peliculas(String nombre, String sinopsis ,Date fecha, String id_pelicula, String url_imagen, String estado) {
        this.nombre = nombre;
        this.sinopsis = sinopsis;
        this.fecha = fecha;
        this.id_pelicula = id_pelicula;
        this.url_imagen = url_imagen;
        this.estado = estado;
    }

    public Peliculas() {
    }

    protected Peliculas(Parcel in) {
        nombre = in.readString();
        sinopsis = in.readString();
        id_pelicula = in.readString();
        url_imagen = in.readString();
        estado = in.readString();
    }

    public static final Parcelable.Creator<Home> CREATOR = new Parcelable.Creator<Home>() {
        @Override
        public Home createFromParcel(Parcel in) {
            return new Home(in);
        }

        @Override
        public Home[] newArray(int size) {
            return new Home[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getId_pelicula() {
        return id_pelicula;
    }

    public void setId_pelicula(String id_pelicula) {
        this.id_pelicula = id_pelicula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(sinopsis);
        parcel.writeString(id_pelicula);
        parcel.writeString(url_imagen);
        parcel.writeString(estado);
    }
}
