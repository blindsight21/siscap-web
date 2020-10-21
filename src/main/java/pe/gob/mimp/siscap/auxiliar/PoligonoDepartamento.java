package pe.gob.mimp.siscap.auxiliar;

import java.math.BigDecimal;
import org.primefaces.model.map.Polygon;

public class PoligonoDepartamento 
{
    private boolean mostrar;
    private BigDecimal nidDepartamento;
    private Polygon poligono;
    
    public boolean isMostrar() {
        return mostrar;
    }

    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    public BigDecimal getNidDepartamento() {
        return nidDepartamento;
    }

    public void setNidDepartamento(BigDecimal nidDepartamento) {
        this.nidDepartamento = nidDepartamento;
    }

    public Polygon getPoligono() {
        return poligono;
    }

    public void setPoligono(Polygon poligono) {
        this.poligono = poligono;
    }
    
    private PoligonoDepartamento()
    {
    
    }
    
    public PoligonoDepartamento(boolean mostrar, BigDecimal nidDepartamento, String colorBorde, String colorRelleno)
    {
        this.mostrar = mostrar;
        this.nidDepartamento = nidDepartamento;
        this.poligono = new Polygon();
        
        this.poligono.setStrokeColor(colorBorde);
        this.poligono.setFillColor(colorRelleno);
        this.poligono.setStrokeOpacity(0.7);
        this.poligono.setFillOpacity(0.2);
    }
}
