package com.company.listeners;

import com.company.controller.Controller;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NormalTextListener extends Controller implements DocumentListener {//выносить отдельно в два листенера, поскольку у нас конвертация в hex формат
    @Override
    public void insertUpdate(DocumentEvent e) {
        if (!isUpdating) {
            isUpdating = true;
            textArea_hex.setText(textArea_normal.getText());
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

    }
}