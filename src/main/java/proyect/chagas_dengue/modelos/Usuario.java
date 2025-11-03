package proyect.chagas_dengue.modelos;

public class Usuario {
    private String nombre;
    private String password;

    public Usuario() {}

    public Usuario(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }
    //Getters
    public String getNombre() {return nombre;}
    public String getPassword() {return password;}

    //Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
