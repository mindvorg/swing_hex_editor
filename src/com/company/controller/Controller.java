package com.company.controller;

import com.company.converter.HexFilter;
import com.company.converter.NormFilter;
import com.company.listeners.MouseMarkListener;
import com.company.listeners.TextListener;
import com.company.models.TableModel;

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
    private final JTextArea numCols = new JTextArea(0, 3);
    private int caretPos=0;

    private JPanel panel = new JPanel();
    private JTextArea textArea_normal = new JTextArea(6, /*8*/25);
    private JTextArea textArea_hex = new JTextArea(6, /*12*/25);
    private JFileChooser fc = new JFileChooser();
    private JScrollPane scroll = new JScrollPane(panel);

    private JMenu file = new JMenu("File");
    private JMenuBar menu = new JMenuBar();
    private TextListener text = new TextListener(textArea_normal, textArea_hex, numCols);


    public Controller() throws HeadlessException {
        //settings
        super("Name");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(screenSize.width / 2 - 200, screenSize.height / 2 - 200, 1000, 400);
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
        numCols.setEditable(false);


        panel.setAutoscrolls(false);
        textArea_hex.setTabSize(8);

        //panel.add(numCols); делается вместе с корректным переносом на новые строки
        JTable table=new JTable(4,4);
        table.isEditing();
        table.setValueAt("asd",1,1);
        // мы делаем клаас поле, у него ограничение в 2 символа, это JTextArea и на него вешается listener
      //  panel.add(table);

        TableModel tbl=new TableModel();
       // panel.add(tbl);
        panel.add(textArea_hex);
        panel.add(textArea_normal);


        text.sync();
       // text.syncToHex();
     //    text.syncToNorm();


        MouseMarkListener marks = new MouseMarkListener(textArea_normal, textArea_hex);
        marks.sync();


        //filter to HexText
        //AbstractDocument doc= (AbstractDocument) textArea_hex.getDocument();


                PlainDocument docHex = (PlainDocument) textArea_hex.getDocument();
        docHex.setDocumentFilter(new HexFilter());
        PlainDocument docNorm=(PlainDocument) textArea_normal.getDocument();
        docNorm.setDocumentFilter(new NormFilter());
//        ((AbstractDocument) textArea_hex.getDocument()).setDocumentFilter(new Filter());
/*        textArea_hex.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                caretPos=textArea_hex.getCaretPosition();            }
            @Override
            public void keyReleased(KeyEvent e) {
                textArea_hex.setCaretPosition(caretPos);
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });*/




        //граница опасной зоны!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        add(scroll);
        setJMenuBar(menu);
        menu.add(file);
        file.add(open);
        file.add(save);
        file.add(replace);
        file.addSeparator();


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private static JTextArea createLimitedTextArea(int maxLength) {
        JTextArea textArea = new JTextArea(5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) {
                    return;
                }
                if (getLength() == 0 || textArea.getCaretPosition() % (maxLength + 1) == 0) {
                    super.insertString(offs, str, a);
                }
            }
        });
        return textArea;
    }

    private static void limitLineLength(JTextArea textArea,int maxLength) throws BadLocationException {
        Document doc=textArea.getDocument();
        int lineCount=textArea.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            int lineStart= textArea.getLineStartOffset(i);
            int lineEnd=textArea.getLineEndOffset(i);
            int lineLength=lineEnd-lineStart-1;
            if(lineLength>maxLength){
                try {
                    doc.insertString(lineEnd-1,"\n",null);
                }catch (BadLocationException e){
                    e.printStackTrace();
                }
            }
        }
    }

//    private void updateScrollBar() {
//        int viewportWidth = scroll.getViewport().getWidth();
//        scroll.getVerticalScrollBar().setValue(levelScrollBar * viewportWidth);
//
//    }


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
    Action save = new AbstractAction("Save") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    };
    Action replace = new AbstractAction("replace") {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("asd");
            JPanel panel = new JPanel(new GridLayout(4, 3));
            JTextArea text1 = new JTextArea();
            text1.setBorder(BorderFactory.createTitledBorder("текст замены"));
            JTextField textField = new JTextField();
            textField.setBorder(BorderFactory.createTitledBorder("индекс вставки"));
            JCheckBox checkBox = new JCheckBox("insert with replace or not");
            panel.add(checkBox);
            panel.add(text1);
            panel.add(textField);
            JOptionPane.showMessageDialog(null, panel);
            if (checkBox.isSelected())
                textArea_normal.replaceRange(text1.getText(), Integer.parseInt(textField.getText()), Integer.parseInt(textField.getText()) + text1.getText().length());
            else textArea_normal.insert(text1.getText(), Integer.parseInt(textField.getText()));
        }
    };

    private void openFile(String fileName) throws IOException {//надо вынести в отдельный файл по работе с файлами
        FileReader fr;
        try {
            fr = new FileReader(fileName);
            //textArea_normal.read(fr, null);
            String output = new String(Files.readAllBytes(Path.of(fileName)));
            //System.out.println(Path.of(fileName));
            System.out.println(output);
            textArea_normal.setText("");
            textArea_normal.setText(output);
            textArea_hex.setText("");
            System.out.println("hex:" + textArea_hex);
            System.out.println("norm:" + textArea_normal);
            //text.sync();
            //textArea_hex.setText(textArea_normal.getText());
            //  text.syncHex();
            //text.syncNorm();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            FileWriter fw;
            try {
                fw = new FileWriter(fc.getSelectedFile().getAbsoluteFile());
                textArea_normal.write(fw);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
