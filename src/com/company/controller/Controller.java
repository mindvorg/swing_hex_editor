package com.company.controller;

import com.company.listeners.MouseMarkListener;
import com.company.listeners.TextListener;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
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

//        sync();

        //       TextListener text=new TextListener(textArea_normal,textArea_hex);
        text.sync();

        MouseMarkListener marks = new MouseMarkListener(textArea_normal, textArea_hex);
        marks.sync();

/*
        textArea_normal.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int start = textArea_normal.getSelectionStart();
                int end = textArea_normal.getSelectionEnd();
                String selectedText = textArea_normal.getText().substring(start, end);
                try {
                    String text = String.valueOf(textArea_normal.modelToView2D(start));
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }

                if (!selectedText.isEmpty()) {
                    //    textArea_normal.replaceRange(selectedText, start, end);
//                    String selectedHEXText = textArea_hex.getText().substring(start*2,end*2);
//                    textArea_hex.setSelectionStart(start*2);
//                    textArea_hex.setSelectionEnd(end*2);
//                    textArea_hex.select(start*2,end*2);
                    try {
                        Object o = hl.addHighlight(start*2, end*2, DefaultHighlighter.DefaultPainter);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                    textArea_normal.setSelectedTextColor(Color.RED);
                    textArea_hex.setSelectedTextColor(Color.YELLOW);
                } else {
                    textArea_normal.setForeground(null);
                    hl.removeAllHighlights();
                }
            }
            @Override
            public  void mouseClicked(MouseEvent e){
                hl.removeAllHighlights();
            }
        });
*/


        //граница опасной зоны!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        add(scroll);
        setJMenuBar(menu);
        menu.add(file);
        file.add(open);
        file.add(save);
        file.addSeparator();



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
    Action save=new AbstractAction("Slave") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    };

    private void openFile(String fileName) throws IOException {//надо вынести в отдельный файл по работе с файлами
        FileReader fr;
        try {
            setTitle(fileName);
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
