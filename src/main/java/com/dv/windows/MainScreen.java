package com.dv.windows;

import com.dv.crypto.Crypto;
import com.dv.tools.FileIO;
import com.lanterna.TerminalPosition;
import com.lanterna.TerminalSize;
import com.lanterna.bundle.LanternaThemes;
import com.lanterna.gui2.*;
import com.lanterna.gui2.dialogs.*;
import com.lanterna.input.KeyStroke;
import com.lanterna.input.KeyType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainScreen extends BasicWindow {

    private WindowBasedTextGUI gui;
    private TextBox outputbox;
    private Label textData;
    private File openedFile;

    public MainScreen(WindowBasedTextGUI gui, TerminalSize size) {
        super("");
        this.gui = gui;

        setHints(Collections.singletonList(Hint.FULL_SCREEN));

        addWindowListener(new WindowListener() {
            @Override
            public void onResized(Window window, TerminalSize oldSize, TerminalSize newSize) {
                outputbox.setSize(newSize);
            }

            @Override
            public void onMoved(Window window, TerminalPosition oldPosition, TerminalPosition newPosition) {

            }

            @Override
            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {

                if (keyStroke.getKeyType() == KeyType.Escape)
                    createMenu().showDialog(gui);
            }

            @Override
            public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {

            }
        });
        outputbox = new TextBox(size);

        setComponent(outputbox);
    }

    public void displayFile() {
        openedFile = new FileDialogBuilder().setTitle("Open File").setActionLabel("Open").build().showDialog(gui);
        if (openedFile != null) {
            outputbox.setText("");
            String buffer = new String(Objects.requireNonNull(FileIO.read(openedFile)));
            outputbox.setText(buffer);
        }
    }

    private boolean promptEncryptionQuestion() {
       MessageDialogButton btn = MessageDialog.showMessageDialog(gui, "Encrypt before saving?", "", MessageDialogButton.Yes, MessageDialogButton.No);
       return btn.equals(MessageDialogButton.Yes);
    }

    private void encrypt(){
        if(outputbox.getText().isEmpty()){
            MessageDialog.showMessageDialog(gui, "No data", "Buffer contains no data to encrypt");
            return;
        }

        byte[] buffer = this.outputbox.getText().trim().getBytes();
        buffer = Crypto.encrypt(buffer);
        buffer = Crypto.encode(buffer);
        outputbox.setText(new String(buffer));
    }

    private void decrypt(){
        if(outputbox.getText().isEmpty()){
            MessageDialog.showMessageDialog(gui, "No data", "Buffer contains no data to decrypt");
            return;
        }

        byte[] buffer = this.outputbox.getText().trim().getBytes();
        System.out.println(buffer.length);
        buffer = Crypto.decode(buffer);
        buffer = Crypto.decrypt(buffer);
        outputbox.setText(new String(buffer));
    }

    private ActionListDialog createMenu(){
        return new ActionListDialogBuilder().setTitle("Menu")
                .addAction("Load file...", this::displayFile)
                .addAction("Save file...", () -> {
                    byte[] buffer = outputbox.getText().getBytes();
                    if(openedFile == null)
                    {
                        String title = TextInputDialog.showDialog(gui, "File name", "Please enter the file name to save as.", "");
                        openedFile = new File(title);
                        try {
                            openedFile.createNewFile();
                        } catch (IOException e) {
                           e.printStackTrace();
                            return;
                        }
                    }
                    if(promptEncryptionQuestion()){
                        buffer = Crypto.encode(buffer);
                        buffer = Crypto.encrypt(buffer);
                    }

                    FileIO.write(openedFile, buffer);
                    MessageDialog.showMessageDialog(gui, "Saved!", "File has been saved to " + openedFile);
                })
                .addAction("Encrypt", this::encrypt)
                .addAction("Decrypt", () -> {

                })
                .addAction("Reset Keys", () -> gui.addWindowAndWait(new LoginScreen(gui)))
                .addAction("Exit", () -> System.exit(0)).build();
    }
}
