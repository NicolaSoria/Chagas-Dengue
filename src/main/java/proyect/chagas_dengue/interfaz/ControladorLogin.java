package proyect.chagas_dengue.interfaz;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import proyect.chagas_dengue.Main;
import proyect.chagas_dengue.dao.UsuarioDAO;
import proyect.chagas_dengue.modelos.PasswordHash;
import proyect.chagas_dengue.modelos.Usuario;

import java.io.IOException;

public class ControladorLogin {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContraseña;
    @FXML private Label lblMensaje;

    @FXML
    private void onIngresar() {
        String usuario = txtUsuario.getText().trim();
        String contraseña = txtContraseña.getText().trim();

        if (usuario.isEmpty() || contraseña.isEmpty()) {
            lblMensaje.setText("Por favor, complete todos los campos");
            return;
        }

        if (autenticarUsuario(usuario, contraseña)) {
            try {
                Main.mostrarPantallaPrincipal();
            } catch (IOException e) {
                mostrarError("Error al cargar la aplicación: " + e.getMessage());
            }
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos");
            txtContraseña.clear();
        }
    }

    @FXML
    private void onRegistrar() {
        String usuario = txtUsuario.getText().trim();
        String contraseña = txtContraseña.getText().trim();

        if (usuario.isEmpty() || contraseña.isEmpty()) {
            lblMensaje.setText("Por favor, complete todos los campos");
            return;
        }

        if (registrarUsuario(usuario, contraseña)) {
            lblMensaje.setStyle("-fx-text-fill: #28a745;");
            lblMensaje.setText("Usuario registrado correctamente");
            txtUsuario.clear();
            txtContraseña.clear();
        } else {
            lblMensaje.setStyle("-fx-text-fill: #dc3545;");
            lblMensaje.setText("El usuario ya existe o no se pudo registrar");
        }
    }

    private boolean registrarUsuario(String nombre, String password) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PasswordHash passwordHash = new PasswordHash();

        // Verificar si ya existe el usuario
        if (usuarioDAO.Autentucacion(nombre) != null) {
            System.out.println("El usuario ya existe");
            return false;
        }

        // Crear nuevo usuario
        if(validarContraseña(password)) {
            Usuario nuevo = new Usuario();
            nuevo.setNombre(nombre);
            nuevo.setPassword(passwordHash.hashPassword(password));
            System.out.println("Contra: " + nuevo.getPassword());
            // Insertar en la BD
            System.out.println("Hoalaa");
            return usuarioDAO.insertarUsuario(nuevo);
        }
        return false;
    }

    /**
     * Método para autenticar usuario
     *
     */
    private boolean autenticarUsuario(String nombre, String passwordIngresada) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PasswordHash passwordHash = new PasswordHash();

        // Buscar usuario por nombre
        Usuario usuario = usuarioDAO.Autentucacion(nombre);

        // Si no existe el usuario
        if (usuario == null) {
            System.out.println("Usuario no encontrado en la BD");
            return false;
        }

        // Verificar la contraseña con hashing
        boolean esValido = passwordHash.verifyPassword(passwordIngresada, usuario.getPassword());

        if (!esValido) {
            System.out.println("Contraseña incorrecta para usuario: " + nombre);
        }

        return esValido;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public  boolean validarContraseña(String contraseña) {
        if (contraseña == null || contraseña.length() < 8) {
            System.out.println("asdasfa");
            return false;
        }

        for (char c : contraseña.toCharArray()) {
            int ascii = (int) c;
            for (int i = 65; i <= 90; i++) {
                if (i == ascii) {
                    return true;
                }
            }
        }
        String simbolosEspeciales = "!@#$%^&*()_+-=[]{};':\"|,.<>/?123456789";
        for (char c : contraseña.toCharArray()) {
            if (simbolosEspeciales.indexOf(c) != -1) {
                return true;
            }
        }

        return false;
    }
}