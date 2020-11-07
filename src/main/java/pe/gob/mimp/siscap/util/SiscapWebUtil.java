/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.siscap.modelo.Disponibilidad;
import pe.gob.mimp.siscap.modelo.Rendimiento;

/**
 *
 * @author Omar
 */
public class SiscapWebUtil {

    public static BigDecimal obtenerTiempoEjecucionMillis(long startTime, long stopTime) {

        double totalTime = stopTime - startTime;
//        return new BigDecimal(String.valueOf(totalTime / 1000.0));
        return nuevoValorBigDecimal(String.valueOf(totalTime));
    }

    public static BigDecimal convertirMillisegundosToHora(BigDecimal millisegundos) {
        return millisegundos.divide(
                nuevoValorBigDecimal("3600000"),
                9,
                RoundingMode.HALF_UP
        );
    }

    public static Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static Date obtenerFechaActual() {
        return new Date();
    }

    public static BigDecimal nuevoValorBigDecimal(String value) {
        return new BigDecimal(value);
    }

    public static Boolean esValorCero(Object object) {

        if (Objects.isNull(object)) {
            return false;
        }

        if (object instanceof BigDecimal) {
            return ((BigDecimal) object).stripTrailingZeros().equals(BigDecimal.ZERO);
        }

        if (object instanceof BigInteger) {
            return BigInteger.ZERO.equals(object);
        }

        return false;
    }

    public static long obtenerDiferenciaEnMinutosEntreFechas(Date fechaInicial, Date fechaUltimo) {
        long resultadoEnMillisegundos = fechaUltimo.getTime() - fechaInicial.getTime();
        return (long) ((resultadoEnMillisegundos / 1000.0) / 60);
    }

    public static Rendimiento crearRendimiento(String nombreMetodo, BigInteger nidFuncionalidad, BigDecimal numTimeResponse, BigInteger nidUsuario) throws Exception {

        Rendimiento rendimiento = new Rendimiento();

        rendimiento.setTxtNombreMetodo(nombreMetodo);
        rendimiento.setNidFuncionalidad(nidFuncionalidad);
        rendimiento.setNumTimeResponse(numTimeResponse);
        rendimiento.setFlgActivo(BigInteger.ONE);
        rendimiento.setNidUsuario(nidUsuario);
        rendimiento.setTxtPc(Internet.obtenerNombrePC());
        rendimiento.setTxtIp(Internet.obtenerIPPC());
        rendimiento.setFecRegistro(obtenerFechaActual());

        return rendimiento;
    }

    public static Disponibilidad crearDisponibilidad(BigInteger nidFuncionalidad, Date fecDisponible,
            Date fecNoDisponible, BigInteger numTiempoMedio, BigInteger nidUsuario) {

        try {

            Disponibilidad disponibilidad = new Disponibilidad();
            disponibilidad.setFecDisponible(fecDisponible);
            disponibilidad.setFecNoDisponible(fecNoDisponible);
            disponibilidad.setNumTiempoMedio(numTiempoMedio);
            disponibilidad.setNidFuncionalidad(nidFuncionalidad);
            disponibilidad.setFlgActivo(SiscapWebConstantes.FLG_ACTIVO);
            disponibilidad.setNidUsuario(nidUsuario);
            disponibilidad.setTxtPc(Internet.obtenerNombrePC());
            disponibilidad.setTxtIp(Internet.obtenerIPPC());
            disponibilidad.setFecRegistro(obtenerFechaActual());

            return disponibilidad;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Metodo que implementa la formula (TMPF/(TMPF + TMPR))*100 del indicador
     * DISPONIBILIDAD
     *
     * @param tiempoMedioFalla tiempo medio para la falla
     * @param tiempoMedioReparacion tiempo medio para la reparacion
     * @return resultado del calculo de DISPONIBILIDAD
     */
    public static BigInteger calculoFormulaDisponibilidad(BigInteger tiempoMedioFalla, BigInteger tiempoMedioReparacion) {

        if (esValorCero(tiempoMedioReparacion)) {
            return BigInteger.ZERO;
        }

        BigDecimal tiempoMedioFallaToBigDecimal = new BigDecimal(tiempoMedioFalla);
        BigDecimal tiempoMedioReparacionToBigDecimal = new BigDecimal(tiempoMedioReparacion);

        BigDecimal calculoDisponibilidad = (tiempoMedioFallaToBigDecimal
                .divide(
                        (tiempoMedioFallaToBigDecimal.add(tiempoMedioReparacionToBigDecimal)),
                        2,
                        RoundingMode.HALF_UP))
                .multiply(nuevoValorBigDecimal("100"));

        return calculoDisponibilidad
                .setScale(0, RoundingMode.HALF_UP)
                .toBigInteger();
    }

    /**
     * Metodo que implementa la formula NTP/OT del indicador RENDIMIENTO
     *
     * @param numeroTareasRealizadas numero de tareas realizadas
     * @param tiempoObservacion tiempo de observacion
     * @return resultado del calculo de RENDIMIENTO
     */
    public static BigDecimal calculoFormulaRendimiento(BigInteger numeroTareasRealizadas, BigDecimal tiempoObservacion) {

        if (esValorCero(tiempoObservacion)) {
            return BigDecimal.ZERO;
        }

        BigDecimal numeroTareasRealizadasToBigDecimal = new BigDecimal(numeroTareasRealizadas);

        //Interpretado como : NTP/OT
        BigDecimal calculoRendimiento = numeroTareasRealizadasToBigDecimal
                .divide(tiempoObservacion,
                        2,
                        RoundingMode.HALF_UP
                );

        return calculoRendimiento;
    }

}
