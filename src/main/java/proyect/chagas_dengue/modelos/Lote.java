package proyect.chagas_dengue.modelos;

public class Lote {
    private int LCod;
    private ProductoQuimico producto;
    private double cantidad; // cantidad disponible en este lote

    public Lote(int DLCod, ProductoQuimico producto, double cantidad) {
        this.LCod = DLCod;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Lote(ProductoQuimico producto, double cantidad) {
        this(0, producto, cantidad);
    }

    public int getDLCod() { return LCod; }
    public void setDLCod(int DLCod) { this.LCod = DLCod; }

    public ProductoQuimico getProducto() { return producto; }
    public void setProducto(ProductoQuimico producto) { this.producto = producto; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        return "Lote #" + LCod + " | Producto: " + producto.getNombre() + " | Cantidad: " + cantidad;
    }
}

