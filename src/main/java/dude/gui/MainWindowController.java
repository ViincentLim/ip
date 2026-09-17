package dude.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Coordinates the FXML controls in the main DUDE window.
 */
public class MainWindowController {
    @FXML
    private TextArea taskListArea;
    @FXML
    private ScrollPane conversationScroll;
    @FXML
    private VBox conversationContainer;
    @FXML
    private TextField commandInput;
    @FXML
    private Button executeButton;

    private final GuiController controller;

    /**
     * Creates the controller used by FXML.
     */
    public MainWindowController() {
        controller = new GuiController();
    }

    /**
     * Loads persisted tasks and prepares automatic conversation scrolling.
     */
    @FXML
    private void initialize() {
        controller.loadTasks();
        taskListArea.setText(controller.renderTasks());
        appendMessage("Welcome to DUDE. Enter a command below.", DialogBox.Kind.APP);
        if (!controller.getLoadMessage().isBlank()) {
            appendMessage(controller.getLoadMessage(), DialogBox.Kind.ERROR);
        }
        conversationContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> conversationScroll.setVvalue(1.0)));
        commandInput.requestFocus();
    }

    /**
     * Executes the command in the input field when the button or Enter is used.
     */
    @FXML
    private void executeCommand() {
        String command = commandInput.getText();
        if (command == null || command.isBlank()) {
            return;
        }
        appendMessage(command, DialogBox.Kind.USER);
        GuiResponse response = controller.execute(command);
        appendMessage(response.text(), response.error() ? DialogBox.Kind.ERROR : DialogBox.Kind.APP);
        taskListArea.setText(controller.renderTasks());
        commandInput.clear();
        commandInput.requestFocus();
    }

    /**
     * Closes the current JavaFX window.
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) executeButton.getScene().getWindow();
        stage.close();
    }

    private void appendMessage(String message, DialogBox.Kind kind) {
        conversationContainer.getChildren().add(new DialogBox(message, kind));
        Platform.runLater(() -> conversationScroll.setVvalue(1.0));
    }
}
