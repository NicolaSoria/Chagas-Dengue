package proyect.chagas_dengue.modelos;

import java.util.Objects;

public class DestinoID {
    private int DVCod;
    private int DZCod;

    public DestinoID(int DVCod,int DZCod){
        this.DVCod = DVCod;
        this.DZCod = DZCod;
    }

    public int getDVCod() { return DVCod; }

    public int getDZCod(){ return DZCod; }

    public void setDZCod(int DZCod) { this.DZCod = DZCod; }

    public void setDVCod(int DVCod) { this.DVCod = DVCod; }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof DestinoID)) return false;
        DestinoID that = (DestinoID) o;
        return DVCod == that.DVCod && DZCod == that.DZCod;
    }

    public int hashCode(){
        return Objects.hash(DVCod,DZCod);
    }
}
