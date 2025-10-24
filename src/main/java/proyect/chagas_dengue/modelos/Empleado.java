package proyect.chagas_dengue.modelos;

public class Empleado {
    private int ECod;          // Clave primaria
    private String nombre;
    private int dni;
    private String direccion;
    private String telefono;

    // 🔹 Constructor vacío (requerido por JavaFX y algunos frameworks)
    public Empleado() {}

    // 🔹 Constructor con todos los campos (sin ECod, cuando vas a insertar uno nuevo)
    public Empleado(String nombre, int dni, String direccion, String telefono) {
        this.nombre = nombre;
        this.dni = dni;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // 🔹 Constructor completo (cuando ya conocés el ECod, por ejemplo al consultar)
    public Empleado(int ECod, String nombre, int dni, String direccion, String telefono) {
        this.ECod = ECod;
        this.nombre = nombre;
        this.dni = dni;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // 🔹 Getters y Setters
    public int getECod() {
        return ECod;
    }

    public void setECod(int ECod) {
        this.ECod = ECod;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "ECod=" + ECod +
                ", nombre='" + nombre + '\'' +
                ", dni=" + dni +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}

