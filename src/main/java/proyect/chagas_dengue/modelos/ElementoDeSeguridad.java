package proyect.chagas_dengue.modelos;

public class ElementoDeSeguridad {
    private String tipo;
    private String descripcion;

    public ElementoDeSeguridad(String tipo, String descripcion) {
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
}
