package proyect.chagas_dengue.modelos;

import java.time.LocalDate;

public class Viaje {
    public enum Tipo{
        Diario,Barrido
    }
    private int VCod;
    private LocalDate fechaInicio;
    private Tipo tipo;

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
}


