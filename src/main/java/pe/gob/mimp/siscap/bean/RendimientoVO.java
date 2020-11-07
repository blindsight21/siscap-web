/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Omar
 */
@Entity
public class RendimientoVO implements Serializable {

    @Id
    private BigInteger item;
    private BigInteger nidFuncionalidad;
    private String modulo;
    private BigInteger numeroTareasRealizadas;
    private BigDecimal tiempoObservacion;
    private BigDecimal porcentajeRendimiento;

    public RendimientoVO() {
        
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

    public BigInteger getNumeroTareasRealizadas() {
        return numeroTareasRealizadas;
    }

    public void setNumeroTareasRealizadas(BigInteger numeroTareasRealizadas) {
        this.numeroTareasRealizadas = numeroTareasRealizadas;
    }

    public BigDecimal getTiempoObservacion() {
        return tiempoObservacion;
    }

    public void setTiempoObservacion(BigDecimal tiempoObservacion) {
        this.tiempoObservacion = tiempoObservacion;
    }

    public BigDecimal getPorcentajeRendimiento() {
        return porcentajeRendimiento;
    }

    public void setPorcentajeRendimiento(BigDecimal porcentajeRendimiento) {
        this.porcentajeRendimiento = porcentajeRendimiento;
    }

}
