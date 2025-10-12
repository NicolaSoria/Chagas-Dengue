package proyect.chagas_dengue.modelos;

public class Camioneta{

private String patente;
private String estado;
private String codigo;

public Camioneta(String patente, String estado, String codigo) {
    this.patente = patente;
    this.estado = estado;
    this.codigo = codigo;
}

public String getPatente() { return patente; }
public String getEstado() { return estado; }

    public String getCodigo() {
        return codigo;
    }
}
