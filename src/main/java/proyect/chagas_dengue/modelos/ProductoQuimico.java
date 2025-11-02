package proyect.chagas_dengue.modelos;

public class ProductoQuimico {
    private int PCod;
    private String nombre;
    private double presentacion; // cantidad, litros, etc.
    private Stock stock;

    public ProductoQuimico(int PCod, String nombre, double presentacion){
        this.PCod = PCod;
        this.nombre = nombre;
        this.presentacion = presentacion;
    }

    public ProductoQuimico(int PCod, String nombre, double presentacion,Stock stock) {
        this.PCod = PCod;
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.stock = stock;
    }

    public void setPCod(int PCod) {this.PCod = PCod;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setPresentacion(double presentacion) {this.presentacion = presentacion;}
    public void setStock(Stock stock) {this.stock = stock;}

    public int getPCod() {return PCod;}
    public String getNombre() { return nombre; }
    public double getPresentacion() { return presentacion; }
    public Stock getStock() { return stock; }

    @Override
    public String toString() {
        return "Producto: " + nombre + " | Presentación: " + presentacion;
    }

}


