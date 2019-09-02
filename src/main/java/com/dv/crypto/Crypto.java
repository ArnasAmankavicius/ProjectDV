package com.dv.crypto;

import com.lanterna.gui2.WindowBasedTextGUI;
import com.lanterna.gui2.dialogs.MessageDialog;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Crypto {

    private static SecretKeySpec key;
    private static final String iv = "Vx3CcQl1NPEew6MccIiRsw==";

    public static byte[] generateIV() {
        SecureRandom secureRand = new SecureRandom();
        int AES_LENGTH = 256;
        byte[] iv = new byte[AES_LENGTH / 16];
        secureRand.nextBytes(iv);
        return iv;
    }

    /*
    public static void generateAESKey(WindowBasedTextGUI gui) {
        Panel panel = new Panel();
        BasicWindow window = new BasicWindow();
        panel.setLayoutManager(new LinearLayout());

        panel.addComponent(new Label("Pass:"));
        final TextBox txtPass = new TextBox().addTo(panel);

        panel.addComponent(new Label("Salt: (empty input will use default)"));
        final TextBox txtSalt = new TextBox().addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(1, 0)));

        new Button("Set", new Runnable() {
            @Override
            public void run() {
                String pass = txtPass.getText();
                String salt = txtSalt.getText();
                if (!pass.isEmpty()) {
                    if (salt.isEmpty()) {
                        MessageDialog.showMessageDialog(gui, "Warn", "Setting default salt");
                        salt = "default";
                    }
                    panel.addComponent(new Label("Generating keys..."));
                    generate(pass, salt);
                    MessageDialog.showMessageDialog(gui, "Info", "Keys Generated");
                    window.close();
                } else {
                    MessageDialog.showMessageDialog(gui, "Warn", "Please enter a password");
                }
            }
        }).addTo(panel);

        window.setComponent(panel);
        Collection<Window.Hint> hints = new ArrayList<>();
        hints.add(Window.Hint.CENTERED);
        window.setHints(hints);

        gui.addWindowAndWait(window);
    }
    */

    public static void generate(@NotNull String pass, @NotNull String salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(pass.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey temp = skf.generateSecret(keySpec);

            key = new SecretKeySpec(temp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static byte[] mess(int opmode, byte[] src, WindowBasedTextGUI gui) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(opmode, key, new IvParameterSpec(decode(iv.getBytes())));

            return cipher.doFinal(src);
        } catch (Exception e) {
            if(e.getMessage().contains("bad key"))
                MessageDialog.showMessageDialog(gui, "ERR", "Bad key used to decrypt file");
            else
                MessageDialog.showMessageDialog(gui, "ERR", e.getMessage());
        }
        return src;
    }

    public static byte[] encode(byte[] data) {
        return Base64.getEncoder().encode(data);
    }

    public static byte[] decode(byte[] data) {
        return Base64.getDecoder().decode(data);
    }
}
