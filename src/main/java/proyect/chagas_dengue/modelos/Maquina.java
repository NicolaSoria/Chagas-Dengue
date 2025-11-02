package proyect.chagas_dengue.modelos;

public abstract class Maquina {


    public Maquina() {

    }

    public enum TipoM{
        Manual,ULV, Pulverizadora
    }
    protected int MCod;
    protected String marca;
    protected String modelo;
    protected TipoM tipo; // "Manual", "ULV", "Pulverizadora"

    public Maquina(String marca,String modelo, TipoM tipo){
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
    }

    public Maquina(int MCod,String marca,String modelo, TipoM tipo){
        this.MCod = MCod;
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
    }

    public int getMCod() {return MCod;}

    public void setMCod(int MCod) {
        this.MCod = MCod;
    }

    public String getMarca() {return marca;}

    public void setMarca(String marca) {this.marca = marca;}

    public String getModelo() {return modelo;}

    public void setModelo(String modelo) {this.modelo = modelo;}

    public TipoM getTipo() {return tipo;}

    public void setTipo(TipoM tipo) {this.tipo = tipo;}

    @Override
    public String toString() {
        return "Máquina: " + marca + " | Modelo: " + modelo + " | Tipo: " + tipo;
    }


}

