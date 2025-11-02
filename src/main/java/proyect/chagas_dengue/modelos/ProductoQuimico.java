package proyect.chagas_dengue.modelos;

public class ProductoQuimico {
    private int PCod;
    private String nombre;
//    private String marca;
    private double presentacion; // cantidad, litros, etc.

    public ProductoQuimico(int PCod, String nombre, double presentacion){
        this.PCod = PCod;
        this.nombre = nombre;
//        this.marca = marca;
        this.presentacion = presentacion;
    }

    public ProductoQuimico(String nombre, String marca, double presentacion) {
        this.nombre = nombre;
//        this.marca = marca;
        this.presentacion = presentacion;
    }

    public void setPCod(int PCod) {this.PCod = PCod;}
    public void setNombre(String nombre) {this.nombre = nombre;}
//    public void setMarca(String marca) {this.marca = marca;}
    public void setPresentacion(double presentacion) {this.presentacion = presentacion;}

    public int getPCod() {return PCod;}
    public String getNombre() { return nombre; }
//    public String getMarca() { return marca; }
    public double getPresentacion() { return presentacion; }

    @Override
    public String toString() {
        return "Producto: " + nombre + " | Presentación: " + presentacion;
    }

}


