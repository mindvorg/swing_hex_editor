package com.company.listeners;

import com.company.controller.Controller;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class HexTextListener extends Controller implements DocumentListener {
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

    }
}