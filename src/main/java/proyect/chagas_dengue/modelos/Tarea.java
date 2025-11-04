package proyect.chagas_dengue.modelos;

import java.util.TreeMap;

public class Tarea {
    private int TCod;
    private String tipo;
    private String descripcion;

    public Tarea(){
    }

    public Tarea(int TCod, String tipo, String descripcion){
        this.TCod = TCod;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public Tarea(String tipo, String descripcion){
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public int getTCod() { return TCod; }

    public void setTCod(int TCod) { this.TCod = TCod; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String toString(){
        return tipo;
    }
}
