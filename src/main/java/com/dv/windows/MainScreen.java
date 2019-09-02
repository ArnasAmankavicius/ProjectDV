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

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainScreen extends BasicWindow {

    private WindowBasedTextGUI gui;
    private TextBox outputbox;
    private Label textData;
    private File openedFile;

    private int iterationCount = 16; // how many times to encrypt or decrypt data
    private boolean encryptByDefault = true;

    public MainScreen(WindowBasedTextGUI gui, TerminalSize size) {
        super("Text Editor");
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

                if(keyStroke.isCtrlDown() && keyStroke.getCharacter() == 't')
                {
                    outputbox.addLine(getTimeStamp());
                }
            }

            @Override
            public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {

            }
        });

        outputbox = new TextBox(size);
        setComponent(outputbox);
    }

    private void displayFile() {
        openedFile = FileIO.getFile(gui);
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

    private boolean promptOverwriteQuestion(){
        MessageDialogButton btn = MessageDialog.showMessageDialog(gui, "Overwrite", "Should overwrite current file?", MessageDialogButton.Yes, MessageDialogButton.No);
        return btn.equals(MessageDialogButton.No);
    }

    private boolean promptSaveQuestion(){
        MessageDialogButton btn = MessageDialog.showMessageDialog(gui, "Save", "Do you wish to save the current file?", MessageDialogButton.Yes, MessageDialogButton.No);
        return btn.equals(MessageDialogButton.Yes);
    }

    private void doTask(int opmode){
        if(outputbox.getText().isEmpty()){
            MessageDialog.showMessageDialog(gui, "No data", "Buffer contains no data");
            return;
        }

        byte[] buffer = this.outputbox.getText().getBytes();
        if(opmode == Cipher.ENCRYPT_MODE)
        {
            for(int i = 0; i < iterationCount; i++)
                buffer = Crypto.mess(opmode, buffer);
            buffer = Crypto.encode(buffer);
        } else if(opmode == Cipher.DECRYPT_MODE) {
            buffer = Crypto.decode(buffer);
            for(int i = 0; i < iterationCount; i++)
                buffer = Crypto.mess(opmode, buffer);
        }

        outputbox.setText(new String(buffer));
    }

    private void createFile(){
        String title = TextInputDialog.showDialog(gui, "File name", "Please enter the file name to save as.", "");
        File dir = new DirectoryDialogBuilder().setTitle("Select a directory").setDescription("Chose a location to save the file to").setActionLabel("Select").build().showDialog(gui);
        openedFile = new File(dir, title);
        try {
            openedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile(){
        byte[] buffer = outputbox.getText().getBytes();
        boolean encrypt;
        if(openedFile == null)
        {
            createFile();
        } else {
            if(promptOverwriteQuestion())
                createFile();
        }
        if(!encryptByDefault) {
            encrypt = promptEncryptionQuestion();
        } else
            encrypt = true;

        if(encrypt) {
            for (int i = 0; i < iterationCount; i++)
                buffer = Crypto.mess(Cipher.ENCRYPT_MODE, buffer);
            buffer = Crypto.encode(buffer);
        }
        FileIO.write(openedFile, buffer);
        MessageDialog.showMessageDialog(gui, "Saved!", "File has been saved to " + openedFile);
    }

    private String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("## YYYY/dd/MM | HH:mm:ss.SS ##");
        return sdf.format(new Date());
    }

    private ActionListDialog createMenu(){
        return new ActionListDialogBuilder().setTitle("Menu")
                .addAction("New File...", () -> {
                    if(!outputbox.getText().isEmpty())
                    {
                        if(promptSaveQuestion())
                            saveFile();
                        outputbox.setText("");
                    }
                })
                .addAction("Load file...", this::displayFile)
                .addAction("Save file...", this::saveFile)
                .addAction("Encrypt", () -> doTask(Cipher.ENCRYPT_MODE))
                .addAction("Encrypt file...", () -> {
                    MessageDialog.showMessageDialog(gui, "Info","Encrypting...");
                    File file = FileIO.getFile(gui);
                    byte[] buffer = FileIO.read(file);
                    for(int i = 0; i < iterationCount; i++)
                        buffer = Crypto.mess(Cipher.ENCRYPT_MODE, buffer);

                    buffer = Crypto.encode(buffer);
                    FileIO.write(file, buffer);

                    MessageDialog.showMessageDialog(gui, "Encrypted!", "Encryption completed");
                })
                .addAction("Decrypt", () -> doTask(Cipher.DECRYPT_MODE))
                .addAction("Decrypt file...", () -> {
                    MessageDialog.showMessageDialog(gui, "Info","Decrypting...");
                    File file = FileIO.getFile(gui);
                    byte[] buffer = FileIO.read(file);
                    buffer = Crypto.decode(buffer);
                    for(int i = 0; i < iterationCount; i++)
                        buffer = Crypto.mess(Cipher.DECRYPT_MODE, buffer);

                    FileIO.write(file, buffer);

                    MessageDialog.showMessageDialog(gui, "Decrypted!", "Decryption completed");
                })
                .addAction("Settings", this::createSettings)
                .addAction("Exit", () -> System.exit(0)).build();
    }

    private void createSettings(){
        new ActionListDialogBuilder().setTitle("Settings")
                .addAction("Change iteration count", () -> {
                    BigInteger temp = TextInputDialog.showNumberDialog(gui, "Iteration", "Enter iteration amount", "16");
                    iterationCount = temp != null ? temp.intValue() : 16;
                    if(temp == null)
                        MessageDialog.showMessageDialog(gui, "ERR", "Failure getting iteration amount");
                    else
                    {
                        MessageDialog.showMessageDialog(gui, "INFO", "Iteration count set!");
                        iterationCount = temp.intValue();
                    }
                })
                .addAction(String.format("Encrypt on save = '%1$s'", encryptByDefault ? "on" : "off"), () -> {
                    encryptByDefault = !encryptByDefault;
                    if(encryptByDefault)
                        MessageDialog.showMessageDialog(gui, "INFO", "Files will be encrypted by default when saved.");
                    else
                        MessageDialog.showMessageDialog(gui, "INFO", "Files will not be encrypted by default when saved.");
                })
                .addAction("Reset Keys", () -> gui.addWindowAndWait(new LoginScreen(gui)))
                .build().showDialog(gui);
    }
}
