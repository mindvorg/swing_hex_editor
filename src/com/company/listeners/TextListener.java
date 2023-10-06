package com.company.listeners;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.math.BigInteger;

public class TextListener {
    private JTextArea textArea_normal;
    private JTextArea textArea_hex;
    private boolean isUpdating = false;

    public TextListener(JTextArea textArea_normal, JTextArea textArea_hex) {
        this.textArea_normal = textArea_normal;
        this.textArea_hex = textArea_hex;
    }

    public void sync() {

        //inserting text in hex from normal
        textArea_normal.getDocument().addDocumentListener(new DocumentListener() {//выносить отдельно в два листенера, поскольку у нас конвертация в hex формат
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println(0);
                if (!isUpdating) {
                    isUpdating = true;
                    //textArea_hex.setText(textArea_normal.getText());
                    //textArea_normal.getText()- берем и посимвольно конвертируем в 16 через цикл и
                    StringBuilder encrypted = new StringBuilder();
                    System.out.println(2);

                  //  for (int i = 0; i < e.getDocument().getLength(); i++) {
                  for (int i=0;i<textArea_normal.getDocument().getLength();i++){
                        int c = 0;
//                        try {
                            //c = e.getDocument().getText(e.getOffset(), e.getLength()).charAt(i);
                            c=textArea_normal.getText().charAt(i);
//                        } catch (BadLocationException ex) {
//                            ex.printStackTrace();
//                        }
                        encrypted.append(Integer.toHexString(c));
                    }
                    textArea_hex.setText(String.valueOf(encrypted));
                    isUpdating = false;
                }

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    StringBuilder encrypted = new StringBuilder();

                    for (int i = 0; i < textArea_normal.getText().length(); i++) {
                        int c = textArea_normal.getText().charAt(i);
                        encrypted.append(Integer.toHexString(c));
                    }
                    textArea_hex.setText(String.valueOf(encrypted));
                    isUpdating = false;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    StringBuilder encrypted = new StringBuilder();
                    for (int i = 0; i < textArea_normal.getText().length(); i++) {
                        int c = textArea_normal.getText().charAt(i);
                        encrypted.append(Integer.toHexString(c));
                    }
                    textArea_hex.setText(String.valueOf(encrypted));
                    isUpdating = false;
                }
            }
        });


        //from hex to normal
        textArea_hex.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

                if (!isUpdating&& textArea_hex.getDocument().getLength()%2==0) {
                    isUpdating = true;
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < textArea_hex.getText().length(); i += 2) {
                        String hexPair = textArea_hex.getText().substring(i, i + 2);
                        int intValue = Integer.parseInt(hexPair, 16);
                        char[] charArray = Character.toChars(intValue);
                        sb.append(charArray);
                    }
                    textArea_normal.setText(String.valueOf(sb));
                    isUpdating = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating&& textArea_hex.getDocument().getLength()%2==0) {
                    isUpdating = true;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < textArea_hex.getText().length(); i += 2) {
                        String hexPair = textArea_hex.getText().substring(i, i + 2);
                        int intValue = Integer.parseInt(hexPair, 16);
                        char[] charArray = Character.toChars(intValue);
                        sb.append(charArray);
                    }
                    textArea_normal.setText(String.valueOf(sb));
                    isUpdating = false;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    textArea_normal.setText(textArea_hex.getText());
                    isUpdating = false;
                }
            }
        });
    }
}
