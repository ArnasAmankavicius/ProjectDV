package com.dv.tools;

import com.lanterna.gui2.WindowBasedTextGUI;
import com.lanterna.gui2.dialogs.FileDialog;
import com.lanterna.gui2.dialogs.FileDialogBuilder;

import java.io.*;

public class FileIO {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte[] read(File file)
    {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void write(File file, byte[] data)
    {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static File getFile(WindowBasedTextGUI gui){
        return new FileDialogBuilder().setTitle("Select a file").setDescription("Select a file to encrypt").setActionLabel("Select").build().showDialog(gui);
    }
}
