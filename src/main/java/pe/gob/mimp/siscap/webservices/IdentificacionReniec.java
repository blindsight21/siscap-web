/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author desarrollador
 */
public class IdentificacionReniec {

    private static final int HTTP_REQUEST_TIMEOUT = 3000;

    private String COD_ERROR;
    private String APELLIDO_PATERNO;
    private String APELLIDO_MATERNO;
    private String APELLIDO_CASADA;
    private String NOMBRES;
    private String COD_DEPARTAMENTO_DOMICILIO;
    private String COD_PROVINCIA_DOMICILIO;
    private String COD_DISTRITO_DOMICILIO;
    private String DEPARTAMENTO_DOMICILIO;
    private String PROVINCIA_DOMICILIO;
    private String DISTRITO_DOMICILIO;
    private String DIRECCION_DOMICILIO;
    private BigDecimal ESTADO_CIVIL;
    private BigDecimal GENERO;
    private String COD_DEPARTAMENTO_NACIMIENTO;
    private String COD_PROVINCIA_NACIMIENTO;
    private String COD_DISTRITO_NACIMIENTO;
    private String DEPARTAMENTO_NACIMIENTO;
    private String PROVINCIA_NACIMIENTO;
    private String DISTRITO_NACIMIENTO;
    private Date FEC_NACIMIENTO;
    private Date FEC_INSCRIPCION;
    private Date FEC_EMISION;
    private Date FEC_CADUCIDAD;
    private BigDecimal DIG_VERIFICACION;
    private String NUM_DNI;

    private String APELLIDOS_NOMBRES;
    private String RESPUESTACONSULTA;

    public IdentificacionReniec() {
    }

    public String getCOD_ERROR() {
        return COD_ERROR;
    }

    public void setCOD_ERROR(String COD_ERROR) {
        this.COD_ERROR = COD_ERROR;
    }

    public String getAPELLIDO_PATERNO() {
        return APELLIDO_PATERNO;
    }

    public void setAPELLIDO_PATERNO(String APELLIDO_PATERNO) {
        this.APELLIDO_PATERNO = APELLIDO_PATERNO;
    }

    public String getAPELLIDO_MATERNO() {
        return APELLIDO_MATERNO;
    }

    public void setAPELLIDO_MATERNO(String APELLIDO_MATERNO) {
        this.APELLIDO_MATERNO = APELLIDO_MATERNO;
    }

    public String getAPELLIDO_CASADA() {
        return APELLIDO_CASADA;
    }

    public void setAPELLIDO_CASADA(String APELLIDO_CASADA) {
        this.APELLIDO_CASADA = APELLIDO_CASADA;
    }

    public String getNOMBRES() {
        return NOMBRES;
    }

    public void setNOMBRES(String NOMBRES) {
        this.NOMBRES = NOMBRES;
    }

    public String getCOD_DEPARTAMENTO_DOMICILIO() {
        return COD_DEPARTAMENTO_DOMICILIO;
    }

    public void setCOD_DEPARTAMENTO_DOMICILIO(String COD_DEPARTAMENTO_DOMICILIO) {
        this.COD_DEPARTAMENTO_DOMICILIO = COD_DEPARTAMENTO_DOMICILIO;
    }

    public String getCOD_PROVINCIA_DOMICILIO() {
        return COD_PROVINCIA_DOMICILIO;
    }

    public void setCOD_PROVINCIA_DOMICILIO(String COD_PROVINCIA_DOMICILIO) {
        this.COD_PROVINCIA_DOMICILIO = COD_PROVINCIA_DOMICILIO;
    }

    public String getCOD_DISTRITO_DOMICILIO() {
        return COD_DISTRITO_DOMICILIO;
    }

    public void setCOD_DISTRITO_DOMICILIO(String COD_DISTRITO_DOMICILIO) {
        this.COD_DISTRITO_DOMICILIO = COD_DISTRITO_DOMICILIO;
    }

    public String getDEPARTAMENTO_DOMICILIO() {
        return DEPARTAMENTO_DOMICILIO;
    }

    public void setDEPARTAMENTO_DOMICILIO(String DEPARTAMENTO_DOMICILIO) {
        this.DEPARTAMENTO_DOMICILIO = DEPARTAMENTO_DOMICILIO;
    }

    public String getPROVINCIA_DOMICILIO() {
        return PROVINCIA_DOMICILIO;
    }

    public void setPROVINCIA_DOMICILIO(String PROVINCIA_DOMICILIO) {
        this.PROVINCIA_DOMICILIO = PROVINCIA_DOMICILIO;
    }

    public String getDISTRITO_DOMICILIO() {
        return DISTRITO_DOMICILIO;
    }

    public void setDISTRITO_DOMICILIO(String DISTRITO_DOMICILIO) {
        this.DISTRITO_DOMICILIO = DISTRITO_DOMICILIO;
    }

    public String getDIRECCION_DOMICILIO() {
        return DIRECCION_DOMICILIO;
    }

    public void setDIRECCION_DOMICILIO(String DIRECCION_DOMICILIO) {
        this.DIRECCION_DOMICILIO = DIRECCION_DOMICILIO;
    }

    public BigDecimal getESTADO_CIVIL() {
        return ESTADO_CIVIL;
    }

    public void setESTADO_CIVIL(BigDecimal ESTADO_CIVIL) {
        this.ESTADO_CIVIL = ESTADO_CIVIL;
    }

    public BigDecimal getGENERO() {
        return GENERO;
    }

    public void setGENERO(BigDecimal GENERO) {
        this.GENERO = GENERO;
    }

    public String getCOD_DEPARTAMENTO_NACIMIENTO() {
        return COD_DEPARTAMENTO_NACIMIENTO;
    }

    public void setCOD_DEPARTAMENTO_NACIMIENTO(String COD_DEPARTAMENTO_NACIMIENTO) {
        this.COD_DEPARTAMENTO_NACIMIENTO = COD_DEPARTAMENTO_NACIMIENTO;
    }

    public String getCOD_PROVINCIA_NACIMIENTO() {
        return COD_PROVINCIA_NACIMIENTO;
    }

    public void setCOD_PROVINCIA_NACIMIENTO(String COD_PROVINCIA_NACIMIENTO) {
        this.COD_PROVINCIA_NACIMIENTO = COD_PROVINCIA_NACIMIENTO;
    }

    public String getCOD_DISTRITO_NACIMIENTO() {
        return COD_DISTRITO_NACIMIENTO;
    }

    public void setCOD_DISTRITO_NACIMIENTO(String COD_DISTRITO_NACIMIENTO) {
        this.COD_DISTRITO_NACIMIENTO = COD_DISTRITO_NACIMIENTO;
    }

    public String getDEPARTAMENTO_NACIMIENTO() {
        return DEPARTAMENTO_NACIMIENTO;
    }

    public void setDEPARTAMENTO_NACIMIENTO(String DEPARTAMENTO_NACIMIENTO) {
        this.DEPARTAMENTO_NACIMIENTO = DEPARTAMENTO_NACIMIENTO;
    }

    public String getPROVINCIA_NACIMIENTO() {
        return PROVINCIA_NACIMIENTO;
    }

    public void setPROVINCIA_NACIMIENTO(String PROVINCIA_NACIMIENTO) {
        this.PROVINCIA_NACIMIENTO = PROVINCIA_NACIMIENTO;
    }

    public String getDISTRITO_NACIMIENTO() {
        return DISTRITO_NACIMIENTO;
    }

    public void setDISTRITO_NACIMIENTO(String DISTRITO_NACIMIENTO) {
        this.DISTRITO_NACIMIENTO = DISTRITO_NACIMIENTO;
    }

    public Date getFEC_NACIMIENTO() {
        return FEC_NACIMIENTO;
    }

    public void setFEC_NACIMIENTO(Date FEC_NACIMIENTO) {
        this.FEC_NACIMIENTO = FEC_NACIMIENTO;
    }

    public Date getFEC_INSCRIPCION() {
        return FEC_INSCRIPCION;
    }

    public void setFEC_INSCRIPCION(Date FEC_INSCRIPCION) {
        this.FEC_INSCRIPCION = FEC_INSCRIPCION;
    }

    public Date getFEC_EMISION() {
        return FEC_EMISION;
    }

    public void setFEC_EMISION(Date FEC_EMISION) {
        this.FEC_EMISION = FEC_EMISION;
    }

    public Date getFEC_CADUCIDAD() {
        return FEC_CADUCIDAD;
    }

    public void setFEC_CADUCIDAD(Date FEC_CADUCIDAD) {
        this.FEC_CADUCIDAD = FEC_CADUCIDAD;
    }

    public BigDecimal getDIG_VERIFICACION() {
        return DIG_VERIFICACION;
    }

    public void setDIG_VERIFICACION(BigDecimal DIG_VERIFICACION) {
        this.DIG_VERIFICACION = DIG_VERIFICACION;
    }

    public String getNUM_DNI() {
        return NUM_DNI;
    }

    public void setNUM_DNI(String NUM_DNI) {
        this.NUM_DNI = NUM_DNI;
    }

    public String getAPELLIDOS_NOMBRES() {
        return APELLIDOS_NOMBRES;
    }

    public void setAPELLIDOS_NOMBRES(String APELLIDOS_NOMBRES) {
        this.APELLIDOS_NOMBRES = APELLIDOS_NOMBRES;
    }

    public String getRESPUESTACONSULTA() {
        return RESPUESTACONSULTA;
    }

    public void setRESPUESTACONSULTA(String RESPUESTACONSULTA) {
        this.RESPUESTACONSULTA = RESPUESTACONSULTA;
    }

    public void obtenerConsultaReniec(String dni) throws MalformedURLException {

    }

    public String obtenerGenero(BigDecimal GENERO) {
        String genero = "";

        try {
            if (null != GENERO) {
                if (GENERO.equals(BigDecimal.ONE)) {
                    genero = "M";
                }
                if (GENERO.equals(BigDecimal.valueOf(2))) {
                    genero = "F";
                }
            }
        } catch (Exception e) {
            System.out.println("Genero: " + e.getMessage());
        }
        return genero;
    }

    public Date obtenerFormatoFecha(String fechaEnFormatoRecibido) {
        Date fechaEnFormatoNuevo = null;
        if (fechaEnFormatoRecibido.length() != 0) {
            //El formato que se está recibiendo
            SimpleDateFormat formatoRecibido = new SimpleDateFormat("yyyyMMdd");
            // parseando la fecha en cadena usando el formato original
            ParsePosition pos = new ParsePosition(0);
            fechaEnFormatoNuevo = formatoRecibido.parse(fechaEnFormatoRecibido, pos);
            /*Si queremos retornar a cadena:
             SimpleDateFormat nuevoFormato = new SimpleDateFormat("dd/MM/yyyy");
             String fechaEnFormatoNuevo2 = nuevoFormato.format(fechaFormatoRecibidoToDate);*/
            return fechaEnFormatoNuevo;
        } else {
            return fechaEnFormatoNuevo;
        }
    }

    public void obtenerConsultaReniecPIDE(String dni) throws MalformedURLException {
        //ReniecWsClient client = new ReniecWsClient();
        try {

            if (dni != null) {
                if (dni.length() == 8) {
//                    String urlProxy = "https://192.168.4.42:8181/wb-proxy-ws-pide-sunat/server/getDataReniec?nroDni=" + dni;
                    
                    //URI servicio PIDE
                    String urlProxy = "http://190.119.182.199:8080/wsService/faces/ConsultaReniec?nroDni=" + dni;

                    System.out.println("urlProxy:" + urlProxy);
                    try {
                        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                                new javax.net.ssl.HostnameVerifier() {
                            public boolean verify(String hostname,
                                    javax.net.ssl.SSLSession sslSession) {
                                if (hostname.equals("192.168.4.42")) {
                                    return true;
                                }
                                return false;
                            }
                        });

                        URL url = new URL(urlProxy);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setUseCaches(false);
                        conn.setDoOutput(true);
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Content-Type", "application/json");
                        InputStream instream = conn.getInputStream();//add                        
                        BufferedReader in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
                        System.out.println("calling ws...");
                        StringBuffer sb = new StringBuffer();
                        try {
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                sb.append(inputLine);
                            }
                            in.close();
                        } finally {
                            instream.close();
                        }
                        JSONObject jsonObjReturnFB = new JSONObject(sb.toString());
                        System.out.println("jsonObjReturnFB-coResultado:" + jsonObjReturnFB.get("coResultado"));

                        if (jsonObjReturnFB.get("coResultado").equals("0000")) {
                            System.out.println("jsonObjReturnFB-datosPersona:" + jsonObjReturnFB.get("datosPersona"));
                            JSONObject jsonDataReturn = jsonObjReturnFB.getJSONObject("datosPersona");
                            this.COD_ERROR = jsonObjReturnFB.get("coResultado").toString();
                            this.NOMBRES = jsonDataReturn.get("preNombres").toString();
                            this.APELLIDO_PATERNO = jsonDataReturn.get("apPrimer").toString();
                            this.APELLIDO_MATERNO = jsonDataReturn.get("apSegundo").toString();
                        }
                    } catch (IOException | JSONException e) {
                        Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
                    }
                }
            }

        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }
    }
}
