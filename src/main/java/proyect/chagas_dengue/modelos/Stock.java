package proyect.chagas_dengue.modelos;

public class Stock {
    private int SPCod; // Código del stock (corresponde a producto)
    private ProductoQuimico producto; // Producto asociado
    private int total; // Cantidad total disponible en stock

    public Stock() {}

    public Stock(int SPCod, ProductoQuimico producto, int total) {
        this.SPCod = SPCod;
        this.producto = producto;
        this.total = total;
    }

    // Getters y setters
    public int getSPCod() { return SPCod; }
    public void setSPCod(int SPCod) { this.SPCod = SPCod; }

    public ProductoQuimico getProducto() { return producto; }
    public void setProducto(ProductoQuimico producto) { this.producto = producto; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public void restarCantidad(int cantidad) {
        if (cantidad <= total) {
            total -= cantidad;
        } else {
            throw new IllegalArgumentException("No hay suficiente stock para restar " + cantidad);
        }
    }

    public void agregarCantidad(int cantidad) {
        total += cantidad;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "SPCod=" + SPCod +
                ", Producto=" + producto.getNombre() +
                ", Total=" + total +
                '}';
    }
}

