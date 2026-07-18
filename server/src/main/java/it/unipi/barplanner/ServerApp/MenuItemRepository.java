
package it.unipi.barplanner.ServerApp;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MenuItemRepository extends JpaRepository<MenuItem, Integer>{ //JpaRepository estende CrudRepository, ma FindAll ritorna list anziché iterable, inoltre ha più metodi
    boolean existsByIdEsterno(String idEsterno);
    
    List<MenuItem> findByTipoTurno(String tipoTurno);
    
    List<MenuItem> findByNomeContainingIgnoreCase(String partialName);
    
    List<MenuItem> findByIngredienti(String ingrediente);
    
    List<MenuItem> findByIngredientiContainingIgnoreCaseAndTipoTurno(String ingrediente, String tipoTurno);
}
