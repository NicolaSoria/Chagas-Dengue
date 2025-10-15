package proyect.chagas_dengue.modelos;

public class Usan {

    private int VCod;
    private int CCod;

    public Usan(int VCod, int CCod) {
        this.VCod = VCod;
        this.CCod = CCod;
    }

    public int getVCod() { return VCod; }

    public void setVCod(int VCod) { this.VCod = VCod; }

    public int getCCod() { return CCod; }

    public void setCCod(int CCod) { this.CCod = CCod; }
}
