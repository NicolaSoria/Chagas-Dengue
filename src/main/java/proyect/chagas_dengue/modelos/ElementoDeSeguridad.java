package proyect.chagas_dengue.modelos;

public class ElementoDeSeguridad {
    private int ESCod;
    private int TESCod;
    private String descripcion;
//    private String estado;

    public ElementoDeSeguridad() {
    }

    public ElementoDeSeguridad(int ESCod, int TESCod, String descripcion) {
        this.ESCod = ESCod;
        this.TESCod = TESCod;
        this.descripcion = descripcion;
//        this.estado = estado;
    }

    public int getESCod() { return ESCod; }
    public int getTESCod() { return TESCod; }
    public String getDescripcion() { return descripcion; }
//    public String getEstado() { return estado; }

    @Override
    public String toString() {
        return descripcion ;
    }

    public void setESCod(int esCod) {
    }

    public void setDescripcion(String descripcion) {
    }

    public void setEstado(String estado) {
    }
}









//package proyect.chagas_dengue.modelos;
//
//public class ElementoDeSeguridad {
//    private String tipo;
//    private String descripcion;
//
//    public ElementoDeSeguridad(String tipo, String descripcion) {
//        this.tipo = tipo;
//        this.descripcion = descripcion;
//    }
//
//    public String getTipo() { return tipo; }
//    public String getDescripcion() { return descripcion; }
//}
