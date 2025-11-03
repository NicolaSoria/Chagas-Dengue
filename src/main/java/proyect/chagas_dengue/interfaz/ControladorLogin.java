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
        PasswordHash contra = new PasswordHash();

        // Validar campos vacíos
        if (usuario.isEmpty() || contraseña.isEmpty()) {
            lblMensaje.setText("Por favor, complete todos los campos");
            return;
        }

        // Autenticacion
        if (autenticarUsuario(usuario, contraseña)) {
            try {
                // Login exitoso
                Main.mostrarPantallaPrincipal();
            } catch (IOException e) {
                mostrarError("Error al cargar la aplicación: " + e.getMessage());
            }
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos");
            // Limpiar campo de contraseña
            txtContraseña.clear();
        }
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
}