package com.dormhub;

import java.util.Arrays;
import java.util.Scanner;

import com.dormhub.cli.CLIApp;
import com.dormhub.view.GUIMain;

public class Main {

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

        GUIMain.main(new String[0]);
    }
}
