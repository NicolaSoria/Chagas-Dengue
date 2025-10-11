package proyect.chagas_dengue.modelos;

public class Camioneta{

private String patente;
private String estado;

public Camioneta(String patente, String estado) {
    this.patente = patente;
    this.estado = estado;
}

public String getPatente() { return patente; }
public String getEstado() { return estado; }

}
