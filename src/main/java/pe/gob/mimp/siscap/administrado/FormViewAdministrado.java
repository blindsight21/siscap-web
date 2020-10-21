/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import pe.gob.mimp.siscap.ws.parametrosiscap.cliente.ParametroSiscapCallService;

/**
 *
 * @author desarrollador
 */
@ManagedBean
@ApplicationScoped
public class FormViewAdministrado extends AdministradorAbstracto implements Serializable{
    private StreamedContent lecturaPDF;
    @Inject
    private ParametroSiscapCallService parametroSiscapCallService;

    @PostConstruct
    public void init() {

    }

    public StreamedContent getLecturaPDF() {
        return lecturaPDF;
    }

    public void setLecturaPDF(StreamedContent lecturaPDF) {
        this.lecturaPDF = lecturaPDF;
    }

    public void obtenerPDF(String nombreArchivo) {
        this.lecturaPDF = new ByteArrayContent(null, "application/pdf");
        //setLecturaPDF(null);
        try {
            if (null != nombreArchivo && !"".equals(nombreArchivo)) {
                /*root linux*/
                String rootServer = parametroSiscapCallService.find(new BigDecimal("9")).getTxtValor();
                String rootLinux = rootServer + "/documentos/" + nombreArchivo;
                /*root windows prueba*/
                //String rootPrueba = "C:\\glassfish\\siscap";
                //String rutaWindows = rootPrueba + "/documentos/" + nombreArchivo;

                //String rutaWindows2 = rutaWindows+"\\documentos\\"+nombreArchivo;
                File file = new File(rootLinux);
                InputStream input = new FileInputStream(file);
                ExternalContext externalContext = getFacesContext().getExternalContext();
                setLecturaPDF(new DefaultStreamedContent(input, externalContext.getMimeType(file.getName()),file.getName()));
            } else {
                System.out.println("obtenerPDF: No se recibió el archivo PDF");
            }
        } catch (Exception e) {
            System.out.println("obtenerPDF: " + e.getMessage());
        }
    }

    public String generateRandomIdForNotCaching() {
        return java.util.UUID.randomUUID().toString();
    }
    
}
