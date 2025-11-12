package proyect.chagas_dengue.modelos;

import java.time.LocalDate;
import java.util.List;

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
    private String destinosTexto;
    private List<Zona> zonas;
    private List<Camioneta> camionetas;
    private List<Maquina> maquinas;
    private List<Empleado> empleados;
    private List<Detalle> detalles;
    private List<Registro> registros;
    private String camionetasTexto;
    private String empleadosTexto;
    private String maquinasTexto;
    private String productosTexto;
    private String registrosTexto;

// Getters y setters para cada uno


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

    public Viaje(int vCod, LocalDate fechaInicio, Tipo tipo, Estado estado) {
        this.VCod = vCod;
        this.fechaInicio = fechaInicio;
        this.tipo = tipo;
        this.estado = estado;
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

    public List<Zona> getZonas() {return zonas;}

    public void setZonas(List<Zona> zonas) {this.zonas = zonas;}

    public String getDestinosTexto(){return destinosTexto;}

    public void setDestinosTexto(String destinosTexto) {
        this.destinosTexto = destinosTexto;
    }

    public List<Camioneta> getCamionetas() { return camionetas;}

    public void setCamionetas(List<Camioneta> camionetas) {this.camionetas = camionetas;}

    public List<Maquina> getMaquinas() {return maquinas;}

    public void setMaquinas(List<Maquina> maquinas) {this.maquinas = maquinas;}

    public List<Empleado> getEmpleados() {return empleados;}

    public void setEmpleados(List<Empleado> empleados) {this.empleados = empleados;}

    public List<Detalle> getDetalles() {return detalles;}

    public void setDetalles(List<Detalle> detalles) {this.detalles = detalles;}

    public List<Registro> getRegistros() {return registros; }

    public void setRegistros(List<Registro> registros) {this.registros = registros;}

    public void setCamionetasTexto(String camionetas) { this.camionetasTexto = camionetas; }

    public void setEmpleadosTexto(String empleados) { this.empleadosTexto = empleados; }

    public void setMaquinasTexto(String maquinas) { this.maquinasTexto = maquinas; }

    public void setProductosTexto(String productos) { this.productosTexto = productos; }

    public void setRegistrosTexto(String registros) { this.registrosTexto = registros; }

    public String getCamionetasTexto() { return camionetasTexto;}

    public String getEmpleadosTexto() { return empleadosTexto;}

    public String getMaquinasTexto() { return maquinasTexto;}

    public String getProductosTexto() { return productosTexto;}

    public String getRegistrosTexto() {return registrosTexto; }
}


