package it.unipi.barplanner.ServerApp;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/BarPlanner")
public class MainController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/")
    public String home() {
        return "Server BarPlanner è attivo e funzionante!";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    List<MenuItem> getAllItems() {
        return menuService.getAllItems();
    }

    @PostMapping(path = "/inizializza")
    public @ResponseBody
    void initialize() {
        menuService.pulisciDB();
        menuService.scaricaDatiCocktail();
        menuService.scaricaDatiCibo();
    }

    @GetMapping(path = "/cocktail")
    public @ResponseBody
    List<MenuItem> getCocktails() {
        return menuService.getTurnItems(CocktailDTO.getTipoTurno());
    }

    @GetMapping(path = "/food")
    public @ResponseBody
    List<MenuItem> getFoods() {
        return menuService.getTurnItems(FoodDTO.getTipoTurno());
    }

    @GetMapping(path = "/turnItems")
    public @ResponseBody
    List<MenuItem> getTurnItems(@RequestParam String turn) {
        return menuService.getTurnItems(turn);
    }

    @DeleteMapping("/rimuovi/{id}")
    public @ResponseBody
    void removeItem(@PathVariable Integer id) {
        menuService.removeItem(id);
    }

    @PostMapping("/aggiorna")
    public @ResponseBody
    MenuItem updateItem(@Valid @RequestBody MenuItem item) {
        return menuService.updateItem(item);
    }

    @GetMapping("/cerca/nome")
    public @ResponseBody
    List<MenuItem> findByName(@RequestParam String q) {
        return menuService.findByName(q);
    }

    @GetMapping("/cerca/ingrediente")
    public @ResponseBody
    List<MenuItem> findByIngredient(@RequestParam String ing) {
        return menuService.findByIngredient(ing);
    }

    @GetMapping("/cerca/ingrediente/turno")
    public @ResponseBody
    List<MenuItem> findTurnItemByIngredient(
            @RequestParam String ing, @RequestParam String turn) {
        return menuService.findByIngredientAndTurn(ing, turn);
    }

    @GetMapping("/count")
    @ResponseBody
    public long getCount() {
        return menuService.contaElementi();
    }
}
