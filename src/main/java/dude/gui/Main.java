package dude.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX window for the DUDE task manager.
 */
public class Main extends Application {
    private final GuiController controller = new GuiController();

    /**
     * Builds and displays the main application window.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        TextArea taskList = new TextArea();
        taskList.setEditable(false);
        taskList.setWrapText(true);
        taskList.setPrefRowCount(12);

        TextArea responses = new TextArea();
        responses.setEditable(false);
        responses.setWrapText(true);
        responses.setPrefRowCount(8);

        TextField commandInput = new TextField();
        commandInput.setPromptText("Enter a command, e.g. list or todo read book");
        Button executeButton = new Button("Execute");
        Button closeButton = new Button("Close");

        controller.loadTasks();
        taskList.setText(controller.renderTasks());
        responses.setText("Welcome to DUDE. Enter a command below.\n");

        Runnable executeCommand = () -> {
            String command = commandInput.getText();
            if (command.isBlank()) {
                return;
            }
            responses.appendText(controller.execute(command));
            taskList.setText(controller.renderTasks());
            commandInput.clear();
        };
        executeButton.setOnAction(event -> executeCommand.run());
        commandInput.setOnAction(event -> executeCommand.run());
        closeButton.setOnAction(event -> stage.close());

        HBox commandBar = new HBox(8, commandInput, executeButton, closeButton);
        HBox.setHgrow(commandInput, javafx.scene.layout.Priority.ALWAYS);

        VBox content = new VBox(8,
                new Label("Tasks"), taskList,
                new Label("Command output"), responses,
                commandBar);
        content.setPadding(new Insets(12));

        BorderPane root = new BorderPane(content);
        Scene scene = new Scene(root, 720, 560);
        stage.setTitle("DUDE");
        stage.setScene(scene);
        stage.show();
    }
}
