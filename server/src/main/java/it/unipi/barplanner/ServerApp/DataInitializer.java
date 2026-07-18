
package it.unipi.barplanner.ServerApp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LogManager.getLogger(DataInitializer.class);
    @Autowired
    private MenuItemRepository menuRepository;
    @Autowired
    private MenuService menuService;
    
    @Override
    public void run(String[] args) throws Exception {
        logger.info("--- CONTROLLO STATO DATABASE ---");
        long numeroPiatti = menuRepository.count();
        if (numeroPiatti == 0) {
            logger.info("Database vuoto. Avvio inizializzazione automatica...");
            menuService.scaricaDatiCocktail();
            menuService.scaricaDatiCibo(); 
            logger.info("--- INIZIALIZZAZIONE COMPLETATA ---");
        } else {
            logger.info("Database popolato (" + numeroPiatti + " elementi trovati).");
        }
    }
}
