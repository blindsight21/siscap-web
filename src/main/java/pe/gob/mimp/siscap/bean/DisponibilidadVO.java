/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.bean;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Omar
 */
@Entity
public class DisponibilidadVO implements Serializable {

    @Id
    private BigInteger item;
    private BigInteger nidFuncionalidad;
    private String modulo;
    private BigInteger tiempoMedioFalla;
    private BigInteger tiempoMedioReparacion;
    private BigInteger porcentajeDisponibilidad;

    public DisponibilidadVO() {
        
    }

    public BigInteger getItem() {
        return item;
    }

    public void setItem(BigInteger item) {
        this.item = item;
    }

    public BigInteger getNidFuncionalidad() {
        return nidFuncionalidad;
    }

    public void setNidFuncionalidad(BigInteger nidFuncionalidad) {
        this.nidFuncionalidad = nidFuncionalidad;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public BigInteger getTiempoMedioFalla() {
        return tiempoMedioFalla;
    }

    public void setTiempoMedioFalla(BigInteger tiempoMedioFalla) {
        this.tiempoMedioFalla = tiempoMedioFalla;
    }

    public BigInteger getTiempoMedioReparacion() {
        return tiempoMedioReparacion;
    }

    public void setTiempoMedioReparacion(BigInteger tiempoMedioReparacion) {
        this.tiempoMedioReparacion = tiempoMedioReparacion;
    }

    public BigInteger getPorcentajeDisponibilidad() {
        return porcentajeDisponibilidad;
    }

    public void setPorcentajeDisponibilidad(BigInteger porcentajeDisponibilidad) {
        this.porcentajeDisponibilidad = porcentajeDisponibilidad;
    }

}
