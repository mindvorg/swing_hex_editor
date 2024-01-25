package com.company.table;

import com.company.model.Model;
import org.riversun.bigdoc.bin.BigFileSearcher;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class MyJTable extends JTable implements ListSelectionListener {
    private Model model;
    private RandomAccessFile rafFile;
    private String path;


    private int numPage = 0;

    private boolean fileIsOpen = false;

    public MyJTable(File tempFile) {
        super(new Model(4, 5));
        try {
            path = tempFile.getPath();
            rafFile = new RandomAccessFile(tempFile.getAbsolutePath(), "rw");
            tempFile.deleteOnExit();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        model = (Model) getModel();
        setCellSelectionEnabled(true);

        for (int i = 1; i < model.getRowCount(); i++) {
            for (int j = 1; j < model.getColumnCount(); j++) {
                JTextField textField = new JTextField();
                textField.setDocument(new JTextFieldLimit(2));
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setFont(new Font("Arial", Font.PLAIN, 12));

                setDefaultEditor(Object.class, new DefaultCellEditor(textField));
            }
        }
        moveNext();
    }


    public void setLoad(File file) {
        try {
            path = file.getPath();
            rafFile = new RandomAccessFile(file, "rw");
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileIsOpen = true;
        numPage = 0;
        loadData();
        transferFocus();
        model.fireTableStructureChanged();
    }

    private void updateData() throws IOException {//when we have new page after writing
        writeDataToFile();
        model.counterPlus();
        numPage++;
        if (fileIsOpen && rafFile.getFilePointer() < rafFile.length())
            loadData();
        else
            model.clear();
        transferFocus();
        model.fireTableStructureChanged();
    }

    public void writeDataToFile() {
        ArrayList<ArrayList<Byte>> tmp = model.getData();
        try {
            rafFile.seek(17 * (getNumCols() - 1) * numPage);
            for (ArrayList<Byte> list : tmp) {
                list.remove(0);
                byte[] array = new byte[getNumCols() - 1];
                for (int i = 0; i < array.length; i++) {
                    array[i] = list.get(i);
                }
                rafFile.write(array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            rafFile.seek(17 * (getNumCols() - 1) * numPage);
            ArrayList<ArrayList<Byte>> newData = new ArrayList<>();
            ArrayList<Byte> tmp = new ArrayList<>();
            for (int i = 0; i < model.getColumnCount(); i++) {
                tmp.add((byte) (i - 1));
            }
            tmp.set(0, (byte) 0);
            newData.add(tmp);
            tmp = new ArrayList<>();
            tmp.add((byte) (17 * numPage));
            int numOperations = (int) Math.min(rafFile.length() - rafFile.getFilePointer(), (getNumCols() - 1) * 17);
            for (int i = 0; i < numOperations; i++) {
                byte elem;
                elem = rafFile.readByte();
                tmp.add(elem);
                if (tmp.size() == model.getColumnCount()) {
                    newData.add(tmp);
                    tmp = new ArrayList<>();
                    tmp.add((byte) ((newData.size() - 1) + 17 * numPage));
                }
            }
            if (tmp.size() > 1) {
                while (tmp.size() != model.getColumnCount()) {
                    tmp.add((byte) 0);
                }
                newData.add(tmp);
            }
            model.setData(newData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveNext() {
        try {
            if (rafFile.getFilePointer() < rafFile.length()) {
                writeDataToFile();
                model.counterPlus();
                numPage++;
                loadData();
                transferFocus();
                model.fireTableStructureChanged();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void movePrev() {

        if (numPage > 0) {
            writeDataToFile();
            model.counterMinus();
            numPage--;
            loadData();
            transferFocus();
            model.fireTableStructureChanged();
        }
    }

    public int getNumCols() {
        return model.getColumnCount();
    }

    public int getNumRows() {
        return model.getRowCount();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int row = rowAtPoint(event.getPoint());
        int col = columnAtPoint(event.getPoint());

        StringBuilder tmp = new StringBuilder(getValueAt(row, col).toString());
        while (tmp.length() < 2) {
            tmp.insert(0, "0");
        }
        int intValue = Integer.parseInt(tmp.toString(), 16);
        char[] charArray = Character.toChars(intValue);
        return "hex: " + tmp + " normal:" + String.valueOf(charArray);
    }

    public void changeNumCols(int newNumCol) {
        writeDataToFile();
        model.resize(newNumCol);
        try {
            numPage = (int) (rafFile.getFilePointer() / (17 * newNumCol));
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadData();
        model.fireTableStructureChanged();
    }

    public boolean find(String text) {
        writeDataToFile();
        if (text.length() % 2 != 0) text += 0;
        byte[] bytes = new byte[text.length() / 2];
        for (int i = 0; i < text.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(text.substring(i, i + 2), 16);
        }
        BigFileSearcher searcher = new BigFileSearcher();
        List<Long> findList = searcher.searchBigFile(new File(path), bytes);
        if (!findList.isEmpty()) {
            numPage = (int) (findList.get(0) / (17 * getNumCols()));
            Long pos = findList.get(0);
            pos -= numPage * 17 * (getNumCols() - 1);
            loadData();
            model.fireTableDataChanged();
            changeSelection((int) (pos / (getNumCols() - 1)) + 1, (int) (pos % (getNumCols() - 1)) + 1, false, false);
            return true;
        } else {
            return false;
        }
    }

    public void delete(boolean selected, int row, int col, Integer numBytes) {
        writeDataToFile();
        if (selected) {//delete with replace
            try {
                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
                for (int i = 0; i < numBytes; i++) {
                    rafFile.write((byte) 0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadData();

        } else {//delete with shift
            int position = numPage * 17 + row * (getNumCols() - 1) + col;
            try {
                int len = (int) rafFile.length();
                for (int i = (position + numBytes); i < len; i++) {
                    rafFile.seek(i);
                    byte b = rafFile.readByte();
                    rafFile.seek(i - numBytes);
                    rafFile.write(b);
                }
                rafFile.setLength(len - numBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadData();
        model.fireTableDataChanged();

    }

    public void insertReplace(boolean selected, int row, int col, String text) {
        row--;
        col--;
        writeDataToFile();
        if (text.length() % 2 != 0) text += 0;
        ArrayList<Byte> replaceData = new ArrayList<>();
        for (int i = 0; i < text.length(); i += 2) {
            replaceData.add((byte) Integer.parseInt(text.substring(i, i + 2), 16));
        }
        if (selected) {//insert with replace
            try {
                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
                for (Byte replaceDatum : replaceData) {
                    rafFile.write(replaceDatum);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {// insert with shift
            if (col == getNumCols() && row == getNumRows())
                col--;
            if (row > getRowCount() - 1) {
                col = getNumCols() - 1;
                row--;
            }
            try {
                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
                byte[] bytes = new byte[replaceData.size()];
                rafFile.read(bytes);
                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
                for (byte b : replaceData) {
                    rafFile.write(b);
                }

                for (long i = rafFile.getFilePointer(); i < rafFile.length(); i = i + replaceData.size()) {
                    byte[] tmp = new byte[replaceData.size()];

                    rafFile.read(tmp);

                    rafFile.seek(i);

                    rafFile.write(bytes);

                    bytes = tmp;
                }
                rafFile.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadData();
        model.fireTableDataChanged();
    }


    // Class to input only 2 symbols
    class JTextFieldLimit extends PlainDocument {
        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString(int offset, String str, AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            if (str.matches("[0-9a-fA-F]")) {
                if ((getLength() + str.length()) <= limit) {
                    super.insertString(offset, str, attr);
                } else {
                    if (editingColumn < getColumnCount() - 1) {//next cell
                        changeSelection(editingRow, editingColumn + 1, false, false);
                        editCellAt(editingRow, editingColumn + 1);

                    } else if (editingRow < getRowCount() - 1) {//next row
                        changeSelection(editingRow + 1, 1, false, false);
                        editCellAt(editingRow + 1, 1);

                    } else if (editingRow == 17) { //limit number of rows in 1 page
                        changeSelection(1, 1, false, false);
                        editCellAt(1, 1);
                        model.fireTableCellUpdated(16, 4);
                        try {
                            updateData();//if file is open need to upload data to the next page
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        changeSelection(1, 1, false, false);
                        editCellAt(1, 1);

                    } else {//add row
                        model.addRow();
                        model.fireTableRowsInserted(0, getRowCount());
                        changeSelection(editingRow + 1, 1, false, false);
                        editCellAt(editingRow + 1, 1);
                    }
                    transferFocus();
                    super.insertString(0, str, attr);
                }
                model.fireTableCellUpdated(editingRow, editingColumn);
            }
        }
    }

}