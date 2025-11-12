package proyect.chagas_dengue.modelos;

import java.time.LocalDate;
import java.util.List;

public class Registro {
    private int RCod;
    private String observacion;
    private LocalDate fecha;
    private Zona zona;
    private Tarea tarea;
    private Viaje viaje;

    public Registro(){}

    public Registro(int RCod,String observacion, LocalDate fecha, Zona zona, Tarea tarea, Viaje viaje){
        this.RCod = RCod;
        this.observacion = observacion;
        this.fecha = fecha;
        this.zona = zona;
        this.tarea = tarea;
        this.viaje = viaje;
    }

    public Registro(int RCod, Zona zona, Tarea tarea, Viaje viaje){
        this.RCod = RCod;
        this.zona = zona;
        this.tarea = tarea;
        this.viaje = viaje;
    }

    public Registro(Viaje viaje, Zona zona, Tarea tarea) {
        this.viaje = viaje;
        this.zona = zona;
        this.tarea = tarea;
    }

    public int getRCod() {
        return RCod;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public Zona getZona() {
        return zona;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setRCod(int RCod) {
        this.RCod = RCod;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }
}
