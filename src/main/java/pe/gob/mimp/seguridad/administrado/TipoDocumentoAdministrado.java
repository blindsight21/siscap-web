package pe.gob.mimp.seguridad.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.general.administrado.AdministradorInterface;
import pe.gob.mimp.seguridad.modelo.TipoDocumento;
import pe.gob.mimp.siscap.ws.tipodocumento.cliente.TipoDocumentoCallService;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class TipoDocumentoAdministrado extends AdministradorAbstracto  implements Serializable, AdministradorInterface{
	
    private TipoDocumento entidad;

    private TipoDocumento entidadSeleccionada;

    private List<TipoDocumento> entidades;
    
    @Inject
    private TipoDocumentoCallService tipoDocumentoCallService;
    
    public TipoDocumentoAdministrado() {
        instanciar();
    }
    
    public void instanciar()
     {
        entidad = new TipoDocumento();
        entidadSeleccionada = new TipoDocumento();    
        entidades = new ArrayList<TipoDocumento>();
     }

    public TipoDocumento getEntidad() {
        return entidad;
    }

    public void setEntidad(TipoDocumento entidad) {
        this.entidad = entidad;
    }

    public TipoDocumento getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(TipoDocumento entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<TipoDocumento> getEntidades() {
        entidades = tipoDocumentoCallService.findAll();

        return entidades;
    }

    public void setEntidades(List<TipoDocumento> entidades) {
        this.entidades = entidades;
    }

    public TipoDocumento getEntidad(String id) {
        TipoDocumento tipoDocumento = null;
        
        if((null != id) || (false == id.equals("")))
        {
            tipoDocumento = tipoDocumentoCallService.find(new BigDecimal(id));
        }

        return tipoDocumento;
    }
    
    public List<TipoDocumento> obtenerTiposDocumento() {
        
        List<TipoDocumento> tiposDocumento = tipoDocumentoCallService.findAll();
        List<TipoDocumento> tiposDocumentoPersonaNatural = new ArrayList<TipoDocumento>();

        for (TipoDocumento tipo : tiposDocumento) {
            if (true == tipo.getTxtDescripcion().equals("DNI")) {
                tiposDocumentoPersonaNatural.add(tipo);
            }
            if (true == tipo.getTxtDescripcion().equals("CARNET EXTRANJERIA")) {
                tiposDocumentoPersonaNatural.add(tipo);
            }
            if (true == tipo.getTxtDescripcion().equals("PASAPORTE")) {
                tiposDocumentoPersonaNatural.add(tipo);
            }
        }
        return tiposDocumentoPersonaNatural;
    }

    @Override
    public void almacenarEntidadSeleccionadaAjax(AjaxBehaviorEvent event) throws Exception 
    {
        try
        {
            System.out.println("----------------------------------------------------------------------almacenarEntidadSeleccionadaAjax");
            this.entidadSeleccionada = (TipoDocumento) ((UIOutput)event.getSource()).getValue();
            
            if(null != this.entidadSeleccionada)
            {
                System.out.println("----------------------------------------------------------------------almacenarEntidadSeleccionadaAjax this.entidadSeleccionada: " + this.entidadSeleccionada.getTxtDescripcion());
            }
            else
            {
                System.out.println("----------------------------------------------------------------------almacenarEntidadSeleccionadaAjax this.entidadSeleccionada: {null}");
            }
            
        }
        catch(Exception e)
        {
            throw(e);
        }
    }
    
    public TipoDocumento obtener(BigInteger id) 
    {
        TipoDocumento tipoDocumento = null;
        
        if((null != id) || (0 != id.compareTo(BigInteger.ZERO)))
        {
            tipoDocumento = tipoDocumentoCallService.find(new BigDecimal(id));
        }

        return tipoDocumento;
    }
    
    public TipoDocumento obtener(String nombre) 
    {
        return tipoDocumentoCallService.obtener(nombre);
    }
}
