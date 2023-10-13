package com.company.controller;

import com.company.converter.HexFilter;
import com.company.listeners.MouseMarkListener;
import com.company.listeners.TextListener;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Controller extends JFrame {

    //    private JMenu file = new JMenu("File");
//    private JMenu open = new JMenu("Open");
//    private JMenu save = new JMenu("Save");
//    private JMenu help = new JMenu("Help");

    private int levelScrollBar = 0;

    private JPanel panel = new JPanel();
    private final JTextArea textArea_normal = new JTextArea(16, 16);
    private final JTextArea textArea_hex = new JTextArea(16, 16);
    private JFileChooser fc = new JFileChooser();
    private JScrollPane scroll = new JScrollPane(panel);

    private JMenu file = new JMenu("File");
    private JMenuBar menu = new JMenuBar();
    private TextListener text = new TextListener(textArea_normal, textArea_hex);


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


        //text.sync();
        text.syncNorm();
        text.syncHex();

        MouseMarkListener marks = new MouseMarkListener(textArea_normal, textArea_hex);
        marks.sync();


        //filter to HexText
        //AbstractDocument doc= (AbstractDocument) textArea_hex.getDocument();
        PlainDocument doc = (PlainDocument) textArea_hex.getDocument();
        doc.setDocumentFilter(new HexFilter());

        //граница опасной зоны!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        add(scroll);
        setJMenuBar(menu);
        menu.add(file);
        file.add(open);
        file.add(save);
        file.addSeparator();



        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }




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
    Action save=new AbstractAction("Save") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    };

    private void openFile(String fileName) throws IOException {//надо вынести в отдельный файл по работе с файлами
        FileReader fr;
        try {
            fr = new FileReader(fileName);
            //textArea_normal.read(fr, null);
            String output = new String(Files.readAllBytes(Path.of(fileName)));
            //System.out.println(Path.of(fileName));
            //System.out.println(output);
            textArea_normal.setText(output);
            text.sync();
            //textArea_hex.setText(textArea_normal.getText());
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            FileWriter fw;
            try {
                fw= new FileWriter(fc.getSelectedFile().getAbsoluteFile());
                textArea_normal.write(fw);
                fw.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
