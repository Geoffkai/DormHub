package com.dormhub;

import com.dormhub.cli.CLIApp;
import java.util.Arrays;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/app.fxml"));
        Scene scene = new Scene(loader.load(), 420, 260);

        var css = Main.class.getResource("/app.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("DormHub");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        boolean cliMode = Arrays.stream(args).anyMatch("--cli"::equalsIgnoreCase);
        boolean guiMode = Arrays.stream(args).anyMatch("--gui"::equalsIgnoreCase);

        if (cliMode || !guiMode) {
            try (Scanner scanner = new Scanner(System.in)) {
                CLIApp cliApp = new CLIApp(scanner);
                cliApp.run();
            }
            return;
        }

        launch(args);
    }
}

