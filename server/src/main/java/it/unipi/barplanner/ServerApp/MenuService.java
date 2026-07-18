package it.unipi.barplanner.ServerApp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuRepository;

    private static final Logger logger = LogManager.getLogger(MenuService.class);

    @Value("${api.url.drink}")
    private String BASE_URL_DRINK;

    @Value("${api.url.food}")
    private String BASE_URL_FOOD;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    public void pulisciDB() {
        menuRepository.deleteAll();
        logger.info("database svuotato");
    }

    public List<MenuItem> getAllItems() {
        return menuRepository.findAll();
    }

    public List<MenuItem> getTurnItems(String tipoTurno) {
        return menuRepository.findByTipoTurno(tipoTurno);
    }

    public void removeItem(@PathVariable Integer id) {
        menuRepository.deleteById(id);
    }

    public MenuItem updateItem(MenuItem item) {
        return menuRepository.save(item); //save() fa update se l'ID esiste già
    }

    public List<MenuItem> findByName(String q) {
        return menuRepository.findByNomeContainingIgnoreCase(q);
    }

    public List<MenuItem> findByIngredient(String ing) {
        return menuRepository.findByIngredienti(ing);
    }

    public List<MenuItem> findByIngredientAndTurn(String ing, String turno) {
        return menuRepository.findByIngredientiContainingIgnoreCaseAndTipoTurno(ing, turno);
    }

    public long contaElementi() {
        return menuRepository.count();
    }

    public void scaricaDatiCocktail() {

        Gson gson = new Gson();
        String characters = "abcdefghijklmnopqrstuvwxyz";
        int MAX_PER_LETTERA = 3;
        for (char c : characters.toCharArray()) {
            HttpURLConnection con = null;
            try {
                //Chiamata API

                URL url = new URL(BASE_URL_DRINK + c);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                int status = con.getResponseCode();
                if (status != 200) {
                    continue; 
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                CocktailDTO.Response response = new CocktailDTO.Response();
                List<CocktailDTO> list = new ArrayList<>(); //lista ausiliaria
                JsonElement jsonElement = gson.fromJson(content.toString(), JsonElement.class);
                JsonObject rootObject = jsonElement.getAsJsonObject();

                if (rootObject.has("drinks") && !rootObject.get("drinks").isJsonNull()) {

                    JsonArray drinksArray = rootObject.get("drinks").getAsJsonArray();

                    for (int i = 0; i < drinksArray.size() && i < MAX_PER_LETTERA; i++) {

                        JsonObject drinkJson = drinksArray.get(i).getAsJsonObject();
                        CocktailDTO dto = new CocktailDTO();

                        dto.setId(getStringOrNull(drinkJson, "idDrink"));
                        dto.setNome(getStringOrNull(drinkJson, "strDrink"));
                        dto.setCategoria(getStringOrNull(drinkJson, "strCategory"));
                        dto.setUrlImmagine(getStringOrNull(drinkJson, "strDrinkThumb"));
                        dto.setStrAlcoholic(getStringOrNull(drinkJson, "strAlcoholic"));
                        dto.setIng1(getStringOrNull(drinkJson, "strIngredient1"));
                        dto.setIng2(getStringOrNull(drinkJson, "strIngredient2"));
                        dto.setIng3(getStringOrNull(drinkJson, "strIngredient3"));
                        dto.setIng4(getStringOrNull(drinkJson, "strIngredient4"));
                        dto.setIng5(getStringOrNull(drinkJson, "strIngredient5"));
                        boolean esisteGia = menuRepository.existsByIdEsterno(dto.getId());
                        if (!esisteGia) {
                            list.add(dto);
                        }
                    }
                }
                response.setElements(list);

                if (response != null && response.getElements() != null) {
                    for (CocktailDTO dto : response.getElements()) {
                        if ("Alcoholic".equalsIgnoreCase(dto.getStrAlcoholic())) {
                            MenuItem entity = mapToEntity(dto, CocktailDTO.getTipoTurno());
                            menuRepository.save(entity);
                            logger.info("Salvato: " + dto.getNome());
                        }
                    }
                }
            } catch (JsonSyntaxException | IOException e) {
                logger.error("Errore durante l'importazione dei drink di iniziale " + c + ": " + e.getMessage());
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        }
        logger.info("Importazione dei cocktail completata");
    }

    public void scaricaDatiCibo() {
        Gson gson = new Gson();

        try {
            URL url = new URL(BASE_URL_FOOD + "?apiKey=" + apiKey + "&type=breakfast" + "&number=50" + "&addRecipeInformation=true&fillIngredients=true");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            FoodDTO.Response response = new FoodDTO.Response();
            List<FoodDTO> list = new ArrayList<>(); //lista ausiliaria
            JsonElement jsonElement = gson.fromJson(content.toString(), JsonElement.class);
            JsonObject rootObject = jsonElement.getAsJsonObject();
            if (rootObject.has("results") && !rootObject.get("results").isJsonNull()) {

                JsonArray foodsArray = rootObject.get("results").getAsJsonArray();

                for (int i = 0; i < foodsArray.size(); i++) {

                    JsonObject foodJson = foodsArray.get(i).getAsJsonObject();
                    FoodDTO dto = new FoodDTO();

                    dto.setId(getStringOrNull(foodJson, "id"));
                    dto.setNome(getStringOrNull(foodJson, "title"));
                    dto.setUrlImmagine(getStringOrNull(foodJson, "image"));
                    if (foodJson.has("dishTypes") && foodJson.get("dishTypes").isJsonArray()) {
                        JsonArray types = foodJson.getAsJsonArray("dishTypes");
                        if (types.size() > 0) {
                            dto.setCategoria(types.get(0).getAsString());
                        }
                    }

                    if (foodJson.has("extendedIngredients") && foodJson.get("extendedIngredients").isJsonArray()) {
                        JsonArray ingredientsArray = foodJson.getAsJsonArray("extendedIngredients");
                        if (ingredientsArray.size() > 0) {
                            dto.setIng1(ingredientsArray.get(0).getAsJsonObject().get("name").getAsString());
                        }
                        if (ingredientsArray.size() > 1) {
                            dto.setIng2(ingredientsArray.get(1).getAsJsonObject().get("name").getAsString());
                        }
                        if (ingredientsArray.size() > 2) {
                            dto.setIng3(ingredientsArray.get(2).getAsJsonObject().get("name").getAsString());
                        }
                        if (ingredientsArray.size() > 3) {
                            dto.setIng4(ingredientsArray.get(3).getAsJsonObject().get("name").getAsString());
                        }
                        if (ingredientsArray.size() > 4) {
                            dto.setIng5(ingredientsArray.get(4).getAsJsonObject().get("name").getAsString());
                        }
                    }
                    boolean esisteGia = menuRepository.existsByIdEsterno(dto.getId());
                    if (!esisteGia) {
                        list.add(dto);
                    }
                }

            }
            response.setElements(list);
            if (response.getElements() != null) {
                for (FoodDTO dto : response.getElements()) {
                    MenuItem entity = mapToEntity(dto, FoodDTO.getTipoTurno());
                    menuRepository.save(entity);
                    logger.info("Salvato: " + dto.getNome());
                }
            }
        } catch (JsonSyntaxException | IOException e) {
            logger.error("Errore durante l'importazione dei cibi: " + e.getMessage());
        }

        logger.info("Importazione dei cibi completata");
    }

    private MenuItem mapToEntity(DTO dto, String tipoTurno) {
        MenuItem item = new MenuItem();

        item.setNome(dto.getNome());
        item.setIdEsterno(dto.getId());
        item.setUrlImmagine(dto.getUrlImmagine());
        item.setCategoria(dto.getCategoria());
        item.setTipoTurno(tipoTurno);
        item.setPrezzo(dto.getPrezzoDefault());
        item.setIngredienti(new HashSet<>(dto.getIngredientiPuliti()));

        return item;
    }

    private String getStringOrNull(JsonObject json, String key) { //per evitare null pointer exception
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }
}
