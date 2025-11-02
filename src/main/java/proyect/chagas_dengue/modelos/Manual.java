package proyect.chagas_dengue.modelos;

public class Manual extends Maquina{
    public Manual() {
        super();
    }

    /*public enum cargaltsM{
        Ocho("8"),
        Quince("15");

        private final String valor;
        cargaltsM(String valor){
            this.valor = valor;
        }
        public String getValor(){
            return valor;
        }
    }*/

    public enum cargaltsM{
        _8,_15
    }

    private cargaltsM cargalts;

    public Manual(String marca,String modelo, cargaltsM cargalts){
        super(marca,modelo,TipoM.Manual);
        this.cargalts = cargalts;
    }

    public Manual(int MCod, String marca,String modelo, cargaltsM cargalts){
        super(MCod,marca,modelo,TipoM.Manual);
        this.cargalts = cargalts;
    }

    public cargaltsM getCargalts() {return cargalts;}

    public void setCargalts(cargaltsM cargalts) {this.cargalts = cargalts;}
}
