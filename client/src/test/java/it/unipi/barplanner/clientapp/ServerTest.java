
package it.unipi.barplanner.clientapp;

import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ServerTest {
    
    public ServerTest() {
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

    private final Service service = new Service();
    
    @Test
    public void isServerUp() {
        System.out.println("Test accensione e connessione server");
        try {
        String response = service.isUp();
        if (response == null) {
            fail("Il server non è acceso");
        } 
        } catch (Exception e) {
            fail("Errore server: " + e.getMessage());
        }
    }
    
}
