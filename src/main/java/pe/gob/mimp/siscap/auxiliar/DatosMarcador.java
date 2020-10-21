package pe.gob.mimp.siscap.auxiliar;

import java.math.BigDecimal;

/**
 * Clase que almacena los datos de un marcador.
 * 
 */
public class DatosMarcador 
{
    private BigDecimal centroId;
    private String centroNombre;
    
    private String contactoNombre;
    private String contactoTelefono;
    private String contactoCorreo;
    
    private String fechaUltimaSupervision;
    private String capacidad;
    private String hombres;
    private String mujeres;

    public BigDecimal getCentroId() {
        return centroId;
    }

    public void setCentroId(BigDecimal centroId) {
        this.centroId = centroId;
    }

    public String getCentroNombre() {
        return centroNombre;
    }

    public void setCentroNombre(String centroNombre) {
        this.centroNombre = centroNombre;
    }

    public String getContactoNombre() {
        return contactoNombre;
    }

    public void setContactoNombre(String contactoNombre) {
        this.contactoNombre = contactoNombre;
    }

    public String getContactoTelefono() {
        return contactoTelefono;
    }

    public void setContactoTelefono(String contactoTelefono) {
        this.contactoTelefono = contactoTelefono;
    }

    public String getContactoCorreo() {
        return contactoCorreo;
    }

    public void setContactoCorreo(String contactoCorreo) {
        this.contactoCorreo = contactoCorreo;
    }

    public String getFechaUltimaSupervision() {
        return fechaUltimaSupervision;
    }

    public void setFechaUltimaSupervision(String fechaUltimaSupervision) {
        this.fechaUltimaSupervision = fechaUltimaSupervision;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getHombres() {
        return hombres;
    }

    public void setHombres(String hombres) {
        this.hombres = hombres;
    }

    public String getMujeres() {
        return mujeres;
    }

    public void setMujeres(String mujeres) {
        this.mujeres = mujeres;
    }
    
}
