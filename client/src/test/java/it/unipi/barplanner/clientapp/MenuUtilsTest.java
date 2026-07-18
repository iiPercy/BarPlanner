
package it.unipi.barplanner.clientapp;


import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class MenuUtilsTest {
    
    public MenuUtilsTest() {
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
    
    private MenuItem creaItemDummy(String nome, String... ingredienti) {
        Set<String> setIngr = new HashSet<>();
        for (String s : ingredienti) {
            setIngr.add(s);
        }
        return new MenuItem(1, null, nome, "Cat", null, "SERA", 10.0, setIngr);
    }
    
    @Test
    public void testRicerca_PerNome() {
        System.out.println("Test Ricerca: Nome Esatto");
        MenuItem item = creaItemDummy("Mojito Classico", "Rum", "Menta");
        boolean trovato = MenuUtils.matchRicerca(item, "Mojito");
        if (!trovato) {
            fail("test ricerca per nome fallito'");
        }
    }
    
    @Test
    void testRicerca_CaseInsensitive() {
        System.out.println("Test Ricerca: Maiuscole/Minuscole");
        MenuItem item = creaItemDummy("Spritz", "Aperol", "Prosecco");
        if (!MenuUtils.matchRicerca(item, "spritz")) {
            fail("Test CaseInsensitive fallito");
        }
        if (!MenuUtils.matchRicerca(item, "SPRiTZ")) {
            fail("Test CaseInsensitive fallito");
        }
    }
    
    @Test
    void testRicerca_PerIngrediente() {
        System.out.println("Test Ricerca: Ingrediente");
        MenuItem item = creaItemDummy("Negroni", "Gin", "Vermouth", "Campari");
        if (!MenuUtils.matchRicerca(item, "Campari")) {
            fail("Test ricerca per ingrediente fallito");
        }
        if (!MenuUtils.matchRicerca(item, "Verm")) {
            fail("Test ricerca per ingrediente fallito");
        }
    }
    
    @Test
    void testRicerca_InputVuoto() {
        System.out.println("Test Ricerca: Input Vuoto");
        MenuItem item = creaItemDummy("Caffè");
        if (!MenuUtils.matchRicerca(item, "")) {
            fail("Test Ricerca con input vuoto fallito");
        }
        
        if (!MenuUtils.matchRicerca(item, null)) {
            fail("Test Ricerca con input vuoto fallito");
        }
    }
}
