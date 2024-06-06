package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Controller {

    @FXML
    private VBox root;

    @FXML
    private TextArea noteTextArea;

    @FXML
    private Button saveButton;

    @FXML
    private Button loadButton;

    @FXML
    private Label notificationLabel;

    @FXML
    private ScrollBar themeScrollBar;

    private File currentFile;

    @FXML
    public void initialize() {
        themeScrollBar.valueProperty().addListener((obs, oldValue, newValue) -> {
            double value = newValue.doubleValue() / 100;
            applyTheme(value);
        });
    }

    @FXML
    protected void onSaveButtonClick() {
        String content = noteTextArea.getText().trim();
        if (content.isEmpty()) {
            showAlert("Error", "Nie można zapisać pustej notatki");
            return;
        }

        if (currentFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz notatkę");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialFileName("nowa notatka.txt");

            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                currentFile = file;
                saveFile(file, content);
            }
        } else {
            saveFile(currentFile, content);
        }
    }

    @FXML
    protected void onLoadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Załaduj notatkę z pliku");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            currentFile = file;
            loadFile(file);
        }
    }

    private void saveFile(File file, String content) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
            showNotification("Notatka zapisana!");
            noteTextArea.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile(File file) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            noteTextArea.setText(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showNotification(String message) {
        notificationLabel.setText(message);
        notificationLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> notificationLabel.setVisible(false));
        pause.play();
    }

    private void applyTheme(double value) {
        String backgroundColor = interpolateColor("#ffffff", "#000000", value);
        String textColor = interpolateColor("#000000", "#ffffff", value);
        String buttonColor = interpolateColor("#0078d7", "#505050", value);

        root.setStyle("-fx-background-color: " + backgroundColor + ";");
        noteTextArea.setStyle("-fx-control-inner-background: " + backgroundColor + "; -fx-text-fill: " + textColor + ";");
        saveButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + textColor + ";");
        loadButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + textColor + ";");
    }

    private String interpolateColor(String color1, String color2, double fraction) {
        int r1 = Integer.valueOf(color1.substring(1, 3), 16);
        int g1 = Integer.valueOf(color1.substring(3, 5), 16);
        int b1 = Integer.valueOf(color1.substring(5, 7), 16);

        int r2 = Integer.valueOf(color2.substring(1, 3), 16);
        int g2 = Integer.valueOf(color2.substring(3, 5), 16);
        int b2 = Integer.valueOf(color2.substring(5, 7), 16);

        int r = (int) ((r2 - r1) * fraction + r1);
        int g = (int) ((g2 - g1) * fraction + g1);
        int b = (int) ((b2 - b1) * fraction + b1);

        return String.format("#%02x%02x%02x", r, g, b);
    }

}
