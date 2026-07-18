package it.unipi.barplanner.clientapp;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItem implements Serializable {

    private Integer id;
    private String idEsterno;
    private String nome;
    private String categoria;
    private String urlImmagine;
    private String tipoTurno;
    private Double prezzo;
    private Set<String> ingredienti;

}
