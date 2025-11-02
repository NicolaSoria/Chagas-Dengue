package proyect.chagas_dengue.modelos;

public class Detalle {
    private int DCod;
    private Viaje viaje;
    private Stock stock;
    private int cantTomada;
    private int cantUsada;


    public Detalle(){}

    public Detalle(int DCod, Viaje viaje, Stock stock, int cantTomada, int cantUsada) {
        this.DCod = DCod;
        this.viaje = viaje;
        this.stock = stock;
        this.cantTomada = cantTomada;
        this.cantUsada = cantUsada;
    }

    public Detalle(Viaje viaje, Stock stock, int cantTomada, int cantUsada) {
        this(0, viaje, stock, cantTomada, cantUsada);
    }

    public int getDCod() { return DCod; }
    public void setDCod(int DCod) { this.DCod = DCod; }

    public Viaje getViaje() { return viaje; }
    public void setViaje(Viaje viaje) { this.viaje = viaje; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public ProductoQuimico getProducto() { return stock.getProducto(); } // accesible desde el detalle

    public int getCantTomada() { return cantTomada; }
    public void setCantTomada(int cantTomada) { this.cantTomada = cantTomada; }

    public int getCantUsada() { return cantUsada; }
    public void setCantUsada(int cantUsada) { this.cantUsada = cantUsada; }

    public int getSobrante() { return cantTomada - cantUsada; }

    @Override
    public String toString() {
        return "Detalle{" +
                "DCod=" + DCod +
                ", Viaje=" + viaje.getVCod() +
                ", Stock=" + stock.getSPCod() +
                ", Producto=" + getProducto().getNombre() +
                ", Tomado=" + cantTomada +
                ", Usado=" + cantUsada +
                ", Sobrante=" + getSobrante() +
                '}';
    }
}
