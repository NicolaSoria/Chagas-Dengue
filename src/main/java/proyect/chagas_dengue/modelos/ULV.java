package proyect.chagas_dengue.modelos;

public class ULV extends Maquina{
    public ULV(int MCod, String marca, String modelo){
        super(MCod,marca,modelo,TipoM.ULV);
    }

    public ULV(String marca, String modelo){
        super(marca,modelo,TipoM.ULV);
    }
}
