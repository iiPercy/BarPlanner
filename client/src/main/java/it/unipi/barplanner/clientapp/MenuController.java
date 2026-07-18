package it.unipi.barplanner.clientapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.control.Label;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MenuController {

    private String turnoCorrente;
    private final ObservableList<MenuItem> menuData = FXCollections.observableArrayList();
    private final Service service = new Service();
    private final PdfService pdfService = new PdfService();
    private static final Logger logger = LogManager.getLogger(MenuController.class);
    private List<MenuItem> modificati = new ArrayList<>();

    @FXML
    private VBox rootNode;
    @FXML
    private Label lblTitolo;
    @FXML
    private Label lblTurnoInfo;
    @FXML
    private Button btnIndietro;
    @FXML
    private Button btnPdf;
    @FXML
    private Button btnSalva;
    @FXML
    private TextField txtRicerca;
    //TABELLA
    @FXML
    private TableView<MenuItem> tabellaMenu;
    @FXML
    private TableColumn<MenuItem, String> colNome;
    @FXML
    private TableColumn<MenuItem, String> colIngredienti;
    @FXML
    private TableColumn<MenuItem, String> colCategoria;
    @FXML
    private TableColumn<MenuItem, Double> colPrezzo;

    public void setup(String turno) {
        this.turnoCorrente = turno;

        rootNode.getStyleClass().removeAll("tema-giorno", "tema-sera");

        if ("GIORNO".equals(turno)) {
            impostaGraficaGiorno();
        } else {
            impostaGraficaSera();
        }
        caricaDati(turnoCorrente);
        lblTurnoInfo.setText("Shift: " + ((turnoCorrente.equals("GIORNO")) ? "Coffee bar" : "Cocktail bar"));
    }

    private void impostaGraficaGiorno() {
        rootNode.getStyleClass().add("tema-giorno");
        lblTitolo.setText("☀ Coffee bar Menu");
    }

    private void impostaGraficaSera() {
        rootNode.getStyleClass().add("tema-sera");
        lblTitolo.setText("🌙 Cocktail Bar Menu");
    }

    private void caricaDati(String turno) {
        logger.info("Caricamento in corso...");
        Task<List<MenuItem>> task = new Task<>() {
            @Override
            protected List<MenuItem> call() throws Exception {
                return service.getMenu(turno);
            }
        };

        task.setOnSucceeded(e -> {
            menuData.clear();

            List<MenuItem> itemsScaricati = task.getValue();
            if (itemsScaricati != null) {
                menuData.addAll(itemsScaricati);
            }

            logger.info("Caricamento completato. Elementi: " + menuData.size());
        });

        task.setOnFailed(e -> {
            logger.error("Errore di caricamento!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to download menu items: " + task.getException().getMessage());
            alert.showAndWait();
        });

        new Thread(task).start();
    }

    @FXML
    public void initialize() {
        btnIndietro.setDisable(false);
        btnSalva.setDisable(true);

        //TABELLA
        tabellaMenu.setEditable(true);
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
        colPrezzo.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrezzo()));
        colPrezzo.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        colPrezzo.setOnEditCommit(event -> {
            MenuItem item = event.getRowValue();
            if (event.getNewValue() != null) {
                item.setPrezzo(event.getNewValue());
                modificati.add(item);
                btnSalva.setDisable(false);
                logger.info("Prezzo di " + item.getNome() + " aggiornato a: " + item.getPrezzo());
                logger.info("Modifica non salvata: " + item.getNome());
            }
        });

        colIngredienti.setCellValueFactory(cellData -> {
            var ingredienti = cellData.getValue().getIngredienti();
            if (ingredienti == null || ingredienti.isEmpty()) {
                return new SimpleStringProperty("");
            }
            String testo = String.join(", ", ingredienti);
            return new SimpleStringProperty(testo);
        });

        //CANCELLAZIONE
        ContextMenu contextMenu = new ContextMenu();
        javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem("Delete permanently");
        deleteItem.setOnAction(event -> {
            onRimuoviClick(null);
        });
        contextMenu.getItems().add(deleteItem);
        tabellaMenu.setRowFactory(tv -> {
            TableRow<MenuItem> row = new TableRow<>();
            row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return row;
        });

        //RICERCA
        FilteredList<MenuItem> filteredData = new FilteredList<>(menuData, b -> true);
        txtRicerca.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                return MenuUtils.matchRicerca(item, newValue);
            });
        });

        //ORDINAMENTO
        SortedList<MenuItem> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tabellaMenu.comparatorProperty());
        tabellaMenu.setItems(sortedData);
    }

    @FXML
    public void onIndietroClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Go back to the starting window");
        alert.setHeaderText("Warning: Unsaved changes will be lost.");
        alert.setContentText("Are you sure you want to quit?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onAggiungiClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AggiungiItem.fxml"));
            Parent root = loader.load();

            AggiungiController.setTipoTurno(turnoCorrente);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add new Item");
            dialogStage.setScene(new Scene(root));

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(rootNode.getScene().getWindow());
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            AggiungiController controller = loader.getController();
            MenuItem nuovoItem = controller.getProdottoCreato();

            if (nuovoItem != null) {
                menuData.add(nuovoItem);
                tabellaMenu.scrollTo(nuovoItem);
                logger.info("elemento aggiunto alla tabella");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Item saved successfully");
            }

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Impossibile aprire la finestra di aggiunta");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to open add new item tab");
            alert.showAndWait();
        }
    }

    @FXML
    void onSalvaClick(ActionEvent event) {
        if (modificati.isEmpty()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save changes");
        alert.setHeaderText("All the changes are about to be saved");
        alert.setContentText("Are you sure you want to continue?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            btnSalva.setDisable(true);
            logger.info("Inizio salvataggio modifiche di " + modificati.size() + " oggetti");
            Task<Void> taskSalvataggio = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    int cont = 0;
                    for (MenuItem item : modificati) {
                        updateMessage("Saving item " + (cont + 1) + " of " + modificati.size());
                        service.aggiornaItem(item);
                        cont++;
                        updateProgress(cont, modificati.size());
                    }
                    return null;
                }
            };

            taskSalvataggio.setOnSucceeded(e -> {
                modificati.clear();
                logger.info("Changes saved successfully");
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Success");
                alert2.setHeaderText(null);
                alert2.setContentText("Changes saved successfully");
                alert2.showAndWait();
            });

            taskSalvataggio.setOnFailed(e -> {
                btnSalva.setDisable(false);
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Error");
                alert2.setHeaderText(null);
                alert2.setContentText("Unable to save changes: " + taskSalvataggio.getException().getMessage());
                alert2.showAndWait();
            });
            new Thread(taskSalvataggio).start();
        }
    }

    @FXML
    public void onRimuoviClick(ActionEvent event) {
        MenuItem selectedItem = tabellaMenu.getSelectionModel().getSelectedItem();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm delete action");
        confirm.setHeaderText("Are you sure you want to delete '" + selectedItem.getNome() + "'?");
        confirm.setContentText("This operation cannot be cancelled");

        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        if (selectedItem.getId() == null) {
            menuData.remove(selectedItem);
            logger.info("Rimosso item locale (non salvato)");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.deleteItem(selectedItem.getId());
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            menuData.remove(selectedItem);
            logger.info("Deleted successfully");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Item deleted successfully");
            alert.showAndWait();
        });

        task.setOnFailed(e -> {
            logger.error("Errore delete: " + task.getException());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error while deleting: " + task.getException().getMessage());
            alert.showAndWait();
        });
        new Thread(task).start();
    }

    @FXML
    public void onPdfClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Menu " + ((turnoCorrente.equals("GIORNO")) ? "Coffee bar" : "Cocktail bar"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Menu_" + ((turnoCorrente.equals("GIORNO")) ? "Coffee bar" : "Cocktail bar") + ".pdf");
        File file = fileChooser.showSaveDialog(rootNode.getScene().getWindow());

        if (file == null) {
            return;
        }

        List<MenuItem> datiDaStampare = new ArrayList<>(tabellaMenu.getItems());
        String turnoPerStampa = this.turnoCorrente;

        Button btnSource = (Button) event.getSource();
        btnSource.setDisable(true);

        Task<Void> taskPdf = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logger.info("creazione PDF in corso");
                pdfService.creaPdfMenu(datiDaStampare, file, turnoPerStampa);
                return null;
            }
        };

        taskPdf.setOnSucceeded(e -> {
            btnSource.setDisable(false);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pdf created successfully");
            alert.setHeaderText(null);
            alert.setContentText("Your PDF file has been saved in:\n" + file.getAbsolutePath());
            alert.showAndWait();
            try {
                java.awt.Desktop.getDesktop().open(file);
            } catch (Exception ex) {
            }
        });

        taskPdf.setOnFailed(e -> {
            btnSource.setDisable(false);
            btnSource.setText("Save PDF");
            Throwable ex = taskPdf.getException();
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to generate your PDF");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        });
        
        new Thread(taskPdf).start();
    }
}
