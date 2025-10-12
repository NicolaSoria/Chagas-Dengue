package proyect.chagas_dengue.dao;

import java.util.List;

public interface interfaceDAO<T,ID> {
    void insertar(T objeto);
    T consultar(ID id);
    void actualizar(T objeto);
    void eliminar(int id);
    List<T> listarTodos();
}
