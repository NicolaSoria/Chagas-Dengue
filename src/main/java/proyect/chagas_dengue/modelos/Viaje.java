package proyect.chagas_dengue.modelos;

import java.time.LocalDate;

public class Viaje {
    public enum Tipo{
        Diario,Barrido
    }
    public enum Estado{
        Pendiente,Finalizado
    }
    private int VCod;
    private LocalDate fechaInicio;
    private Tipo tipo;
    private Estado estado;

    public Viaje(){
        this.fechaInicio = null;
        this.tipo = null;
    }

    public Viaje(LocalDate fechaInicio, Tipo tipo) {
        this.fechaInicio = fechaInicio;
        this.tipo = tipo;
    }

    public Viaje(int vCod, LocalDate fechaInicio, Tipo tipo) {
        this.VCod = vCod;
        this.fechaInicio = fechaInicio;
        this.tipo = tipo;
    }

    public int getVCod() {
        return VCod;
    }

    public void setVCod(int vCod) {
        this.VCod = vCod;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Estado getEstado(){
        return estado;
    }

    public void setEstado(Estado estado){
        this.estado = estado;
    }
}


