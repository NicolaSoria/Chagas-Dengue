package proyect.chagas_dengue.modelos;

public class Maquina {
    private String marca;
    private String modelo;
    private String tipo; // "Manual", "ULV", "Pulverizadora"

    public Maquina(String marca, String modelo, String tipo) {
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
    }

    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getTipo() { return tipo; }
}

