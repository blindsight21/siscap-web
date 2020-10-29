/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.util;

import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import pe.gob.mimp.siscap.ws.tipogobierno.cliente.TipoGobiernoCallService;

/**
 *
 * @author Omar
 */
@Singleton
@Startup
@Default
public class Prueba {
    
    @Inject
    private TipoGobiernoCallService tipoGobiernoCallService;
    
    @PostConstruct
    public void onStartUp() {
        System.out.println("onStartUp");
    }
    
    @PreDestroy
    public void onShutDown(){
        System.out.println("onShutDown");
    }
    
    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    public void execute() {
        System.out.println(":::::::::Iniciando tareas programadas:::::::::::::");
        /*A partir de acá irán las tareas programadas*/
//        tarea.evaluarDiasHabiles();
        System.out.println(tipoGobiernoCallService.find(BigDecimal.ONE));

        System.out.println(":::::::::Terminando tareas programadas:::::::::::::");
    }

    
}
