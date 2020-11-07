/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.siscap.bean.RendimientoVO;
import pe.gob.mimp.siscap.modelo.Rendimiento;
import pe.gob.mimp.siscap.modelo.TipoObjetivo;
import pe.gob.mimp.siscap.util.SiscapWebConstantes;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.util.Util;

/**
 *
 * @author Omar
 */
@ManagedBean
@ViewScoped
public class RendimientoAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(TipoObjetivo.class.getName());

    @Inject
    private RendimientoCallService rendimientoCallService;

    private RendimientoVO entidadSeleccionada;
    private List<RendimientoVO> rendimientoList;

    public RendimientoAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        limpiarRendimiento();
    }

    @PreDestroy
    public void destroy() {
        System.out.println("ESTO ESTA DESTRUYENDOSE");
    }

    public RendimientoVO getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(RendimientoVO entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<RendimientoVO> getRendimientoList() {
        return rendimientoList;
    }

    public void setRendimientoList(List<RendimientoVO> rendimientoList) {
        this.rendimientoList = rendimientoList;
    }

    public void limpiarRendimiento() {
        this.entidadSeleccionada = new RendimientoVO();
        this.rendimientoList = new ArrayList<>();
    }

    public List<RendimientoVO> loadRendimientoList() {

        List<EnumFuncionalidad> enumFuncionalidadList = Arrays.asList(EnumFuncionalidad.values());

        List<RendimientoVO> rendimientoVOList = new ArrayList<>();

        AtomicReference<BigInteger> itemAtomicReference = new AtomicReference<>(BigInteger.ZERO);
        enumFuncionalidadList.forEach(enumFuncionalidad -> {

            itemAtomicReference.set(itemAtomicReference.get().add(BigInteger.ONE));

            RendimientoVO rendimientoVO = new RendimientoVO();
            rendimientoVO.setItem(itemAtomicReference.get());
            rendimientoVO.setModulo(enumFuncionalidad.getTxtFuncionalidad());
            rendimientoVO.setNidFuncionalidad(enumFuncionalidad.getNidFuncionalidadBigInteger());
            rendimientoVO.setNumeroTareasRealizadas(BigInteger.valueOf(enumFuncionalidad.getTareasPorFuncionalidad()));
            rendimientoVO.setTiempoObservacion(null);
            rendimientoVO.setPorcentajeRendimiento(null);

            rendimientoVOList.add(rendimientoVO);

        });

        if (Util.esListaVacia(rendimientoVOList)) {
            return null;
        }

        rendimientoList = rendimientoVOList.stream().collect(Collectors.toList());

        rendimientoList.forEach((rendimientoVO) -> {

            rendimientoVO.setTiempoObservacion(
                    obtenerTiempoObservacion(
                            rendimientoVO.getNidFuncionalidad()
                    )
            );
            rendimientoVO.setPorcentajeRendimiento(
                    SiscapWebUtil.calculoFormulaRendimiento(
                            rendimientoVO.getNumeroTareasRealizadas(),
                            rendimientoVO.getTiempoObservacion()
                    )
            );
        });

        return rendimientoList;
    }

    private BigDecimal obtenerTiempoObservacion(BigInteger nidFuncionalidad) {

//        BigDecimal promedioTiempoObservacion = BigDecimal.ZERO;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("flgActivo", SiscapWebConstantes.FLG_ACTIVO);
        parameters.put("nidFuncionalidad", nidFuncionalidad);
        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidRendimiento");

        List<Rendimiento> rendimientoFromBDList = rendimientoCallService.loadRendimientoList(findByParamBean);

        if (Util.esListaVacia(rendimientoFromBDList)) {
            return BigDecimal.ZERO;
        }

        Map<String, List<Rendimiento>> agrupacionPorNombreMetodo = rendimientoFromBDList.stream().collect(
                Collectors.groupingBy(Rendimiento::getTxtNombreMetodo,
                        Collectors.toList()
                ));

        List<BigDecimal> avgTotal = new ArrayList();

        agrupacionPorNombreMetodo.forEach((k, v) -> {
            AtomicReference<BigDecimal> sumaNumTimeResponsePorNombreMetodo = new AtomicReference<>(BigDecimal.ZERO);
            v.forEach(x -> {
                sumaNumTimeResponsePorNombreMetodo.set(sumaNumTimeResponsePorNombreMetodo.get().add(x.getNumTimeResponse()));
            });

            BigDecimal avg = sumaNumTimeResponsePorNombreMetodo.get().divide(
                    new BigDecimal(v.size()),
                    2,
                    RoundingMode.HALF_UP);

            avgTotal.add(avg);
        });

        AtomicReference<BigDecimal> sumaTimeResponse = new AtomicReference<>(BigDecimal.ZERO);
        avgTotal.forEach(x -> {
            sumaTimeResponse.set(sumaTimeResponse.get().add(x));
        });

        return SiscapWebUtil.convertirMillisegundosToHora(sumaTimeResponse.get())
                .setScale(9, RoundingMode.HALF_UP);
    }
}
