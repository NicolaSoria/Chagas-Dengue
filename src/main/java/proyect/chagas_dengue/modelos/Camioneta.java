package proyect.chagas_dengue.modelos;

public class Camioneta{


private String patente;
private String estado;
private int CCod;

public Camioneta(String patente, String estado, int CCod) {
    this.patente = patente;
    this.estado = estado;
    this.CCod = CCod;
}

public String getPatente() { return patente; }
public String getEstado() { return estado; }

    public int getCCod() {
        return CCod;
    }

    @Override
    public String toString() {
        return "Patente: " + patente + " | Estado: " + estado;
    }

}



