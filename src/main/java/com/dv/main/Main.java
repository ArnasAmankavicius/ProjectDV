package com.dv.main;

import com.dv.crypto.Crypto;
import com.dv.windows.LoginScreen;
import com.dv.windows.MainScreen;
import com.lanterna.TextColor;
import com.lanterna.bundle.LanternaThemes;
import com.lanterna.gui2.*;
import com.lanterna.screen.Screen;
import com.lanterna.screen.TerminalScreen;
import com.lanterna.terminal.DefaultTerminalFactory;
import com.lanterna.terminal.Terminal;

import java.io.IOException;

public class Main {

    private static WindowBasedTextGUI gui;

    Main() throws IOException {
        Terminal term = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(term);
        gui = new MultiWindowTextGUI(screen);
        gui.setTheme(LanternaThemes.getRegisteredTheme("businessmachine"));

        screen.startScreen();

        gui.addWindowAndWait(new LoginScreen(gui));
        gui.addWindowAndWait(new MainScreen(gui, screen.getTerminalSize()));
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}

