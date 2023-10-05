package com.company.listeners;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenEventListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser=new JFileChooser();
        fileChooser.showOpenDialog(new JPanel());
    }
}