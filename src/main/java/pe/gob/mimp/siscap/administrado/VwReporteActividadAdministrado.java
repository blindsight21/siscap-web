package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.siscap.modelo.VwReporteActividad;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.vwreporteactividad.cliente.VwReporteActividadCallService;
import pe.gob.mimp.util.EnumFuncionalidad;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class VwReporteActividadAdministrado extends AdministradorAbstracto implements Serializable {

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    private Logger logger = Logger.getLogger(VwReporteActividad.class.getName());

    private VwReporteActividad entidad;
    private VwReporteActividad entidadSeleccionada;
    private List<VwReporteActividad> entidades;

    @Inject
    private VwReporteActividadCallService vwReporteActividadCallService;

    private BarChartModel modeloBarraTipoObjetivo;
    private BarChartModel modeloBarraTrimestre;
    private BarChartModel modeloBarraEstado;
    private BarChartModel modeloBarraPublico;
    private BarChartModel modeloBarraProfesional;

    @PostConstruct
    public void initBean() {
        limpiarReporte();
        /*this.entidad = new VwReporteActividad();
         this.entidadSeleccionada = new VwReporteActividad();*/
    }

    public VwReporteActividad getEntidad() {
        return entidad;
    }

    public void setEntidad(VwReporteActividad entidad) {
        this.entidad = entidad;
    }

    public VwReporteActividad getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(VwReporteActividad entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<VwReporteActividad> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<VwReporteActividad> entidades) {
        this.entidades = entidades;
    }

    public BarChartModel getModeloBarraTipoObjetivo() {
        return modeloBarraTipoObjetivo;
    }

    public void setModeloBarraTipoObjetivo(BarChartModel modeloBarraTipoObjetivo) {
        this.modeloBarraTipoObjetivo = modeloBarraTipoObjetivo;
    }

    public BarChartModel getModeloBarraTrimestre() {
        return modeloBarraTrimestre;
    }

    public void setModeloBarraTrimestre(BarChartModel modeloBarraTrimestre) {
        this.modeloBarraTrimestre = modeloBarraTrimestre;
    }

    public BarChartModel getModeloBarraEstado() {
        return modeloBarraEstado;
    }

    public void setModeloBarraEstado(BarChartModel modeloBarraEstado) {
        this.modeloBarraEstado = modeloBarraEstado;
    }

    public BarChartModel getModeloBarraPublico() {
        return modeloBarraPublico;
    }

    public void setModeloBarraPublico(BarChartModel modeloBarraPublico) {
        this.modeloBarraPublico = modeloBarraPublico;
    }

    public BarChartModel getModeloBarraProfesional() {
        return modeloBarraProfesional;
    }

    public void setModeloBarraProfesional(BarChartModel modeloProfesional) {
        this.modeloBarraProfesional = modeloBarraProfesional;
    }

    public BarChartModel generarGraficoTrimestre() {
        modeloBarraTrimestre = new BarChartModel();

        generarTrimestre();

        modeloBarraTrimestre.setLegendPosition("ne");
        modeloBarraTrimestre.setShowDatatip(false);

        return modeloBarraTrimestre;
    }

    /*public BarChartModel generarGraficoPublicoObjetivo() {
     modeloBarraPublico = new BarChartModel();

     generarPublicoObjetivo();

     modeloBarraPublico.setLegendPosition("ne");
     modeloBarraPublico.setShowDatatip(false);

     return modeloBarraPublico;
     }*/
    public BarChartModel generarGraficoEstado() throws Exception {
        
        long startTime = System.currentTimeMillis();
        modeloBarraEstado = new BarChartModel();

        generarEstado();

        modeloBarraEstado.setLegendPosition("ne");
        modeloBarraEstado.setShowDatatip(false);

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.REPORTE.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        return modeloBarraEstado;
    }

    public BarChartModel generarGraficoTipoObjetivo() {
        modeloBarraTipoObjetivo = new BarChartModel();

        generarTipoObjetivo();

        modeloBarraTipoObjetivo.setLegendPosition("ne");
        modeloBarraTipoObjetivo.setShowDatatip(false);

        return modeloBarraTipoObjetivo;
    }

    public BarChartModel generarGraficoProfesional() throws Exception {

        long startTime = System.currentTimeMillis();
        modeloBarraProfesional = new BarChartModel();

        generarProfesional();

        modeloBarraProfesional.setLegendPosition("ne");
        modeloBarraProfesional.setShowDatatip(false);

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.REPORTE.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        return modeloBarraProfesional;
    }

    public void generarTrimestre() {

        long startTime = System.currentTimeMillis();
        this.modeloBarraTrimestre = new BarChartModel();

        int itrimestre1 = 0;
        int itrimestre2 = 0;
        int itrimestre3 = 0;
        int itrimestre4 = 0;

        try {

            List<VwReporteActividad> vwReporteTrimestres = vwReporteActividadCallService.findAll();

            for (VwReporteActividad vwReporteRegistro : vwReporteTrimestres) {
                if (vwReporteRegistro.getNumTrimestre() == BigInteger.ONE) {
                    itrimestre1++;
                } else if (vwReporteRegistro.getNumTrimestre() == BigInteger.valueOf(2)) {
                    itrimestre2++;
                } else if (vwReporteRegistro.getNumTrimestre() == BigInteger.valueOf(3)) {
                    itrimestre3++;
                } else if (vwReporteRegistro.getNumTrimestre() == BigInteger.valueOf(4)) {
                    itrimestre4++;
                }
            }

            Integer intTrimestre1 = itrimestre1;
            Integer intTrimestre2 = itrimestre2;
            Integer intTrimestre3 = itrimestre3;
            Integer intTrimestre4 = itrimestre4;

            Number numTrimestre1 = (Number) intTrimestre1;
            Number numTrimestre2 = (Number) intTrimestre2;
            Number numTrimestre3 = (Number) intTrimestre3;
            Number numTrimestre4 = (Number) intTrimestre4;

            ChartSeries serieTrimestre1 = new ChartSeries();
            serieTrimestre1.setLabel("Trimestre 1" + " (" + intTrimestre1.toString() + ")");
            serieTrimestre1.set(" ", numTrimestre1);

            ChartSeries serieTrimestre2 = new ChartSeries();
            serieTrimestre2.setLabel("Trimestre 2" + " (" + intTrimestre2.toString() + ")");
            serieTrimestre2.set(" ", numTrimestre2);

            ChartSeries serieTrimestre3 = new ChartSeries();
            serieTrimestre3.setLabel("Trimestre 3" + " (" + intTrimestre3.toString() + ")");
            serieTrimestre3.set(" ", numTrimestre3);

            ChartSeries serieTrimestre4 = new ChartSeries();
            serieTrimestre4.setLabel("Trimestre 4" + " (" + intTrimestre4.toString() + ")");
            serieTrimestre4.set(" ", numTrimestre4);

            this.modeloBarraTrimestre.addSeries(serieTrimestre1);
            this.modeloBarraTrimestre.addSeries(serieTrimestre2);
            this.modeloBarraTrimestre.addSeries(serieTrimestre3);
            this.modeloBarraTrimestre.addSeries(serieTrimestre4);

            Axis xAxis = this.modeloBarraTrimestre.getAxis(AxisType.X);
            xAxis.setLabel("Trimestre");

            Axis yAxis = this.modeloBarraTrimestre.getAxis(AxisType.Y);
            yAxis.setLabel("Cantidad");

            yAxis.setMin(0);

            this.modeloBarraTrimestre.setTitle("REPORTE DE ACTIVIDADES POR TRIMESTRE");
            this.modeloBarraTrimestre.setLegendPosition("ne");
            this.modeloBarraTrimestre.setShowDatatip(false);
            this.modeloBarraTrimestre.setShowPointLabels(true);

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.REPORTE.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {

            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }
    }

    /*public void generarPublicoObjetivo() {
     this.modeloBarraPublico = new BarChartModel();

     int iautoridad = 0;
     int ifuncionario = 0;
     int iprofesional = 0;
     int itecnico = 0;
     int isocivil = 0;
     int iotros = 0;

     try {

     List<VwReporteActividad> vwReportePublicosObjetivos = vwReporteActividadCallService.findAll();

     for (VwReporteActividad vwReportePublico : vwReportePublicosObjetivos) {
     if (vwReportePublico.getNidPublicoObjetivo() == BigInteger.ONE) {
     iautoridad++;
     } else if (vwReportePublico.getNidPublicoObjetivo() == BigInteger.valueOf(2)) {
     ifuncionario++;
     } else if (vwReportePublico.getNidPublicoObjetivo() == BigInteger.valueOf(3)) {
     iprofesional++;
     } else if (vwReportePublico.getNidPublicoObjetivo() == BigInteger.valueOf(4)) {
     itecnico++;
     } else if (vwReportePublico.getNidPublicoObjetivo() == BigInteger.valueOf(5)) {
     isocivil++;
     } else {
     iotros++;
     }
     }

     Integer intAutoridad = iautoridad;
     Integer intFuncionario = ifuncionario;
     Integer intProfesional = iprofesional;
     Integer intTecnico = itecnico;
     Integer intScivil = isocivil;
     Integer intOtros = iotros;

     Number numAutoridad = (Number) intAutoridad;
     Number numFuncionario = (Number) intFuncionario;
     Number numProfesional = (Number) intProfesional;
     Number numTecnico = (Number) intTecnico;
     Number numScivil = (Number) intScivil;
     Number numOtros = (Number) intOtros;

     ChartSeries serieAutoridad = new ChartSeries();
     serieAutoridad.setLabel("Autoridades" + " (" + intAutoridad.toString() + ")");
     serieAutoridad.set(" ", numAutoridad);

     ChartSeries serieFuncionario = new ChartSeries();
     serieFuncionario.setLabel("Funcionarios" + " (" + intFuncionario.toString() + ")");
     serieFuncionario.set(" ", numFuncionario);

     ChartSeries serieProfesional = new ChartSeries();
     serieProfesional.setLabel("Profesionales" + " (" + intProfesional.toString() + ")");
     serieProfesional.set(" ", numProfesional);

     ChartSeries serieTecnico = new ChartSeries();
     serieTecnico.setLabel("Tecnicos" + " (" + intTecnico.toString() + ")");
     serieTecnico.set(" ", numTecnico);

     ChartSeries serieScivil = new ChartSeries();
     serieScivil.setLabel("Sociedad Civil" + " (" + intScivil.toString() + ")");
     serieScivil.set(" ", numScivil);

     ChartSeries serieOtros = new ChartSeries();
     serieOtros.setLabel("Otros" + " (" + intOtros.toString() + ")");
     serieOtros.set(" ", numOtros);

     this.modeloBarraPublico.addSeries(serieAutoridad);
     this.modeloBarraPublico.addSeries(serieFuncionario);
     this.modeloBarraPublico.addSeries(serieProfesional);
     this.modeloBarraPublico.addSeries(serieTecnico);
     this.modeloBarraPublico.addSeries(serieScivil);
     this.modeloBarraPublico.addSeries(serieOtros);

     Axis xAxis = this.modeloBarraPublico.getAxis(AxisType.X);
     xAxis.setLabel("Publico Objetivo");

     Axis yAxis = this.modeloBarraPublico.getAxis(AxisType.Y);
     yAxis.setLabel("Cantidad");

     yAxis.setMin(0);

     this.modeloBarraPublico.setTitle("REPORTE DE ACTIVIDADES POR PUBLICO OBJETIVO");
     this.modeloBarraPublico.setLegendPosition("ne");
     this.modeloBarraPublico.setShowDatatip(false);
     this.modeloBarraPublico.setShowPointLabels(true);

     } catch (Exception e) {

     Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
     }
     }*/
    public void generarEstado() {

        long startTime = System.currentTimeMillis();
        this.modeloBarraEstado = new BarChartModel();

        int ianulado = 0;
        int iprogramado = 0;
        int ireprogramado = 0;
        int iejecutado = 0;
        int icerrado = 0;

        try {

            List<VwReporteActividad> vwReporteEstadosRegistrados = vwReporteActividadCallService.findAll();

            for (VwReporteActividad vwReporteEstado : vwReporteEstadosRegistrados) {
                if (null != vwReporteEstado.getTxtEstadoActividadGob() && true == vwReporteEstado.getTxtEstadoActividadGob().equals("ANULADO")) {
                    ianulado++;
                } else if (null != vwReporteEstado.getTxtEstadoActividadGob() && true == vwReporteEstado.getTxtEstadoActividadGob().equals("PROGRAMADO")) {
                    iprogramado++;
                } else if (null != vwReporteEstado.getTxtEstadoActividadGob() && true == vwReporteEstado.getTxtEstadoActividadGob().equals("REPROGRAMADO")) {
                    ireprogramado++;
                } else if (null != vwReporteEstado.getTxtEstadoActividadGob() && true == vwReporteEstado.getTxtEstadoActividadGob().equals("EJECUTADO")) {
                    iejecutado++;
                } else if (null != vwReporteEstado.getTxtEstadoActividadGob() && true == vwReporteEstado.getTxtEstadoActividadGob().equals("CERRADO")) {
                    icerrado++;
                }
            }

            Integer intAnulado = ianulado;
            Integer intProgramado = iprogramado;
            Integer intReprogramado = ireprogramado;
            Integer intEjecutado = iejecutado;
            Integer intCerrado = icerrado;

            Number numAnulado = (Number) intAnulado;
            Number numProgramado = (Number) intProgramado;
            Number numReprogramado = (Number) intReprogramado;
            Number numEjecutado = (Number) intEjecutado;
            Number numCerrado = (Number) intCerrado;

            ChartSeries serieAnulado = new ChartSeries();
            serieAnulado.setLabel("Anulado" + " (" + intAnulado.toString() + ")");
            serieAnulado.set(" ", numAnulado);

            ChartSeries serieProgramado = new ChartSeries();
            serieProgramado.setLabel("Programado" + " (" + intProgramado.toString() + ")");
            serieProgramado.set(" ", numProgramado);

            ChartSeries serieReprogramado = new ChartSeries();
            serieReprogramado.setLabel("Re-Programado" + " (" + intReprogramado.toString() + ")");
            serieReprogramado.set(" ", numReprogramado);

            ChartSeries serieEjecutado = new ChartSeries();
            serieEjecutado.setLabel("Ejecutado" + " (" + intEjecutado.toString() + ")");
            serieEjecutado.set(" ", numEjecutado);

            ChartSeries serieCerrado = new ChartSeries();
            serieCerrado.setLabel("Cerrado" + " (" + intCerrado.toString() + ")");
            serieCerrado.set(" ", numCerrado);

            this.modeloBarraEstado.addSeries(serieAnulado);
            this.modeloBarraEstado.addSeries(serieProgramado);
            this.modeloBarraEstado.addSeries(serieReprogramado);
            this.modeloBarraEstado.addSeries(serieEjecutado);
            this.modeloBarraEstado.addSeries(serieCerrado);

            Axis xAxis = this.modeloBarraEstado.getAxis(AxisType.X);
            xAxis.setLabel("Estado");

            Axis yAxis = this.modeloBarraEstado.getAxis(AxisType.Y);
            yAxis.setLabel("Cantidad");

            yAxis.setMin(0);

            this.modeloBarraEstado.setTitle("REPORTE DE ACTIVIDADES POR ESTADO");
            this.modeloBarraEstado.setLegendPosition("ne");
            this.modeloBarraEstado.setShowDatatip(false);
            this.modeloBarraEstado.setShowPointLabels(true);

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.REPORTE.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {

            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarTipoObjetivo() {

        long startTime = System.currentTimeMillis();
        this.modeloBarraTipoObjetivo = new BarChartModel();

        int idesempeno = 0;
        int iaprendizaje = 0;

        try {

            List<VwReporteActividad> vwReporteActividadesRegistradas = vwReporteActividadCallService.findAll();

            for (VwReporteActividad vwReporteActividad : vwReporteActividadesRegistradas) {
                if (null != vwReporteActividad.getTxtTipoObjetivo() && true == vwReporteActividad.getTxtTipoObjetivo().equals("DESEMPEÑO")) {
                    idesempeno++;
                } else if (null != vwReporteActividad.getTxtTipoObjetivo() && true == vwReporteActividad.getTxtTipoObjetivo().equals("APRENDIZAJE")) {
                    iaprendizaje++;
                }
            }

            Integer intDesempeno = idesempeno;
            Integer intAprendizaje = iaprendizaje;

            Number numDesempeno = (Number) intDesempeno;
            Number numAprendizaje = (Number) intAprendizaje;

            ChartSeries serieDesempeno = new ChartSeries();
            serieDesempeno.setLabel("Desempeño" + " (" + intDesempeno.toString() + ")");
            serieDesempeno.set(" ", numDesempeno);

            ChartSeries serieAprendizaje = new ChartSeries();
            serieAprendizaje.setLabel("Aprendizaje" + " (" + intAprendizaje.toString() + ")");
            serieAprendizaje.set(" ", numAprendizaje);

            this.modeloBarraTipoObjetivo.addSeries(serieDesempeno);
            this.modeloBarraTipoObjetivo.addSeries(serieAprendizaje);

            Axis xAxis = this.modeloBarraTipoObjetivo.getAxis(AxisType.X);
            xAxis.setLabel("Tipo Objetivo");

            Axis yAxis = this.modeloBarraTipoObjetivo.getAxis(AxisType.Y);
            yAxis.setLabel("Cantidad");

            yAxis.setMin(0);

            this.modeloBarraTipoObjetivo.setTitle("REPORTE DE ACTIVIDADES POR TIPO OBJETIVO");
            this.modeloBarraTipoObjetivo.setLegendPosition("ne");
            this.modeloBarraTipoObjetivo.setShowDatatip(false);
            this.modeloBarraTipoObjetivo.setShowPointLabels(true);

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.REPORTE.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {

            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }

    }

    public void generarProfesional() {

        long startTime = System.currentTimeMillis();
        this.modeloBarraTipoObjetivo = new BarChartModel();

        int idesempeno = 0;
        int iaprendizaje = 0;

        try {

            List<VwReporteActividad> vwReporteActividadesRegistradas = vwReporteActividadCallService.findAll();

            for (VwReporteActividad vwReporteActividad : vwReporteActividadesRegistradas) {
                if (null != vwReporteActividad.getTxtTipoObjetivo() && true == vwReporteActividad.getTxtTipoObjetivo().equals("DESEMPEÑO")) {
                    idesempeno++;
                } else if (null != vwReporteActividad.getTxtTipoObjetivo() && true == vwReporteActividad.getTxtTipoObjetivo().equals("APRENDIZAJE")) {
                    iaprendizaje++;
                }
            }

            Integer intDesempeno = idesempeno;
            Integer intAprendizaje = iaprendizaje;

            Number numDesempeno = (Number) intDesempeno;
            Number numAprendizaje = (Number) intAprendizaje;

            ChartSeries serieDesempeno = new ChartSeries();
            serieDesempeno.setLabel("Desempeño" + " (" + intDesempeno.toString() + ")");
            serieDesempeno.set(" ", numDesempeno);

            ChartSeries serieAprendizaje = new ChartSeries();
            serieAprendizaje.setLabel("Aprendizaje" + " (" + intAprendizaje.toString() + ")");
            serieAprendizaje.set(" ", numAprendizaje);

            this.modeloBarraTipoObjetivo.addSeries(serieDesempeno);
            this.modeloBarraTipoObjetivo.addSeries(serieAprendizaje);

            Axis xAxis = this.modeloBarraTipoObjetivo.getAxis(AxisType.X);
            xAxis.setLabel("Tipo Objetivo");

            Axis yAxis = this.modeloBarraTipoObjetivo.getAxis(AxisType.Y);
            yAxis.setLabel("Cantidad");

            yAxis.setMin(0);

            this.modeloBarraTipoObjetivo.setTitle("REPORTE DE ACTIVIDADES POR TIPO OBJETIVO");
            this.modeloBarraTipoObjetivo.setLegendPosition("ne");
            this.modeloBarraTipoObjetivo.setShowDatatip(false);
            this.modeloBarraTipoObjetivo.setShowPointLabels(true);

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.REPORTE.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {

            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }

    }

    public List<VwReporteActividad> obtenerReporte() {

        //List<VwReporteActividad> actividades = null;
        //this.entidades = vwReporteActividadCallService.obtenerPorTrimestreEstado(this.getEntidadSeleccionada().getNumTrimestre(), this.getEntidadSeleccionada().getNidEstadoActividadGob(), this.getEntidadSeleccionada().getNidArea());
        return this.entidades;
    }

    public void loadVwReporteActividadList() {
        logger.info(":: ActividadGobAdministrado.loadActividadGobList :: Starting execution...");
        long startTime = System.currentTimeMillis();
        //ActividadGEAdministrado actividadGEAdministrado = (ActividadGEAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGEAdministrado}").getValue(getFacesContext());
        try {
            Map<String, Object> parameters = new HashMap<>();
            //parameters.put("nidEstadoActividad", CoreConstant.STATUS_ACTIVE);
            if (null != this.getEntidadSeleccionada().getNumTrimestre()) {
                parameters.put("numTrimestre", this.getEntidadSeleccionada().getNumTrimestre());
            }
            if (null != this.getEntidadSeleccionada().getNumAnio()) {
                parameters.put("numAnio", this.getEntidadSeleccionada().getNumAnio());
            }
            if (null != this.getEntidadSeleccionada().getNidDepartamento()) {
                parameters.put("nidDepartamento", this.getEntidadSeleccionada().getNidDepartamento());
            }
            if (null != this.getEntidadSeleccionada().getNidEstadoActividadGob()) {
                parameters.put("nidEstadoActividadGob", this.getEntidadSeleccionada().getNidEstadoActividadGob());
            }
            if (null != this.getEntidadSeleccionada().getNidArea()) {
                parameters.put("nidArea", this.getEntidadSeleccionada().getNidArea());
            }
            //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("nidActividadGob");
            this.entidades = vwReporteActividadCallService.loadVwReporteActList(findByParamBean);

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.REPORTE.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error loadActividadGobList" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGobAdministrado.loadActividadGobList :: Execution finish.");
    }

    public void limpiarReporte() {
        this.entidad = new VwReporteActividad();
        this.entidadSeleccionada = new VwReporteActividad();
        this.entidades = new ArrayList<>();
    }

}
