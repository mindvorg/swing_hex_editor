package com.company.listeners;

import com.company.controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;

public class OpenEventListener extends AbstractAction {
    private JFileChooser fc;
    private JTextArea textArea_normal;
    private JTextArea textArea_hex;

    public OpenEventListener(String name, JFileChooser fc, JTextArea textArea_normal, JTextArea textArea_hex) {
        super(name);
        this.fc = fc;
        this.textArea_normal = textArea_normal;
        this.textArea_hex = textArea_hex;
    }


    private void openFile(String fileName) throws IOException {//надо вынести в отдельный файл по работе с файлами
        FileReader fr;
        try {
            TextListener text = new TextListener(textArea_normal, textArea_hex);

            fr = new FileReader(fileName);
            textArea_normal.read(fr, null);
            //text.sync();
            textArea_hex.setText(textArea_normal.getText());
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                openFile(String.valueOf(fc.getSelectedFile().getAbsoluteFile()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}