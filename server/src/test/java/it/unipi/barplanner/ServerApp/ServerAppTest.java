
package it.unipi.barplanner.ServerApp;

import it.unipi.barplanner.ServerApp.MenuItem;
import it.unipi.barplanner.ServerApp.MenuItemRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class ServerAppTest {
    
    public ServerAppTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Autowired
    private MenuItemRepository menuRepository;
    
    @Test
    @Transactional //(fa il rollback automatico a fine test)
    void testSalvataggioERecupero() {
        MenuItem itemDiProva = new MenuItem();
        itemDiProva.setNome("Test Mojito Unitario");
        itemDiProva.setPrezzo(15.0);
        itemDiProva.setTipoTurno("SERA");
        itemDiProva.setCategoria("Cocktail");
        itemDiProva.setIdEsterno("TEST001");
        
        Set<String> ingredienti = new HashSet<>();
        ingredienti.add("Rum");
        ingredienti.add("Menta");
        itemDiProva.setIngredienti(ingredienti);

        try {
            menuRepository.save(itemDiProva);
        } catch (Exception e) {
            fail("Errore critico durante il test salvataggio nel DB: " + e.getMessage());
        }
        
        List<MenuItem> risultati = menuRepository.findByNomeContainingIgnoreCase("Test Mojito Unitario");
        
        if (risultati.isEmpty()) {
            fail("TEST SALVATAGGIO E QUERY FALLITO");
        }
        
        MenuItem itemRecuperato = risultati.get(0);
        if (itemRecuperato.getPrezzo() != 15.0) {
            fail("TEST FALLITO: Il prezzo salvato non corrisponde a quello recuperato");
        }
        
        if (!itemRecuperato.getIngredienti().contains("Rum")) {
            fail("TEST FALLITO: un ingrediente non è stato salvato correttamente.");
        }
    }
    
}
