package pe.gob.mimp.siscap.administrado;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.StreamedContent;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.AreaAdministrado;
import pe.gob.mimp.siscap.modelo.ActividadGob;
import pe.gob.mimp.siscap.modelo.VwActividadCalendario;
import pe.gob.mimp.siscap.ws.actividadgob.cliente.ActividadGobCallService;
import pe.gob.mimp.siscap.ws.vwactividadcalendario.cliente.VwActividadCalendarioCallService;

@ManagedBean
@ViewScoped
public class ScheduleViewAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(ActividadGob.class.getName());

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    public List<VwActividadCalendario> actividadCalendarioViewList;
    private StreamedContent file;

    @Inject
    private VwActividadCalendarioCallService vwActividadCalendarioCallService;
    @Inject
    private ActividadGobCallService actividadGobCallService;

    @PostConstruct
    public void init() {
        cargarActividades();
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public List<VwActividadCalendario> getActividadCalendarioViewList() {
        return actividadCalendarioViewList;
    }

    public void setActividadCalendarioViewList(List<VwActividadCalendario> actividadCalendarioViewList) {
        this.actividadCalendarioViewList = actividadCalendarioViewList;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public ActividadGob validarObject(Object objeto) {
        ActividadGob retornar = null;
        if (objeto instanceof ActividadGob) {
            retornar = (ActividadGob) objeto;
        }
        return retornar;
    }

    public String obtenerInformacion(Object objecto, String tipo) {
        AreaAdministrado areaAdministrado = (AreaAdministrado) getFacesContext().getApplication().createValueBinding("#{areaAdministrado}").getValue(getFacesContext());
        StringBuilder informacion = new StringBuilder();
        ActividadGob actividadGob = validarObject(objecto);
        try {
            switch (tipo) {
                case "unidad":
                    informacion.append(areaAdministrado.getEntidad(actividadGob.getNidArea().toString()).getTxtArea());
                    break;
                case "responsable":
                    informacion.append(actividadGob.getTxtResponsableNombre())
                            .append(" ").append(actividadGob.getTxtResponsableApepat())
                            .append(" ").append(actividadGob.getTxtResponsableApemat());
                    break;
            }
        } catch (Exception e) {
            informacion.append("");
        }

        return informacion.toString();
    }

    public void cargarActividades() {
        try {
            loadActividadCalendarioViewList();
            eventModel = new DefaultScheduleModel();
            for (VwActividadCalendario vwActividadCalendario : this.actividadCalendarioViewList) {
                if (vwActividadCalendario.getFecInicio() != null && vwActividadCalendario.getFecFin() != null) {
                    DefaultScheduleEvent eventDatos = new DefaultScheduleEvent();
                    eventDatos.setAllDay(true);
                    eventDatos.setTitle(vwActividadCalendario.getTxtGobierno() + " - " + vwActividadCalendario.getTxtSigla());
                    eventDatos.setStartDate(resetDate(vwActividadCalendario.getFecInicio(), "am").getTime());
                    eventDatos.setEndDate(resetDate(vwActividadCalendario.getFecFin(), "pm").getTime());
                    eventDatos.setData(vwActividadCalendario);

                    switch (vwActividadCalendario.getNidEstadoActividadGob().intValueExact()) {
                        case 1://anulado
                            eventDatos.setStyleClass("eventRojo");
                            break;
                        case 2://programado
                            eventDatos.setStyleClass("eventAmarillo");
                            break;
                        case 3://reprogramado
                            eventDatos.setStyleClass("eventAzul");
                            break;
                        case 4://ejecutado
                            eventDatos.setStyleClass("eventVerde");
                            break;
                    }
                    eventModel.addEvent(eventDatos);
                    //eventModel.addEvent(new DefaultScheduleEvent(actividadGobierno.getTxtTema(), resetDate(actividadGobierno.getFecInicio(), "am").getTime(), resetDate(actividadGobierno.getFecFin(), "pm").getTime(), actividadGobierno));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error cargar actividades", Util.tiempo());
        }
    }

    private Calendar resetDate(Date fecha, String tipo) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        switch (tipo) {
            case "am":
                cal.set(Calendar.AM_PM, Calendar.AM);
                cal.set(Calendar.HOUR, 8);
                break;
            case "pm":
                cal.set(Calendar.AM_PM, Calendar.PM);
                cal.set(Calendar.HOUR, 11);
                break;
        }
        return cal;
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }

    public void loadActividadCalendarioViewList() {
        logger.info(":: ActividadGobAdministrado.loadActividadCalendarioViewList :: Starting execution...");
        EstadoActividadGobAdministrado estadoActividadGobAdministrado = (EstadoActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{estadoActividadGobAdministrado}").getValue(getFacesContext());
        //ActividadGEAdministrado actividadGEAdministrado = (ActividadGEAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGEAdministrado}").getValue(getFacesContext());
        try {
            Map<String, Object> parameters = new HashMap<>();
            //parameters.put("nidEstadoActividad", CoreConstant.STATUS_ACTIVE);
            if (null != estadoActividadGobAdministrado.getEntidadSeleccionada().getTxtEstadoActividadGob()) {
                parameters.put("txtEstadoActividadGob", estadoActividadGobAdministrado.getEntidadSeleccionada().getTxtEstadoActividadGob());
            }
            //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            this.actividadCalendarioViewList = vwActividadCalendarioCallService.loadVwActividadCalendarioList(new FindByParamBean(parameters, "nidActividadG"));

        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error loadActividadGobList" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGobAdministrado.loadActividadCalendarioViewList :: Execution finish.");
    }

    public void getReportData() throws IOException, InvalidFormatException {
        String[] columns = {"TEMA", "UNIDAD ORGANICA", "GOBIERNO", "FECHA INICIO", "FECHA FIN"};
        Workbook workbook = new XSSFWorkbook();

        /* CreationHelper helps us create instances of various things like DataFormat, 
         Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("CalendarioLista");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        //headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Create Other rows and cells with employees data
        int rowNum = 1;
        for (VwActividadCalendario actividadCalendarioView : this.actividadCalendarioViewList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(actividadCalendarioView.getTxtTema());
            row.createCell(1)
                    .setCellValue(actividadCalendarioView.getTxtArea());
            row.createCell(2)
                    .setCellValue(actividadCalendarioView.getTxtGobierno());
            
            Cell fechaInicio = row.createCell(3);
            fechaInicio.setCellValue(actividadCalendarioView.getFecInicio());
            fechaInicio.setCellStyle(dateCellStyle);

            Cell fechaFin = row.createCell(4);
            fechaFin.setCellValue(actividadCalendarioView.getFecFin());
            fechaFin.setCellStyle(dateCellStyle);
        }

        // Resize all columns to fit the content size
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        workbook.write(fileOut);
        //fileOut.close();

        InputStream inputStream = new ByteArrayInputStream(fileOut.toByteArray());
        this.file = new DefaultStreamedContent(inputStream, "application/xls", "reporteCalendario.xls");
        // Closing the workbook
        //workbook.close();
        //this.actividadCalendarioViewList
        /*HSSFWorkbook workbook = new HSSFWorkbook();
         HSSFSheet sheet = workbook.createSheet();
         HSSFRow row = sheet.createRow(0);
         HSSFCell cell = row.createCell(0);
         cell.setCellValue(0.0);

         FacesContext facesContext = FacesContext.getCurrentInstance();
         ExternalContext externalContext = facesContext.getExternalContext();
         externalContext.setResponseContentType("application/vnd.ms-excel");
         externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"my.xls\"");

         workbook.write(externalContext.getResponseOutputStream());
         facesContext.responseComplete();
        
         String fileName = "dynamic.xls";
         String contentType = "application/vnd.ms-excel";

         // ...

         HSSFWorkbook workbook = new HSSFWorkbook();
         // Build XLS content here.
         workbook.write(output);
         workbook.close();*/
    }

}
