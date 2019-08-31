package com.dv.windows;

import com.dv.crypto.Crypto;
import com.lanterna.TerminalSize;
import com.lanterna.bundle.LanternaThemes;
import com.lanterna.gui2.*;
import com.lanterna.gui2.dialogs.MessageDialog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoginScreen extends BasicWindow {

    public LoginScreen(WindowBasedTextGUI gui) {
        super("Login Screen");

        setHints(Collections.singletonList(Hint.CENTERED));

        Label passLbl = new Label("Pass:");
        Label saltLbl = new Label("Salt: (left blank uses default salt)");
        Label statusLbl = new Label("");

        TextBox passBox = new TextBox().setMask('*').setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        TextBox saltBox = new TextBox().setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        Button setButton = new Button("Set");
        setButton.addListener(button -> {
            String pass = passBox.getText();
            String salt = saltBox.getText();
            if (!pass.isEmpty()) {
                if (salt.isEmpty()) {
                    MessageDialog.showMessageDialog(gui, "Warn", "Default salt used");
                    salt = "d3f4ul7";
                }
                statusLbl.setText("Generating key...");
                Crypto.generate(pass, salt);
                MessageDialog.showMessageDialog(gui, "Info", "Key generated");
                close();
            } else {
                MessageDialog.showMessageDialog(gui, "ERR", "Enter a password");
            }
        });

        List<Component> components = Arrays.asList(passLbl, passBox, new EmptySpace(new TerminalSize(1, 0)), saltLbl, saltBox, setButton, statusLbl);

        Panel panel = new Panel(new LinearLayout());
        for (Component c : components)
            panel.addComponent(c);

        setComponent(panel);
    }
}
