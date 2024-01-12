package com.company.controller;

import com.company.converter.Filter;
import com.company.converter.HexFilter;
import com.company.converter.NormFilter;

import com.company.gigachatTable.MyJTable;
import com.company.listeners.MouseMarkListener;
import com.company.listeners.TextListener;

import static java.nio.charset.StandardCharsets.*;


import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Controller extends JFrame {

    //    private JMenu file = new JMenu("File");
//    private JMenu open = new JMenu("Open");
//    private JMenu save = new JMenu("Save");
//    private JMenu help = new JMenu("Help");

    private int levelScrollBar = 0;
    private final JTextArea numCols = new JTextArea(0, 3);
    private int caretPos = 0;
    private File tempFile;
    {
        try {
            tempFile = File.createTempFile("hello", ".tmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel panel = new JPanel(new BorderLayout(3,3));
//    private JTextArea textArea_normal = new JTextArea(6, /*8*/25);
//    private JTextArea textArea_hex = new JTextArea(6, /*12*/25);
    private MyJTable table = new MyJTable(tempFile);
    private JFileChooser fc = new JFileChooser();
    JPanel panelTable=new JPanel();
    private JScrollPane scroll = new JScrollPane(panelTable);
    private ArrayList<Integer[]> selection = new ArrayList<>();
    private JMenu file = new JMenu("File");
    private JMenu utils = new JMenu("utils");
    private JMenu view = new JMenu("view");
    private JMenuBar menu = new JMenuBar();
//    private TextListener text = new TextListener(textArea_normal, textArea_hex, numCols);


    public Controller() throws HeadlessException {
        //settings
        super("Name");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(screenSize.width / 2 - 400, screenSize.height / 2 - 200, 800, 400);
//        textArea_normal.setLineWrap(true);
//        textArea_hex.setLineWrap(true);
//        textArea_hex.setBorder(BorderFactory.createTitledBorder("hex"));
//        textArea_normal.setBorder(BorderFactory.createTitledBorder("normal"));
        //menu
/**
 * русское поле экспериментов
 * */
        //Charset charset = Charset.forName("windows-1251");

//
//        System.out.println(Character.toChars(Integer.parseInt("43f", 16)));
//        System.out.println(Character.toChars(Integer.parseInt("435", 16)));


//        //scroll.putClientProperty("autoscrolls",false);
//        scroll.setAutoscrolls(false);
//        textArea_normal.setAutoscrolls(false);
//        textArea_hex.setAutoscrolls(false);
//        //panel.putClientProperty("JTextAr");
//        textArea_hex.setText("sd");
        numCols.setEditable(false);

        panel.setAutoscrolls(false);
//        textArea_hex.setTabSize(8);


        MouseListener listener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selection.clear();
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row != -1 && col != -1) {
                    selection.add(new Integer[]{row, col});
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("new marked");
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                //    if (selection.get(0)[0] != row || selection.get(0)[1] != col) {
                if (row != -1 && col != -1) {
                    selection.add(new Integer[]{row, col});
                }
                if (selection.get(0)[0].equals(selection.get(1)[0])) selection.sort(Comparator.comparingInt(o -> o[1]));
                else selection.sort(Comparator.comparingInt(o -> o[0]));
                for (Integer[] elem :
                        selection) {
                    System.out.println(Arrays.toString(elem));
                }
                //     table.mark(selection);
                //        if (!(selection.get(0)[0] == row && selection.get(0)[1] == col))
                if ((selection.get(0)[0] != selection.get(1)[0] || selection.get(0)[1] != selection.get(1)[1])) table.clearSelection();



            }
        };
        table.addMouseListener(listener);
        // table.setSelectionMode(1);

        //panel.add(table);
        panelTable.add(table);
//        JScrollPane scrollPane=new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        Dimension d=table.getPreferredSize();
//        scrollPane.setPreferredSize(new Dimension(d.width,table.getRowHeight()*rows));
//        JPanel navigation= new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton next=new JButton("↑");
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            table.moveNext();
            }
        });
        JButton prev=new JButton("↓");
        prev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.movePrev();
            }
        });
        JPanel panelButtons=new JPanel();
        panelButtons.add(prev);
        panelButtons.add(next);

        panel.add(scroll,BorderLayout.CENTER);
        panel.add(panelButtons, BorderLayout.SOUTH);

//        panel.add(videoTable);
//        panel.add(textArea_hex);
        //panel.add(textArea_normal);


//        text.sync();
        // text.syncToHex();
        //    text.syncToNorm();


//        MouseMarkListener marks = new MouseMarkListener(textArea_normal, textArea_hex);
//        marks.sync();
//
//
//        PlainDocument docHex = (PlainDocument) textArea_hex.getDocument();
//        docHex.setDocumentFilter(new HexFilter());
//        PlainDocument docNorm = (PlainDocument) textArea_normal.getDocument();
//        docNorm.setDocumentFilter(new NormFilter());
//        ((AbstractDocument) textArea_hex.getDocument()).setDocumentFilter(new Filter());


        //граница опасной зоны!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        add(panel);
        setJMenuBar(menu);
        menu.add(file);
        menu.add(utils);
        menu.add(view);
        file.add(open);
     //   file.add(save);
        view.add(viewData);
        view.add(resize);
        utils.add(copy);
        utils.add(cut);
        utils.add(replace);
        utils.add(delete);
        utils.add(find);

        // utils.addSeparator();
        file.addSeparator();

        tempFile.deleteOnExit();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private static JTextArea    createLimitedTextArea(int maxLength) {
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

    private static void limitLineLength(JTextArea textArea, int maxLength) throws BadLocationException {
        Document doc = textArea.getDocument();
        int lineCount = textArea.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            int lineStart = textArea.getLineStartOffset(i);
            int lineEnd = textArea.getLineEndOffset(i);
            int lineLength = lineEnd - lineStart - 1;
            if (lineLength > maxLength) {
                try {
                    doc.insertString(lineEnd - 1, "\n", null);
                } catch (BadLocationException e) {
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
/*            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    openFile(String.valueOf(fc.getSelectedFile().getAbsoluteFile()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }*/
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                table.loadData(fc.getSelectedFile());
                //                final SwingWorker<ArrayList<ArrayList<String>>, String> worker = new SwingWorker<>() {
//                    public final StringBuilder str = new StringBuilder();
//                    public final ArrayList<ArrayList<String>> data = new ArrayList<>();
//
//                    @Override
//                    protected ArrayList<ArrayList<String>> doInBackground() throws Exception {
//                        try (BufferedReader reader = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
//                            long fileSize = fc.getSelectedFile().length();
//                            long bytesRead = 0;
//                            String line;
//                            ArrayList<String> tmp = new ArrayList<>();
//                            for (int i = 0; i < 5; i++) {
//                                tmp.add((Integer.toHexString(i - 1)));
//                            }
//                            tmp.set(0, "");
//                            data.add(tmp);
//                            tmp = new ArrayList<>();
//                            tmp.add("0");
//                            while ((line = reader.readLine()) != null) {
//                                bytesRead += line.length();
//                                for (int i = 0; i < line.length(); i++) {
//                              //      System.out.print(line.charAt(i));
//                                    tmp.add(Integer.toHexString(line.charAt(i)));
//                                    if (tmp.size() == 5) {
//                                        data.add(tmp);
//                                        tmp = new ArrayList<>();
//                                        tmp.add(Integer.toHexString(data.size() - 1));
//                                    }
//                                }
//                           //     System.out.println();
//                           //     publish(String.valueOf(str));  // Опубликовать текущую строку
//                                setProgress((int) ((bytesRead * 100) / fileSize));
//                            }
//                            while(tmp.size()!=5)
//                            {
//                                tmp.add("");
//                            }
//                            data.add(tmp);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return data;
//
//
//                    }
//
//                    @Override
//                    protected void process(java.util.List<String> chunks) {
//
//                    }
//
//                    @Override
//                    protected void done() {
//                        // progressBar.setValue(100);
//                        //  JOptionPane.showMessageDialog(LargeFileReaderApp.this, "File loaded successfully!");
//                        System.out.println("done");
//                    }
//                };
//
//                worker.execute();
//                try {
////                    System.out.println("proverka swingworker");
////                    ArrayList<ArrayList<String>> result = worker.get();
////                    for (ArrayList<String> elem : result) {
////                        for (String str : elem) {
////                            System.out.print(str+" ");
////                        }
////                        System.out.println();
////                    }
//                    table.updateData(worker.get());
//                } catch (InterruptedException | ExecutionException ex) {
//                    ex.printStackTrace();
//                }
            }
        }
    };
/*    Action save = new AbstractAction("Save") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    };*/
    Action replace = new AbstractAction("insert and replace") {
        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel(new GridLayout(4, 3));
            JTextArea text1 = new JTextArea(2, 6);
            text1.setLineWrap(true);
            text1.setTabSize(8);
            text1.setAutoscrolls(false);
            text1.setBorder(BorderFactory.createTitledBorder("текст замены"));
            PlainDocument docHex = (PlainDocument) text1.getDocument();
            docHex.setDocumentFilter(new Filter());
            JSpinner spinnerRows = new JSpinner(new SpinnerNumberModel(0, 0, table.getNumRows() - 1, 1));
            spinnerRows.setBorder(BorderFactory.createTitledBorder("ряд"));

            JSpinner spinnerCols = new JSpinner(new SpinnerNumberModel(0, 0, table.getNumCols() - 2, 1));
            spinnerCols.setBorder(BorderFactory.createTitledBorder("столбец"));
            JCheckBox checkBox = new JCheckBox("insert with replace or not");
            JScrollPane scrol = new JScrollPane(text1);

            panel.add(checkBox);
            panel.add(spinnerCols);
            panel.add(spinnerRows);
            //panel.add(scrol);

            JOptionPane.showMessageDialog(null, panel);
                /*if (checkBox.isSelected())
                    System.out.println(spinnerCols.getValue());
                else System.out.println(spinnerRows.getValue());
                System.out.println("happy new year");
                */
            table.replace(checkBox.isSelected(), (Integer) spinnerRows.getValue() + 1, (Integer) spinnerCols.getValue() + 1, text1.getText());
        }
    };
    Action find = new AbstractAction("find") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("check resize");
            JPanel panel = new JPanel(new GridLayout(4, 3));
            JTextField textField = new JTextField();
            textField.setBorder(BorderFactory.createTitledBorder("введите текст для поиска"));
            panel.add(textField);
            JOptionPane.showMessageDialog(null, panel);
            table.find(textField.getText());
            System.out.println("all clear");
        }
    };
    Action resize = new AbstractAction("change № columns") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("check resize");
            JPanel panel = new JPanel(new GridLayout(4, 3));
            JTextField textField = new JTextField();
            textField.setBorder(BorderFactory.createTitledBorder("количество колонок"));
            panel.add(textField);
            JOptionPane.showMessageDialog(null, panel);
            System.out.println("change to " + Integer.parseInt(textField.getText()) + " cols");
            table.changeNumCols(Integer.parseInt(textField.getText()));
        }
    };
    Action delete = new AbstractAction("delete") {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (selection.isEmpty()) {
                JPanel panel = new JPanel(new GridLayout(4, 3));
                JSpinner spinnerRows = new JSpinner(new SpinnerNumberModel(0, 0, table.getNumRows() - 1, 1));
                spinnerRows.setBorder(BorderFactory.createTitledBorder("ряд"));

                JSpinner spinnerCols = new JSpinner(new SpinnerNumberModel(0, 0, table.getNumCols() - 2, 1));
                spinnerCols.setBorder(BorderFactory.createTitledBorder("столбец"));

                JSpinner spinnerBytes = new JSpinner(new SpinnerNumberModel(1, 0, table.getNumCols() * table.getNumRows() + table.getNumCols(), 1));
                spinnerBytes.setBorder(BorderFactory.createTitledBorder("число байтов для удаления"));

                JCheckBox checkBox = new JCheckBox("delete with replace or not");


                panel.add(checkBox);
                panel.add(spinnerCols);
                panel.add(spinnerRows);
                panel.add(spinnerBytes);


                JOptionPane.showMessageDialog(null, panel);

                table.delete(checkBox.isSelected(), (Integer) spinnerRows.getValue() + 1, (Integer) spinnerCols.getValue() + 1, (Integer) spinnerBytes.getValue());

            } else {
                JPanel panel = new JPanel(new GridLayout(4, 3));
                JCheckBox checkBox = new JCheckBox("insert with replace or not");
                panel.add(checkBox);
                JOptionPane.showMessageDialog(null, panel);
                int num = selection.get(1)[0] * (table.getNumCols() - 1) + selection.get(1)[1] - selection.get(0)[0] * (table.getNumCols() - 1) - selection.get(0)[1] + 1;
                //num=(selection.get(1)[0]-selection.get(0)[0]+1)*(selection.get(1)[1]-selection.get(0)[1]+1);
                System.out.println("num " + num);
                table.delete(checkBox.isSelected(), selection.get(0)[0], selection.get(0)[1], num);
            }
        }
    };
    Action copy = new AbstractAction("copy") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("copy");
            StringBuilder str = new StringBuilder();

            System.out.println(selection.get(0)[0] + "" + selection.get(0)[1] + "" + selection.get(1)[0] + "" + selection.get(1)[1]);
            int r = selection.get(0)[0], c = selection.get(0)[1];
//            for (int i = selection.get(0)[0]; i < selection.get(1)[0]; i++) {
//                for (int j = 0; j < selection.get(1)[1] - 1; j++) {//додумать логику как от одной ячейки до другой пройтись
            while (r * table.getNumRows() + c != selection.get(1)[0] * table.getNumRows() + selection.get(1)[1] + 1) {
                if (c > table.getColumnCount() - 1) {
                    r++;
                    c = 1;
                }
                System.out.println("r" + (r - 1) + "c" + (c - 1));
                str.append(table.getValueAt(r, c));
                c++;
                //}
            }
            StringSelection select = new StringSelection(str.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(select, select);
            selection.clear();
        }
    };
    Action cut = new AbstractAction("cut") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Cut");
            StringBuilder str = new StringBuilder();

            if (selection.get(0)[0].equals(selection.get(1)[0])) selection.sort(Comparator.comparingInt(o -> o[1]));
            else selection.sort(Comparator.comparingInt(o -> o[0]));
            System.out.println(selection.get(0)[0] + "" + selection.get(0)[1] + "" + selection.get(1)[0] + "" + selection.get(1)[1]);
            int r = selection.get(0)[0], c = selection.get(0)[1];
//            for (int i = selection.get(0)[0]; i < selection.get(1)[0]; i++) {
//                for (int j = 0; j < selection.get(1)[1] - 1; j++) {//додумать логику как от одной ячейки до другой пройтись
            while (r * table.getNumRows() + c != selection.get(1)[0] * table.getNumRows() + selection.get(1)[1] + 1) {
                if (c > table.getColumnCount() - 1) {
                    r++;
                    c = 1;
                }
                System.out.println("r" + (r - 1) + "c" + (c - 1));
                str.append(table.getValueAt(r, c));
                c++;
                //}
            }
            StringSelection select = new StringSelection(str.toString());
            int num = selection.get(1)[0] * (table.getNumCols() - 1) + selection.get(1)[1] - selection.get(0)[0] * (table.getNumCols() - 1) - selection.get(0)[1] + 1;
            //num=(selection.get(1)[0]-selection.get(0)[0]+1)*(selection.get(1)[1]-selection.get(0)[1]+1);
            System.out.println("num " + num);
            table.delete(false, selection.get(0)[0], selection.get(0)[1], num);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(select, select);
            selection.clear();
        }
    };
    Action viewData=new AbstractAction("view selected Data") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selection.isEmpty())
            {
                JOptionPane.showMessageDialog(null, "please, select data first");
            }
            else
            {
                StringBuilder dec=new StringBuilder();
                StringBuilder hex=new StringBuilder();
                StringBuilder bin =new StringBuilder();
                int r = selection.get(0)[0], c = selection.get(0)[1];
                while (r * table.getNumRows() + c != selection.get(1)[0] * table.getNumRows() + selection.get(1)[1] + 1) {
                    if (c > table.getColumnCount() - 1) {
                        r++;
                        c = 1;
                    }
                    StringBuilder tmp = new StringBuilder(table.getValueAt(r, c).toString());
                    while (tmp.length() < 2) {
                        tmp.insert(0, "0");
                    }
                    int intValue = Integer.parseInt(String.valueOf(tmp), 16);
                    dec.append( Character.toChars(intValue));
                    hex.append(tmp);
                    bin.append(Integer.toBinaryString(intValue));
                    c++;
                }
                JOptionPane.showMessageDialog(null,"decimal: "+dec+"\nhex: "+hex+"\nbinary: "+bin);
            }
        }
    };

/*
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
*/

/*    private void saveFile() {
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
    }*/
   /* class LimitedCellEditor extends DefaultCellEditor{
        private JTextField textField;
        public LimitedCellEditor(){
            super(new JTextField());
            textField=(JTextField) editorComponent;
            textField.setDocument(new LimitedDocument(2));

        }

    }

    private class LimitedDocument extends PlainDocument {
        private int limit;
        public LimitedDocument(int i) {
        limit=i;
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str==null) return;
            if((getLength())+str.length()<=limit){
            super.insertString(offs, str, a);}
            else {
                int reminder=limit-getLength();
                if(reminder>0){
                    super.insertString(offs,str.substring(0,reminder),a);
                }
            }
        }
    }*/
/*   static class LimitedCellEditor extends DefaultCellEditor {
       private JTextField textField;
       private JTable table;

       public LimitedCellEditor() {
           super(new JTextField());
           textField = (JTextField) editorComponent;
           textField.setDocument(new LimitedDocument(2)); // Ограничение на 2 символа

           textField.addKeyListener(new KeyListener() {
               @Override
               public void keyTyped(KeyEvent e) {
                   // Обработка события ввода символа
               }

               @Override
               public void keyPressed(KeyEvent e) {
                   // Обработка события нажатия клавиши
               }

               @Override
               public void keyReleased(KeyEvent e) {
                   if (textField.getText().length() >= 2) {
                       moveCursor();
                   }
               }
           });
       }

       private void moveCursor() {
           SwingUtilities.invokeLater(() -> {
               int editingRow = table.getEditingRow();
               int editingColumn = table.getEditingColumn();
               if (editingRow != -1 && editingColumn != -1) {
                   if (editingColumn < table.getColumnCount() - 1) {
                       table.changeSelection(editingRow, editingColumn + 1, false, false);
                   } else if (editingRow < table.getRowCount() - 1) {
                       table.changeSelection(editingRow + 1, 0, false, false);
                   }
               }
           });
       }

       @Override
       public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
           this.table = table;
           return super.getTableCellEditorComponent(table, value, isSelected, row, column);
       }
   }

    // Документ для ограничения количества символов в JTextField
    static class LimitedDocument extends PlainDocument {
        private int limit;

        public LimitedDocument(int limit) {
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            System.out.println("asd");
            if (str == null) return;

            if ((getLength() + str.length()) <= limit) {
                System.out.println("less");
                super.insertString(offset, str, attr);
            } else {
                System.out.println("more");
                int remainder = limit - getLength();
                if (remainder > 0) {
                    System.out.println("remainder >0");
                    super.insertString(offset, str.substring(0, remainder), attr);
                }
            }
        }
    }*/
}
