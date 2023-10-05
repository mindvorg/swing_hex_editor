package com.company.listeners;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextListener  {
    private JTextArea textArea_normal;
    private JTextArea textArea_hex;
    private boolean isUpdating=false;

    public TextListener(JTextArea textArea_normal, JTextArea textArea_hex) {
        this.textArea_normal = textArea_normal;
        this.textArea_hex = textArea_hex;
    }

    public void sync() {
        textArea_normal.getDocument().addDocumentListener(new DocumentListener() {//выносить отдельно в два листенера, поскольку у нас конвертация в hex формат
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    //textArea_hex.setText(textArea_normal.getText());
                    //textArea_normal.getText()- берем и посимвольно конвертируем в 16 через цикл и
                    StringBuilder encrypted = new StringBuilder();

                    for (int i = 0; i < textArea_normal.getText().length(); i++) {
                        int c = textArea_normal.getText().charAt(i);
                        encrypted.append( Integer.toHexString(c) );
                    }
                    textArea_hex.setText(String.valueOf(encrypted));
                    isUpdating = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    textArea_hex.setText(textArea_normal.getText());
                    isUpdating = false;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    textArea_hex.setText(textArea_normal.getText());
                    isUpdating = false;
                }
            }
        });

        textArea_hex.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    textArea_normal.setText(textArea_hex.getText());
                    isUpdating = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    textArea_normal.setText(textArea_hex.getText());
                    isUpdating = false;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    textArea_hex.setText(textArea_normal.getText());
                    isUpdating = false;
                }
            }
        });
    }
}
