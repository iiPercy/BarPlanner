
package it.unipi.barplanner.ServerApp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
@Entity
@Table(name="menu_item", schema="barplanner")
public class MenuItem implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    
    @Column(name="idesterno")
    private String idEsterno;
    
    @Column(name="nome")
    @NotBlank
    private String nome;
    
    @Column(name="categoria")
    private String categoria;
    
    @Column(name="urlimmagine")
    private String urlImmagine;
    
    @Column(name="tipoturno")
    @Pattern(regexp = "GIORNO|SERA")
    private String tipoTurno;
    
    @Column(name="prezzo")
    @Min(value = 0)
    private double prezzo;
    
    @ElementCollection //Tabella ingredienti che servirà a filtrare i menuitem in base ad essi
    @CollectionTable(
        name = "ingredienti",           
        joinColumns = @JoinColumn(name = "menu_item_id") // Chiave esterna
    )
    @Column(name = "nome_ingrediente")        
    
    private Set<String> ingredienti = new HashSet<>(); //meglio di ArrayList perché evita i duplicati (eventuali errori mandati dell'API)
    
}
