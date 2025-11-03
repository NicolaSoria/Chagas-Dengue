package proyect.chagas_dengue.modelos;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHash {
    // Crea Hash
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    // Verifica si una contraseña ingresada coincide con el hash almacenado
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}

// agregar en la BD.
/*CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);*/