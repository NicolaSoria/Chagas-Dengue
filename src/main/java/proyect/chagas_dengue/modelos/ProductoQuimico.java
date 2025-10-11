package proyect.chagas_dengue.modelos;

public class ProductoQuimico {
    private String nombre;
    private String marca;
    private double presentacion; // cantidad, litros, etc.

    public ProductoQuimico(String nombre, String marca, double presentacion) {
        this.nombre = nombre;
        this.marca = marca;
        this.presentacion = presentacion;
    }

    public String getNombre() { return nombre; }
    public String getMarca() { return marca; }
    public double getPresentacion() { return presentacion; }
}

