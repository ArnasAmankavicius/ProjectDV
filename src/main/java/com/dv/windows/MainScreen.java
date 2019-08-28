package com.dv.windows;

import com.lanterna.TerminalPosition;
import com.lanterna.TerminalSize;
import com.lanterna.gui2.*;
import com.lanterna.gui2.dialogs.FileDialogBuilder;
import com.lanterna.input.KeyStroke;
import com.lanterna.input.KeyType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainScreen extends BasicWindow {

    private WindowBasedTextGUI gui;
    private TextBox outputbox;
    private Label currentFilePath;

    public MainScreen(WindowBasedTextGUI gui, TerminalSize size) {
        super("");
        this.gui = gui;

        setHints(Collections.singletonList(Hint.FULL_SCREEN));

        Panel panel = new Panel(new LinearLayout().setSpacing(1));

        addWindowListener(new WindowListener() {
            @Override
            public void onResized(Window window, TerminalSize oldSize, TerminalSize newSize) {
            }

            @Override
            public void onMoved(Window window, TerminalPosition oldPosition, TerminalPosition newPosition) {

            }

            @Override
            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if (keyStroke.getKeyType() == KeyType.Escape)
                    displayFile();
            }

            @Override
            public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {

            }
        });

        currentFilePath = new Label("No path selected");
        outputbox = new TextBox().setSize(panel.getSize()).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        outputbox.addTo(panel);

        panel.addComponent(outputbox);
        panel.addComponent(currentFilePath);

        setComponent(panel);
    }

    public void displayFile() {
        File file = new FileDialogBuilder().setTitle("Open File").setActionLabel("Open").build().showDialog(gui);
        if (file != null) {
            outputbox.setText("");
            String data = read(file);
            currentFilePath.setText(file.getAbsolutePath());
            outputbox.setText(data);
        } else {
            currentFilePath.setText("No path selected");
        }
    }

    public String read(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Empty File";
    }
}
