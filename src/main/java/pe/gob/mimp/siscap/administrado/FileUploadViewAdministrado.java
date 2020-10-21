/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.administrado;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.siscap.modelo.ActividadGob;
import pe.gob.mimp.siscap.modelo.ArchivoActividad;
import pe.gob.mimp.siscap.ws.archivoactividad.cliente.ArchivoActividadCallService;
import pe.gob.mimp.siscap.ws.parametrosiscap.cliente.ParametroSiscapCallService;

/**
 *
 * @author desarrollador
 */
@ManagedBean
@ApplicationScoped
public class FileUploadViewAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(ArchivoActividad.class.getName());
    private static final long serialVersionUID = 1L;

    private ArchivoActividad entidadSeleccionada;

    private StreamedContent lecturaPDF;
    
    //BD
    private List<ArchivoActividad> archivoActividadList;
    public List<ArchivoActividad> archivoAsistenciaList;

    @Inject
    private ParametroSiscapCallService parametroSiscapCallService;
    @Inject
    private ArchivoActividadCallService archivoActividadCallService;

    @PostConstruct
    public void init() {
        archivoAsistenciaList = new ArrayList<>();
    }

    public StreamedContent getLecturaPDF() {
        return lecturaPDF;
    }

    public void setLecturaPDF(StreamedContent lecturaPDF) {
        this.lecturaPDF = lecturaPDF;
    }
    
    public ArchivoActividad getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ArchivoActividad entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<ArchivoActividad> getArchivoActividadList() {
        return archivoActividadList;
    }

    public void setArchivoActividadList(List<ArchivoActividad> archivoActividadList) {
        this.archivoActividadList = archivoActividadList;
    }

    public List<ArchivoActividad> getArchivoAsistenciaList() {
        return archivoAsistenciaList;
    }

    public void setArchivoAsistenciaList(List<ArchivoActividad> archivoAsistenciaList) {
        this.archivoAsistenciaList = archivoAsistenciaList;
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        String correlativo;// si es que se quiere agregar un correlativo al archivo
        ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());
        if (validarArchivo()) {
            boolean existeArchivo;
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            /*ruta windows prueba*/
            //String rootWindows = "C:\\glassfish\\siscap";//para prueba en windows
            /*ruta servidor linux*/
            //String rootLinux = fachadaParametro.find(new BigDecimal("9")).getTxtValor();//ruta del servidor
            File directorio = new File(detectOSRoot() + "/documentos");
            //Si es que no existe el directorio, se creara.
            if (!directorio.exists()) {
                directorio.mkdir();
                //correlativo = "1";
            } else {
                //correlativo = String.valueOf(directorio.list().length + 1);
            }
            String nombreArchivo = actividadGobAdministrado.getEntidadSeleccionada().getNidActividadGob() + "-"
                    + fmt.format(new Date()) + "-"
                    + event.getFile().getFileName();

            File file = new File(detectOSRoot() + "/documentos/" + nombreArchivo);

            existeArchivo = file.exists();
            InputStream is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
            if (!existeArchivo) {
                //loadArchivoAsistenciaList();
                crearArchivoActividadDB(actividadGobAdministrado.getEntidadSeleccionada(), nombreArchivo, file.toString());
                adicionarMensaje("Carga completa", "El archivo se ha cargado correctamente");
            } else {
                adicionarMensajeWarning("Advertencia", "El archivo ya existe. Se ha sobreescrito el archivo");
            }
        }
    }

    public Boolean validarArchivo() {
        Boolean rpta = true;

        ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nidActividadGob", actividadGobAdministrado.getEntidadSeleccionada());
        parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
        int count = archivoActividadCallService.getRecordCount(new FindByParamBean(parameters, ""));
        logger.log(Level.INFO, "Archivos encontrados: {0}", count);

        if (count > 0) {
            rpta = enviarWarnMessage("Ya existe un archivo registrado.");
        }
        return rpta;
    }

    public String detectOSRoot() {
        /* includes: Windows 2000,  Windows 95, Windows 98, Windows NT, Windows Vista, Windows XP etc*/
        Boolean rptaOS = System.getProperty("os.name").startsWith("Windows");
        String rootOS;
        if (rptaOS) {
            rootOS = "C:\\glassfish\\siscap";
        } else {
            rootOS = parametroSiscapCallService.find(new BigDecimal("9")).getTxtValor();
        }
        return rootOS;
    }

    private boolean enviarWarnMessage(String mensaje) {
        adicionarMensajeError("Información", mensaje);
        return false;
    }

    public void crearArchivoActividadDB(ActividadGob nidActividadGob, String nombreArchivo, String url) {
        UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
        try {
            ArchivoActividad nuevoArchivo = new ArchivoActividad();

            nuevoArchivo.setNidActividadGob(nidActividadGob);
            nuevoArchivo.setTxtArchivo(nombreArchivo);
            nuevoArchivo.setTxtUrl(url);
            nuevoArchivo.setFlgActivo(BigInteger.ONE);
            nuevoArchivo.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
            nuevoArchivo.setTxtPc(Internet.obtenerNombrePC());
            nuevoArchivo.setTxtIp(Internet.obtenerIPPC());
            nuevoArchivo.setFecEdicion(new Date());

            archivoActividadCallService.crearArchivoActividad(nuevoArchivo);

            if (null != nuevoArchivo.getNidArchivoActividad()) {
                loadArchivoAsistenciaList();
            }

        } catch (Exception e) {
        }
    }

    public void anularArchivoActividadDB(ArchivoActividad entidadSeleccionada) {
        try {

            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
            entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
            entidadSeleccionada.setFecEdicion(new Date());

            archivoActividadCallService.editarArchivoActividad(entidadSeleccionada);
            loadArchivoAsistenciaList();

        } catch (Exception e) {
        }
    }

    public void anularArchivoAsistencia(ArchivoActividad entidadSeleccionada) throws IOException {
        if (null != entidadSeleccionada) {
            /*ruta windows prueba*/
            //String rutaWindows = "C:\\glassfish\\siscap";
            /*ruta servidor linux*/
            //String rootLinux = fachadaParametro.find(new BigDecimal("9")).getTxtValor();
            File directorio = new File(detectOSRoot() + "/documentos/" + entidadSeleccionada.getTxtArchivo());
            if (directorio.delete()) {
                anularArchivoActividadDB(entidadSeleccionada);
                adicionarMensaje("Eliminación completa", "El archivo se ha eliminado correctamente");
                loadArchivoAsistenciaList();
            } else {
                adicionarMensajeWarning("Advertencia", "No se puede eliminar el archivo");
            }
        } else {
            adicionarMensajeWarning("Advertencia", "No se puede eliminar el archivo. El pámetro no se obtuvo o se encuentra vacio");
        }
    }

    public void loadArchivoAsistenciaList() {
        logger.info(":: FileUploadViewAdministrado.loadActividadGobList :: Starting execution...");
        try {
            ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nidActividadGob", actividadGobAdministrado.getEntidadSeleccionada());
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            this.archivoAsistenciaList = archivoActividadCallService.loadArchivoActividadList(new FindByParamBean(parameters, "nidArchivoActividad"));

        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error loadActividadGobList" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGobAdministrado.loadActividadGobList :: Execution finish.");
    }
    
    public void obtenerPDF(String nombreArchivo) {
        this.lecturaPDF = new ByteArrayContent(null, "application/pdf");
        //setLecturaPDF(null);
        try {
            if (null != nombreArchivo && !"".equals(nombreArchivo)) {
                /*root linux*/
                //String rootServer = fachadaParametro.find(new BigDecimal("9")).getTxtValor();
                String root = detectOSRoot() + "/documentos/" + nombreArchivo;
                /*root windows prueba*/
                //String rootPrueba = "C:\\glassfish\\siscap";
                //String rutaWindows = rootPrueba + "/documentos/" + nombreArchivo;

                //String rutaWindows2 = rutaWindows+"\\documentos\\"+nombreArchivo;
                File file = new File(root);
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
    
    

    /*public void loadArchivoAsistenciaList() {
     ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());
     //ruta windows prueba
     String rutaWindows = "C:\\glassfish\\siscap";
     //ruta servidor linux
     //String rootLinux = fachadaParametro.find(new BigDecimal("9")).getTxtValor();
     File dir = new File(rutaWindows + "/documentos");
     try {
     if (null != actividadGobAdministrado.getEntidadSeleccionada().getNidActividadGob()) {
     if (0 < dir.length()) {

     List<String> ficheros = new ArrayList<>(dir.list().length);
     Collections.addAll(ficheros, dir.list());
     archivoAsistenciaList = new ArrayList<>();
     for (String valor : ficheros) {
     String[] parts = valor.split("-");
     if (actividadGobAdministrado.getEntidadSeleccionada().getNidActividadGob().toString().equals(parts[0])) {
     archivoAsistenciaList.add(valor);
     }
     }
     }
     }
     } catch (Exception e) {
     archivoAsistenciaList = null;
     }
     }*/
}
