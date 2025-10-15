package proyect.chagas_dengue.modelos;

import java.util.Objects;

public class UsanID {
    private int VCod;
    private int CCod;

    public UsanID() {}

    public UsanID(int VCod, int CCod) {
        this.VCod = VCod;
        this.CCod = CCod;
    }

    public int getVCod() {
        return VCod;
    }

    public void setVCod(int VCod) {
        this.VCod = VCod;
    }

    public int getCCod() {
        return CCod;
    }

    public void setCCod(int CCod) {
        this.CCod = CCod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsanID)) return false;
        UsanID usanID = (UsanID) o;
        return VCod == usanID.VCod && CCod == usanID.CCod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(VCod, CCod);
    }
}
