
package it.unipi.barplanner.ServerApp;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;


public class FoodDTO extends DTO {
    private static final String tipoTurno="GIORNO";
    
    public static String getTipoTurno() {
        return tipoTurno;
    }
    
    @Data
    public static class Response {
        private List<FoodDTO> elements;
    }

    @Override
    public List<String> getIngredientiPuliti() {
        List<String> lista = new ArrayList<>();
        add(lista, ing1);
        add(lista, ing2);
        add(lista, ing3);
        add(lista, ing4);
        add(lista, ing5);
        return lista;
    }

    private void add(List<String> lista, String ingrediente) {
        if (ingrediente != null && !ingrediente.trim().isEmpty()) {
            lista.add(ingrediente.trim());
        }
    }
    
    @Override
    public double getPrezzoDefault() {
        return 2;
    }
}
