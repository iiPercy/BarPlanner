package it.unipi.barplanner.clientapp;

import java.io.IOException;
import java.util.Optional;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class StartController {

    @FXML
    private ToggleGroup gruppoTurno;
    @FXML
    private ToggleButton btnGiorno;
    @FXML
    private ToggleButton btnSera;
    @FXML
    private Button btnEntra;
    @FXML
    private Button btnInizializza;

    private final Service service = new Service();
    private static final Logger logger = LogManager.getLogger(StartController.class);

    @FXML
    public void initialize() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        logger.info("=== AVVIO APPLICAZIONE ===");
        aggiornaStatoBottoneEntra();
        gruppoTurno.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            aggiornaStatoBottoneEntra();
        });
    }

    @FXML
    private void onInizializzaClick(ActionEvent event) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Confirm");
        alert.setHeaderText("Warning! You might lose data");
        alert.setContentText("This will delete all the current data and then download it again.\n\nAre you sure you want to continue?");
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            btnInizializza.setDisable(true);
            btnEntra.setDisable(true);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    service.inizializza();
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                btnInizializza.setDisable(false);
                if (gruppoTurno.getSelectedToggle() != null) {
                    btnEntra.setDisable(false);
                }
                mostraAlert(AlertType.INFORMATION, "Success", "Application initialized successfully");
            });

            task.setOnFailed(e -> {
                btnInizializza.setDisable(false);
                mostraAlert(AlertType.ERROR, "Error", "Unable to contact the server: " + task.getException().getMessage());
            });

            new Thread(task).start();
        }
    }

    @FXML
    void onEntraClick(ActionEvent event) throws IOException {
        String turnoScelto = "";
        if (btnGiorno.isSelected()) {
            turnoScelto = "GIORNO";
        } else if (btnSera.isSelected()) {
            turnoScelto = "SERA";
        }
        logger.info("Entrando in modalità: " + turnoScelto );
        
        //CAMBIO SCENA E GRAFICA PER TURNO
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent root = loader.load();

        MenuController controller = loader.getController();

        String turno = btnGiorno.isSelected() ? "GIORNO" : "SERA";
        controller.setup(turno);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        Stage stage = (Stage) btnEntra.getScene().getWindow();
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.setScene(scene);
    }

    private void aggiornaStatoBottoneEntra() {
        boolean turnoSelezionato = gruppoTurno.getSelectedToggle() != null;

        if (turnoSelezionato) {
            btnEntra.setDisable(false);
            btnEntra.setText("MANAGE YOUR MENU");
        } else {
            btnEntra.setDisable(true);
            btnEntra.setText("Select your shift to continue");
        }
    }

    private void mostraAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
