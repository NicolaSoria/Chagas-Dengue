package proyect.chagas_dengue.modelos;

public class Destino {
    private int DVCod;
    private int DZCod;

    public Destino(){

    }

    public Destino(int DVCod,int DZCod){
        this.DVCod = DVCod;
        this.DZCod = DZCod;
    }

    public int getDVCod(){
        return DVCod;
    }

    public int getDZCod() {
        return DZCod;
    }

    public void setDVCod(int DVCod) {
        this.DVCod = DVCod;
    }

    public void setDZCod(int DZCod) {
        this.DZCod = DZCod;
    }
}
