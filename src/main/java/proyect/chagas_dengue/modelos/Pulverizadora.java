package proyect.chagas_dengue.modelos;

public class Pulverizadora extends Maquina{
    public Pulverizadora() {

    }

    /*public enum cargaltsP{
        Trece("13"),
        Quince("15");
        private final String valor;
        cargaltsP(String valor){
            this.valor = valor;
        }
        public String getValor(){
            return valor;
        }
    }*/

    public enum cargaltsP{
        _13,_15
    }

    private cargaltsP cargalts;



    public Pulverizadora(String marca,String modelo, Pulverizadora.cargaltsP cargalts){
        super(marca,modelo,TipoM.Pulverizadora);
        this.cargalts = cargalts;
    }

    public Pulverizadora(int MCod, String marca,String modelo, Pulverizadora.cargaltsP cargalts){
        super(MCod,marca,modelo,TipoM.Pulverizadora);
        this.cargalts = cargalts;
    }

    public Pulverizadora.cargaltsP getCargalts() {return cargalts;}

    public void setCargalts(Pulverizadora.cargaltsP cargalts) {this.cargalts = cargalts;}
}
