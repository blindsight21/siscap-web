package pe.gob.mimp.siscap.auxiliar;

import java.math.BigDecimal;

public class NodoModeloDatos 
{
    private BigDecimal identificador;
    private Number valor;
    private String etiqueta;
    
    public BigDecimal getIdentificador() {
        return identificador;
    }

    public void setIdentificador(BigDecimal identificador) {
        this.identificador = identificador;
    }

    public Number getValor() {
        return valor;
    }

    public void setValor(Number valor) {
        this.valor = valor;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
    
}
