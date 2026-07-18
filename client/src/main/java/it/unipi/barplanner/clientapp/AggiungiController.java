package it.unipi.barplanner.clientapp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AggiungiController {

    @FXML
    private TextField txtNome;
    @FXML
    private ComboBox<String> comboCategoria;
    @FXML
    private TextField txtPrezzo;
    @FXML
    private TextArea txtIngredienti;
    @FXML
    private Button btnSalva;

    private MenuItem prodottoCreato = null;
    private static String tipoTurno;
    private final Service service = new Service();
    private static final Logger logger = LogManager.getLogger(AggiungiController.class);

    @FXML
    public void initialize() {
        comboCategoria.setItems(FXCollections.observableArrayList("Hot Drink", "Cold Drink", "Cocktail", "Brunch", "Breakfast", "Others"));
        comboCategoria.getSelectionModel().selectFirst();
    }

    @FXML
    void onSalvaClick(ActionEvent event) {
        if (txtNome.getText().isEmpty() || txtPrezzo.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText(null);
            alert.setContentText("Add name and price before continuing");
            alert.showAndWait();
            return;
        }

        try {
            String nome = txtNome.getText();
            String cat = comboCategoria.getValue();
            double prezzo = Double.parseDouble(txtPrezzo.getText().replace(",", "."));

            String textIngr = txtIngredienti.getText();
            Set<String> ingredienti = MenuUtils.parseIngredienti(textIngr);

            MenuItem prodotto = new MenuItem(null, null, nome, cat, null, tipoTurno, prezzo, ingredienti);
            logger.info("Aggiunta di '" + prodotto.getNome() + "' al db in corso");
            Task<MenuItem> task = new Task<>() {
                @Override
                protected MenuItem call() throws Exception {
                    return service.aggiornaItem(prodotto);
                }
            };

            task.setOnSucceeded(e -> {
                logger.info("Caricamento dell'oggetto sul db completato");
                prodottoCreato=prodotto;
                chiudiFinestra();
            });

            task.setOnFailed(e -> {
                logger.error("Errore: " + task.getException().getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Server error");
                alert.setHeaderText(null);
                alert.setContentText("Unable to add new item: " + task.getException().getMessage());
                alert.showAndWait();
                prodottoCreato=null;
            });
            new Thread(task).start();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Price error");
            alert.setHeaderText(null);
            alert.setContentText("Price needs to be a valid number (like 10.50)");
            alert.showAndWait();
        }
    }

    @FXML
    void onAnnullaClick(ActionEvent event) {
        this.prodottoCreato = null;
        chiudiFinestra();
    }

    private void chiudiFinestra() {
        Stage stage = (Stage) btnSalva.getScene().getWindow();
        stage.close();
    }

    public static void setTipoTurno(String tipoTurno) {
        AggiungiController.tipoTurno = tipoTurno;
    }

    public MenuItem getProdottoCreato() {
        return prodottoCreato;
    }
}
