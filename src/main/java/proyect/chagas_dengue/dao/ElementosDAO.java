package proyect.chagas_dengue.dao;

import java.sql.Connection;
import java.util.List;

public class ElementosDAO implements interfaceDAO{

    private Connection conexion;



    @Override
    public void insertar(Object objeto) {

    }

    @Override
    public Object consultar(Object o) {
        return null;
    }

    @Override
    public void actualizar(Object objeto) {

    }

    @Override
    public void eliminar(int id) {

    }

    @Override
    public List listarTodos() {
        return List.of();
    }
}
