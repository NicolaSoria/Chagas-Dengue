package proyect.chagas_dengue.modelos;

public class Zona {
    private int ZCod;
    private String nombre;
    private String direccion;
    private String localidad;
    private String departamento;

    public Zona(int ZCod,String nombre,String direccion,String localidad,String departamento){
        this.ZCod = ZCod;
        this.nombre = nombre;
        this.direccion = direccion;
        this.localidad = localidad;
        this.departamento = departamento;
    }

    public Zona(String nombre,String direccion,String localidad,String departamento){
        this.nombre = nombre;
        this.direccion = direccion;
        this.localidad = localidad;
        this.departamento = departamento;
    }

    public void setZCod(int ZCod){this.ZCod = ZCod;}
    public void setNombre(String nombre){this.nombre = nombre;}
    public void setDireccion(String direccion){this.direccion = direccion;}
    public void setLocalidad(String localidad){this.localidad = localidad;}
    public void setDepartamento(String departamento){this.departamento = departamento;}

    public int getZCod(){return ZCod;}
    public String getNombre(){return nombre;}
    public String getDireccion(){return direccion;}
    public String getLocalidad(){return localidad;}
    public String getDepartamento(){return departamento;}

}

