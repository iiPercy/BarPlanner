package it.unipi.barplanner.clientapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Service {

    private static final Logger logger = LogManager.getLogger(Service.class);
    private static final String BASE_URL = "http://localhost:8080/BarPlanner";
    private final Gson gson = new Gson();

    public List<MenuItem> getMenu(String turno) throws Exception {
        URL url = new URL(BASE_URL + "/turnItems?turn=" + turno);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Errore nel recupero items: HTTP " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        List<MenuItem> items = new ArrayList<>();
        JsonElement jsonElement = gson.fromJson(content.toString(), JsonElement.class);

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (JsonElement element : jsonArray) {
                MenuItem item = mapJsonToItem(element.getAsJsonObject());
                items.add(item);
            }
        }
        return items;
    }

    public void inizializza() throws Exception {
        URL url = new URL(BASE_URL + "/inizializza");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        int responseCode = con.getResponseCode();
        con.disconnect();

        if (responseCode != 200) {
            throw new RuntimeException("Errore inizializzazione: " + responseCode);
        }
    }

    public MenuItem aggiornaItem(MenuItem item) throws Exception {
        URL url = new URL(BASE_URL + "/aggiorna");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        String jsonInputString = gson.toJson(item);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        if (responseCode >= 400) { //Spring ritorna 400 Bad Request se @Valid fallisce
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                throw new RuntimeException("Errore dal server (" + responseCode + "): " + response.toString());
            }
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return gson.fromJson(response.toString(), MenuItem.class);
        }
    }

    public void deleteItem(Integer id) throws Exception {
        URL url = new URL(BASE_URL + "/rimuovi/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

        int code = con.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new RuntimeException("Errore Server HTTP: " + code);
        }
    }

    public boolean isInizializzato() throws Exception {
        URL url = new URL(BASE_URL + "/count");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() != 200) {
            throw new RuntimeException("Errore: " + con.getResponseCode());
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String response = in.readLine(); 
            long count = Long.parseLong(response);
            return count > 0; 
        }
    }
    
    public String isUp() throws Exception {
        URL url = new URL(BASE_URL + "/" );
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        
        if (con.getResponseCode() != 200) {
            throw new RuntimeException("Errore: " + con.getResponseCode());
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            return in.readLine();   
        }
    }

    private MenuItem mapJsonToItem(JsonObject obj) {
        int id = 0;
        if (obj.has("id") && !obj.get("id").isJsonNull()) {
            id = obj.get("id").getAsInt();
        }

        String nome = getSafeString(obj, "nome");
        String categoria = getSafeString(obj, "categoria");
        String urlImmagine = getSafeString(obj, "urlImmagine");
        String tipoTurno = getSafeString(obj, "tipoTurno");
        String idEsterno = getSafeString(obj, "idEsterno");

        double prezzo = 5.0;
        if (obj.has("prezzo") && !obj.get("prezzo").isJsonNull()) {
            prezzo = obj.get("prezzo").getAsDouble();
        }

        Set<String> ingredienti = new HashSet<>();
        if (obj.has("ingredienti") && obj.get("ingredienti").isJsonArray()) {
            JsonArray ingArray = obj.get("ingredienti").getAsJsonArray();
            for (JsonElement ing : ingArray) {
                if (!ing.isJsonNull()) {
                    ingredienti.add(ing.getAsString());
                }
            }
        }

        return new MenuItem(
                id,
                idEsterno,
                nome,
                categoria,
                urlImmagine,
                tipoTurno,
                prezzo,
                ingredienti
        );
    }

    private String getSafeString(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return "";
    }
}
