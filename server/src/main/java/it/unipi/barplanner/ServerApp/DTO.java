
package it.unipi.barplanner.ServerApp;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DTO {
    private String id;

    private String nome;

    private String categoria;

    private String urlImmagine;

    String ing1;
    String ing2;
    String ing3;
    String ing4;
    String ing5;
    
    @Data
    public static class Response {
        private List<DTO> elements;
    }

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
    
    public double getPrezzoDefault() {
        return 10;
    }
}
