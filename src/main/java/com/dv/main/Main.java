package com.dv.main;

import com.dv.windows.LoginScreen;
import com.dv.windows.MainScreen;
import com.lanterna.TextColor;
import com.lanterna.gui2.*;
import com.lanterna.screen.Screen;
import com.lanterna.screen.TerminalScreen;
import com.lanterna.terminal.DefaultTerminalFactory;
import com.lanterna.terminal.Terminal;

import java.io.IOException;

public class Main {

    /*
        TODO Figure out how to properly render the text on the screen
     */

    private static WindowBasedTextGUI gui;

    private static BasicWindow currentWindow;

    Main() throws IOException {
        Terminal term = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(term);
        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));

        screen.startScreen();

        gui.addWindowAndWait(new LoginScreen(gui));
        gui.addWindowAndWait(new MainScreen(gui, screen.getTerminalSize()));
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}

