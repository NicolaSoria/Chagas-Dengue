package proyect.chagas_dengue.modelos;

public class Camioneta{

private String patente;
private String estado;
private int codigo;

public Camioneta(String patente, String estado, int codigo) {
    this.patente = patente;
    this.estado = estado;
    this.codigo = codigo;
}

public String getPatente() { return patente; }
public String getEstado() { return estado; }

    public int getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return "Patente: " + patente + " | Estado: " + estado;
    }


}



