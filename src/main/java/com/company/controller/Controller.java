package com.company.controller;

import com.company.filter.Filter;
import com.company.table.MyJTable;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;

public class Controller extends JFrame {

    private File tempFile;

    {
        try {
            tempFile = File.createTempFile("hello", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RandomAccessFile raf;

    private JPanel panel = new JPanel(new BorderLayout(3, 3));
    private MyJTable table = new MyJTable(tempFile);
    private JFileChooser fc = new JFileChooser();
    JPanel panelTable = new JPanel();
    private JScrollPane scroll = new JScrollPane(panelTable);
    private ArrayList<Integer[]> selection = new ArrayList<>();
    private JMenu file = new JMenu("File");
    private JMenu utils = new JMenu("utils");
    private JMenu view = new JMenu("view");
    private JMenuBar menu = new JMenuBar();


    public Controller() throws HeadlessException {

        super("Hex Editor");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(screenSize.width / 2 - 400, screenSize.height / 2 - 200, 800, 400);

        tempFile.deleteOnExit();
        panel.setAutoscrolls(false);

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
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row != -1 && col != -1) {
                    selection.add(new Integer[]{row, col});
                }
                if (selection.get(0)[0].equals(selection.get(1)[0])) selection.sort(Comparator.comparingInt(o -> o[1]));
                else selection.sort(Comparator.comparingInt(o -> o[0]));
                if ((selection.get(0)[0] != selection.get(1)[0] || selection.get(0)[1] != selection.get(1)[1]))
                    table.clearSelection();
            }
        };
        table.addMouseListener(listener);
        panelTable.add(table);
        JButton next = new JButton("↓");
        next.addActionListener(e -> table.moveNext());
        JButton prev = new JButton("↑");
        prev.addActionListener(e -> table.movePrev());
        JButton clearSelection = new JButton("clearSelection");
        clearSelection.addActionListener(e -> selection.clear());
        JPanel panelButtons = new JPanel();
        panelButtons.add(clearSelection, BorderLayout.WEST);
        panelButtons.add(prev);
        panelButtons.add(next);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelButtons, BorderLayout.SOUTH);
        add(panel);
        setJMenuBar(menu);
        menu.add(file);
        menu.add(utils);
        menu.add(view);
        file.add(open);
        file.add(save);
        view.add(viewData);
        view.add(resize);
        utils.add(copy);
        utils.add(cut);
        utils.add(replace);
        utils.add(delete);
        utils.add(find);
        file.addSeparator();

        tempFile.deleteOnExit();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    Action open = new AbstractAction("Open") {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File originalFile = new File(fc.getSelectedFile().getPath());
                try {
                    tempFile = File.createTempFile(String.valueOf(fc.getSelectedFile()), "");
                    Files.copy(originalFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    tempFile.deleteOnExit();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                tempFile.deleteOnExit();


                table.setLoad(tempFile);

            }
        }
    };
    Action save = new AbstractAction("Save") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                table.writeDataToFile();
                File originalFile = new File(fc.getSelectedFile().getPath());
                try {
                    Files.copy(tempFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
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
            JSpinner spinnerRows = new JSpinner(new SpinnerNumberModel(0, 0, table.getNumRows() - 2, 1));
            spinnerRows.setBorder(BorderFactory.createTitledBorder("ряд"));

            JSpinner spinnerCols = new JSpinner(new SpinnerNumberModel(0, 0, table.getNumCols() - 2, 1));
            spinnerCols.setBorder(BorderFactory.createTitledBorder("столбец"));
            JCheckBox checkBox = new JCheckBox("insert with replace or not");
            JScrollPane scroll = new JScrollPane(text1);
            panel.add(checkBox);
            panel.add(spinnerCols);
            panel.add(spinnerRows);
            panel.add(scroll);
            int result = JOptionPane.showConfirmDialog(null, panel, "enter info", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION)
                table.insertReplace(checkBox.isSelected(), (Integer) spinnerRows.getValue() + 1, (Integer) spinnerCols.getValue() + 1, text1.getText());
        }
    };
    Action find = new AbstractAction("find") {
        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel(new GridLayout(4, 3));
            JTextField textField = new JTextField();
            PlainDocument docHex = (PlainDocument) textField.getDocument();
            docHex.setDocumentFilter(new Filter());
            textField.setBorder(BorderFactory.createTitledBorder("введите текст для поиска"));
            panel.add(textField);
            int result = JOptionPane.showConfirmDialog(null, panel, "enter info", JOptionPane.YES_NO_OPTION);
            boolean flag;
            if (result == JOptionPane.OK_OPTION) {
                flag = table.find(textField.getText());
                if (!flag) JOptionPane.showMessageDialog(null, "not found");
            }
        }
    };
    Action resize = new AbstractAction("change № columns") {
        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel(new GridLayout(4, 3));
            JTextField textField = new JTextField();
            textField.setBorder(BorderFactory.createTitledBorder("количество колонок"));
            panel.add(textField);
            int result = JOptionPane.showConfirmDialog(null, panel, "enter info", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION)
                table.changeNumCols(Integer.parseInt(textField.getText()));
        }
    };
    Action delete = new AbstractAction("delete") {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (!selection.isEmpty()) {
                JPanel panel = new JPanel(new GridLayout(4, 3));
                JCheckBox checkBox = new JCheckBox("insert with replace or not");
                panel.add(checkBox);
                int result = JOptionPane.showConfirmDialog(null, panel, "are you sure", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    int num = selection.get(1)[0] * (table.getNumCols() - 1) + selection.get(1)[1] - selection.get(0)[0] * (table.getNumCols() - 1) - selection.get(0)[1] + 1;
                    table.delete(checkBox.isSelected(), selection.get(0)[0], selection.get(0)[1], num);
                }
            } else {
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
                int result = JOptionPane.showConfirmDialog(null, panel, "enter info", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.OK_OPTION)
                    table.delete(checkBox.isSelected(), (Integer) spinnerRows.getValue(), (Integer) spinnerCols.getValue(), (Integer) spinnerBytes.getValue());

            }
        }
    };
    Action copy = new AbstractAction("copy") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selection.isEmpty()) JOptionPane.showMessageDialog(null, "empty selection");
            else {
                StringBuilder str = new StringBuilder();
                int r = selection.get(0)[0], c = selection.get(0)[1];
                while (r * table.getNumRows() + c != selection.get(1)[0] * table.getNumRows() + selection.get(1)[1] + 1) {
                    if (c > table.getColumnCount() - 1) {
                        r++;
                        c = 1;
                    }
                    str.append(table.getValueAt(r, c));
                    c++;
                }
                StringSelection select = new StringSelection(str.toString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(select, select);
                selection.clear();
            }
        }
    };
    Action cut = new AbstractAction("cut") {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder str = new StringBuilder();
            if (selection.isEmpty()) JOptionPane.showMessageDialog(null, "empty selection");
            else {
                if (selection.get(0)[0].equals(selection.get(1)[0])) selection.sort(Comparator.comparingInt(o -> o[1]));
                else selection.sort(Comparator.comparingInt(o -> o[0]));
                int r = selection.get(0)[0], c = selection.get(0)[1];
                while (r * table.getNumRows() + c != selection.get(1)[0] * table.getNumRows() + selection.get(1)[1] + 1) {
                    if (c > table.getColumnCount() - 1) {
                        r++;
                        c = 1;
                    }
                    str.append(table.getValueAt(r, c));
                    c++;
                }
                StringSelection select = new StringSelection(str.toString());
                int num = selection.get(1)[0] * (table.getNumCols() - 1) + selection.get(1)[1] - selection.get(0)[0] * (table.getNumCols() - 1) - selection.get(0)[1] + 1;
                table.delete(false, selection.get(0)[0], selection.get(0)[1], num);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(select, select);
                selection.clear();
            }
        }
    };
    Action viewData = new AbstractAction("view selected Data") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selection.isEmpty()) {
                JOptionPane.showMessageDialog(null, "please, select data first");
            } else {
                StringBuilder dec = new StringBuilder();
                StringBuilder hex = new StringBuilder();
                StringBuilder bin = new StringBuilder();
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
                    dec.append(Character.toChars(intValue));
                    hex.append(tmp);
                    bin.append(Integer.toBinaryString(intValue));
                    c++;
                }
                JOptionPane.showMessageDialog(null, "decimal: " + dec + "\nhex: " + hex + "\nbinary: " + bin);
            }
        }
    };

}
