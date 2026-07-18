
package it.unipi.barplanner.clientapp;

import java.util.HashSet;
import java.util.Set;

public class MenuUtils {

    public static Set<String> parseIngredienti(String input) {
        Set<String> ingredienti = new HashSet<>();
        if (input == null || input.trim().isEmpty()) {
            return ingredienti;
        }
        String[] array = input.split(",");
        for (String s : array) {
            String pulita = s.trim();
            if (!pulita.isEmpty()) {
                ingredienti.add(pulita);
            }
        }
        return ingredienti;
    }

    public static boolean matchRicerca(MenuItem item, String testoRicerca) {
        if (testoRicerca == null || testoRicerca.trim().isEmpty()) {
            return true;
        }

        if (item == null) {
            return false;
        }

        String lowerFilter = testoRicerca.toLowerCase();

        if (item.getNome() != null && item.getNome().toLowerCase().contains(lowerFilter)) {
            return true;
        }

        if (item.getIngredienti() != null) {
            for (String ingr : item.getIngredienti()) {
                if (ingr.toLowerCase().contains(lowerFilter)) {
                    return true;
                }
            }
        }

        return false;
    }
}
