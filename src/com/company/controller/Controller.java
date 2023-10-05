package com.company.controller;

import com.company.listeners.TextListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.IOException;

public class Controller extends JFrame {

    //    private JMenu file = new JMenu("File");
//    private JMenu open = new JMenu("Open");
//    private JMenu save = new JMenu("Save");
//    private JMenu help = new JMenu("Help");

    private int levelScrollBar = 0;

    private JPanel panel = new JPanel();
    protected JTextArea textArea_normal = new JTextArea(16, 16);
    protected JTextArea textArea_hex = new JTextArea(16, 16);
    private JFileChooser fc = new JFileChooser();
    private JScrollPane scroll = new JScrollPane(panel);

    private JMenu file = new JMenu("File");
    private JMenuBar menu = new JMenuBar();
    protected boolean isUpdating = false;

    public Controller() throws HeadlessException {
        //settings
        super("Name");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(screenSize.width / 2 - 200, screenSize.height / 2 - 200, 400, 400);
        textArea_normal.setLineWrap(true);
        textArea_hex.setLineWrap(true);
        textArea_hex.setBorder(BorderFactory.createTitledBorder("hex"));
        textArea_normal.setBorder(BorderFactory.createTitledBorder("normal"));
        //menu
/**
 * русское поле экспериментов
 * */
//        //scroll.putClientProperty("autoscrolls",false);
//        scroll.setAutoscrolls(false);
//        textArea_normal.setAutoscrolls(false);
//        textArea_hex.setAutoscrolls(false);
//        //panel.putClientProperty("JTextAr");
//        textArea_hex.setText("sd");
        panel.add(textArea_hex);
        panel.add(textArea_normal);

//        sync();

        TextListener text=new TextListener(textArea_normal,textArea_hex);
        text.sync();



        //updateScrollBar();


        //граница опасной зоны
        add(scroll);
        setJMenuBar(menu);
        menu.add(file);
        file.add(open);
        file.add("save");
        file.addSeparator();
        file.add("new");


//разобраться почему после открытия не работает листенер, мб сделать открывание через листенер а не экшн

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

//    private void sync() {
//
//        textArea_normal.getDocument().addDocumentListener(new NormalTextListener(textArea_hex, textArea_normal));
//        textArea_hex.getDocument().addDocumentListener(new NormalTextListener(textArea_normal, textArea_hex));
//    }


    private void updateScrollBar() {
        int viewportWidth = scroll.getViewport().getWidth();
        scroll.getVerticalScrollBar().setValue(levelScrollBar * viewportWidth);

    }


    Action open = new AbstractAction("Open") {
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
    };

    private void openFile(String fileName) throws IOException {
        FileReader fr;
        try {
            setTitle(fileName);
            fr = new FileReader(fileName);
            textArea_normal.read(fr, null);
            textArea_hex.setText(textArea_normal.getText());
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
