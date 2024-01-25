package com.company.gigachatTable;

import com.company.video.Model;
import org.riversun.bigdoc.bin.BigFileSearcher;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MyJTable extends JTable implements ListSelectionListener {
    private Model model;
    private RandomAccessFile rafFile;
    private String path;


    private int numPage = 0;
    private int curIndex = 0;
    private boolean fileIsOpen = false;
    private int limitRows = 16;

    public MyJTable(File tempFile) {

        //   super(new DefaultTableModel(8, 8));

        // model = (DefaultTableModel) getModel();
        super(new Model(4, 5));
        try {
            path=tempFile.getPath();
            rafFile = new RandomAccessFile(tempFile.getAbsolutePath(), "rw");
            tempFile.deleteOnExit();
            System.out.println(tempFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(getColumnCount() + getRowCount());
        model = (Model) getModel();
        setCellSelectionEnabled(true);

        for (int i = 1; i < model.getRowCount(); i++) {
            for (int j = 1; j < model.getColumnCount(); j++) {
                JTextField textField = new JTextField();
                textField.setDocument(new JTextFieldLimit(2)); // Ограничение символов до 2
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setFont(new Font("Arial", Font.PLAIN, 12));

                setDefaultEditor(Object.class, new DefaultCellEditor(textField));
            }
        }
        moveNext();//можно либо подавать новую модель каждый раз и хранить отдельно старую, но смысл тогда в этом всём
        //надо посмотреть как создавать временный файл и в него записывать и обнулять тогда просто JTable, а при переключении кнопок
    }

    //    public MyJTable(ArrayList<ArrayList<String>> data) {
//        //   super(new DefaultTableModel(8, 8));
//
//        // model = (DefaultTableModel) getModel();
//        //super(new Model(5, 5));
//        super(new Model(data));
//        System.out.println(getColumnCount()+getRowCount());
//        model = (Model) getModel();
//        setCellSelectionEnabled(true);
//
//        for (int i = 1; i < model.getRowCount(); i++) {
//            for (int j = 1; j < model.getColumnCount(); j++) {
//                JTextField textField = new JTextField();
///* PlainDocument docHex = (PlainDocument) textArea_hex.getDocument();
//        docHex.setDocumentFilter(new HexFilter());*/
////                PlainDocument doc=(PlainDocument) textField.getDocument();
////                doc.setDocumentFilter(new Filter());
//                textField.setDocument(new JTextFieldLimit(2)); // Ограничение символов до 2
//                textField.setHorizontalAlignment(JTextField.CENTER);
//                textField.setFont(new Font("Arial", Font.PLAIN, 12));
//
//                //     textField.setBorder(BorderFactory.createEmptyBorder());
////                textField.addKeyListener(new java.awt.event.KeyAdapter() {
////                    public void keyTyped(KeyEvent e) {
////                        System.out.println("typed");
////                        if (textField.getText().length() >= 2) {
////                            System.out.println("before consume");
////                            e.consume();  // Запрет ввода больше 2 символов
////                            System.out.println("after consume");
////                            int row = getSelectedRow();
////                            int col = getSelectedColumn();
////                            if (row + 1 >= getRowCount()) {
////                                System.out.println("37");
////                                changeSelection(row + 1, col, false, false);
////                            }
////                        }
////                    }
////                });
//                setDefaultEditor(Object.class, new DefaultCellEditor(textField));
//            }
//        }
//    }
    public void updateData(ArrayList<ArrayList<String>> input) {
        //model.updateData(input);
        model.fireTableDataChanged();
    }

    public void setLoad(File file) {

        try {
            path=file.getPath();
            rafFile = new RandomAccessFile(file, "rw");
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileIsOpen = true;
        curIndex = 0;
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
        System.out.println(model.toString());
        transferFocus();
        model.fireTableStructureChanged();
        // System.out.println(rafFile);
    }

    private void writeDataToFile() {
        System.out.println(model.toString());
        ArrayList<ArrayList<Byte>> tmp = model.getData();
        try {
            rafFile.seek(17 * (getNumCols() - 1) * numPage);
         //   System.out.println(rafFile.getFilePointer());
            for (ArrayList<Byte> list : tmp) {
                //         System.out.println(list.toString());
                list.remove(0);
         //       System.out.println(list.toString());
                byte[] array = new byte[getNumCols() - 1];
                for (int i = 0; i < array.length; i++) {
                    array[i] = list.get(i);
                }
                //   System.out.println(Arrays.toString(array));


                //  System.out.println(rafFile.getFilePointer());
                rafFile.write(array);
                // System.out.println(rafFile.getFilePointer());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            rafFile.seek(17 * (getNumCols() - 1) * numPage);
            System.out.println("file pointer:" + rafFile.getFilePointer());
            //тут делать сик, чтобы просто подгружать определенную страницу сделать один фор через мин либо размер либо 64
            ArrayList<ArrayList<Byte>> newData = new ArrayList<>();
            ArrayList<Byte> tmp = new ArrayList<>();
            for (int i = 0; i < model.getColumnCount(); i++) {
                tmp.add((byte) (i - 1));
            }
            tmp.set(0, (byte) 0);
            newData.add(tmp);
            tmp = new ArrayList<>();
            tmp.add((byte) (17 * numPage));
            int numOperations = (int) Math.min(rafFile.length() - rafFile.getFilePointer(), (getNumCols() - 1) * 17);//разобраться почему добавляется 17ая строчка, проверить как будет работать следующая подгрузка, если файла больше нет
            System.out.println("num" + numOperations);
            for (int i = 0; i < numOperations; i++) {
                byte elem;
                elem = rafFile.readByte();
                System.out.print(elem);
                tmp.add(elem);
                if (tmp.size() == model.getColumnCount()) {
                    System.out.println();
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
            /*if (rafFile.length() > 64)//we have more than 1 data model
            {
                for (int i = 0; i < 64; i++) {
                    byte elem;
                    elem = rafFile.readByte();
                    System.out.print(elem);
                    tmp.add(elem);
                    if (tmp.size() == model.getColumnCount()) {
                        System.out.println();
                        newData.add(tmp);
                        tmp = new ArrayList<>();
                        tmp.add((byte) (newData.size() - 1));
                    }
                }
            } else {
                for (int i = 0; i < rafFile.length(); i++) {
                    byte elem;
                    elem = rafFile.readByte();
                    System.out.print(elem);

                    tmp.add(elem);
                    if (tmp.size() == model.getColumnCount()) {
                        System.out.println();

                        newData.add(tmp);
                        tmp = new ArrayList<>();
                        tmp.add((byte) (newData.size() - 1));
                    }
                }
                while (tmp.size() != model.getColumnCount()) {
                    tmp.add((byte) 0);
                }
                newData.add(tmp);
            }*/
            model.setData(newData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveNext() {
//        ArrayList<ArrayList<Byte>> visibleRows = getVisibleRows(curIndex + 10, Math.min(model.getRowCount(), curIndex + 10));
//        setRowSelectionInterval(0, visibleRows.size());
//        curIndex = Math.min(model.getRowCount(), curIndex + 10);
//        model.fireTableDataChanged();
        try {

            if (rafFile.getFilePointer() < rafFile.length()) {
                System.out.println("move next");
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
//        ArrayList<ArrayList<Byte>> visibleRows = getVisibleRows(curIndex - 10, Math.min(model.getRowCount(), curIndex - 10));
//        setRowSelectionInterval(0, visibleRows.size());
//        curIndex = Math.min(model.getRowCount(), curIndex - 10);
//        model.fireTableDataChanged();
        if (numPage > 0) {
            System.out.println("move pref");
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

//        if (tmp.length()==1) tmp.insert(0, "0");
        int intValue = Integer.parseInt(tmp.toString(), 16);
        char[] charArray = Character.toChars(intValue);
        return "hex: " + tmp + " normal:" + String.valueOf(charArray);
    }

    public void changeNumCols(int newNumCol) {//working with raf
        /*  if(!fileIsOpen) */
        /*
         * я нахожусь на какой-то позиции каретки
         * rafFile.getFilePointer()
         * начало страницы, первый pointer= 17 * (getNumCols() - 1) * numPage
         * pointer/17 * (NEWgetNumCols() - 1)=newNumPage
         * */

        writeDataToFile();
        model.resize(newNumCol);
        try {
            numPage = (int) (rafFile.getFilePointer() / (17 * newNumCol));
            System.out.println("new numPage" + numPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadData();
        System.out.println(model.toString());
        model.fireTableStructureChanged();
    }

    public void find(String text) {
        if (text.length() % 2 != 0) text += 0;
        //ArrayList<Byte> replaceData = new ArrayList<>();
        byte[] bytes =new byte[text.length()/2];
        for (int i = 0; i < text.length(); i+=2) {
            bytes[i/2]=(byte) Integer.parseInt(text.substring(i, i + 2), 16);
        }
        System.out.println(Arrays.toString(bytes));


        writeDataToFile();
        System.out.println("find start");
        System.out.println(path);
        try {
            byte[] searchBytes=text.getBytes("UTF-8");
            BigFileSearcher searcher=new BigFileSearcher();
            List<Long> findList= searcher.searchBigFile(new File(path),bytes);
            System.out.println("find:"+findList);
            if(!findList.isEmpty())
            {
                numPage = (int) (findList.get(0) / (17*getNumCols()));
                System.out.println(numPage);
                Long pos=findList.get(0);
                pos-=numPage*17*(getNumCols()-1);
                System.out.println((int) (pos/(getNumCols()-1)));
                System.out.println((int) (pos%(getNumCols()-1)));

                loadData();
                model.fireTableDataChanged();
                   changeSelection( (int) (pos/(getNumCols()-1))+1,(int) (pos%(getNumCols()-1))+1,false,false);
               //changeSelection(2,2,false,false);
                // transferFocus();

            }
            else{
                System.out.println("not found");
            }

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
            //byte[] bytes=text.getBytes("utf-8");

        //     result = model.findInData(text);



        System.out.println("find start");

       // clearSelection();
        //    changeSelection(result[0], result[1], false, false);
        // setValueAt(getValueAt(result[0], result[1]),result[0], result[1]);
//        editCellAt(1, 3);
//        transferFocus();
        //model.fireTableCellUpdated(result[0], result[1]);

    }

    public void delete(boolean selected, int row, int col, Integer numBytes) {
        writeDataToFile();
        if (selected) {//insert with replace
//            row++;
//            col++;
            System.out.println("delete with replace");
            System.out.println(numBytes);
//            for (int i = 0; i < numBytes; i++) {
//                if (col > model.getColumnCount() - 1) {
//                    row++;
//                    col = 1;
//                }
//                model.setValueAt("", row, col);
//                col++;
//            }
            try {
                rafFile.seek(row * (getNumCols() - 1) + col);
                System.out.println(rafFile.getFilePointer());

                for (int i = 0; i < numBytes; i++) {
                    rafFile.seek(rafFile.getFilePointer() - 1);
                    rafFile.write((byte) 0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadData();

        } else {//insert and move data
            /*if (col == getNumCols() && row == getNumRows())
                col--;
            if (row > getRowCount() - 1) {
                col = getNumCols() - 1;
                row--;
            }
            */
            //model.deleteWithShift(numBytes, row, col);
            int position = row * (getNumCols() - 1) + col;
            System.out.println("pos" + position);
            // Количество байтов, которые нужно удалить
            // Сдвиг всех байтов после удаленных байтов на нужное количество позиций влево
            try {
                int len = (int) rafFile.length();
                for (int i = (position + numBytes); i < len; i++) {
                    System.out.print("-");
                    rafFile.seek(i);
                    byte b = rafFile.readByte();
                    //        System.out.println("byte:"+b);
                    rafFile.seek(i - numBytes);
                    rafFile.write(b);
                }
                System.out.println("new len" + (len - numBytes));
                System.out.println("len" + len);
                rafFile.setLength(len - numBytes);
//                if (position >= rafFile.length()) position = rafFile.length() - 1;
//                rafFile.seek(position + numBytes);
//                while ((bytesRead = rafFile.read(buffer)) != -1) {
//                    rafFile.seek(rafFile.getFilePointer() - bytesRead - numBytes);
//                    rafFile.write(buffer, 0, bytesRead);
//                    rafFile.seek(rafFile.getFilePointer() + numBytes);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadData();
        model.fireTableDataChanged();
        /*↑↓→←*/
    }

    public void insertReplace(boolean selected, int row, int col, String text) {
        row--;
        col--;
        System.out.println(row);
        System.out.println(col);
        System.out.println(text);
        writeDataToFile();
        if (text.length() % 2 != 0) text += 0;
        ArrayList<Byte> replaceData = new ArrayList<>();
        for (int i = 0; i < text.length(); i += 2) {
            replaceData.add((byte) Integer.parseInt(text.substring(i, i + 2), 16));
        }
        System.out.println(replaceData.toString());
        if (selected) {//insert with replace

            try {
                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
                for (Byte replaceDatum : replaceData) {
                    rafFile.write(replaceDatum);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {// insert and move data
            if (col == getNumCols() && row == getNumRows())
                col--;
            if (row > getRowCount() - 1) {
                col = getNumCols() - 1;
                row--;
            }
            try {
//                rafFile.setLength(rafFile.length() + replaceData.size());
//                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
//                byte[] bytes = new byte[(int) (rafFile.length() - numPage * 17 + row * (getNumCols() - 1) + col)];
//                rafFile.read(bytes);
//
//                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
//                for (byte b : replaceData) {
//                    rafFile.write(b);
//
//
//                }
//                rafFile.write(bytes);//попробовать оставить что ниже написано, но взять массив из 1024 байтов, например, считывать и записывать поверх, немного должно ускорить программу


                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
                byte[] bytes=new byte[replaceData.size()];
                rafFile.read(bytes);
                rafFile.seek(numPage * 17 + row * (getNumCols() - 1) + col);
                for (byte b : replaceData) {
                    rafFile.write(b);
                }


                for (long i = rafFile.getFilePointer(); i <rafFile.length(); i=i+replaceData.size()) {
                    byte[] tmp=new byte[replaceData.size()];
             //       System.out.print(rafFile.getFilePointer()+" | ");
                    rafFile.read(tmp);
               //     System.out.print(rafFile.getFilePointer()+" | ");
                    rafFile.seek(i);
                 //   System.out.print(rafFile.getFilePointer()+" | ");
                    rafFile.write(bytes);
                   // System.out.println(rafFile.getFilePointer()+" | ");
                    bytes=tmp;
                }
                rafFile.write(bytes);


                /*for (int i = row * (getNumCols() - 1) + col; i < rafFile.length() - replaceData.size(); i = i + replaceData.size()) {
                    for (int j = 0; j < replaceData.size(); j++) {
                        byte b = rafFile.readByte();
                        rafFile.seek(rafFile.getFilePointer() - 1);
                        rafFile.write(replaceData.get(j));
                        System.out.println(rafFile.getFilePointer());//разобраться на этом моменте
                        replaceData.set(j, b);
                    }
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
            //        model.insertWithShift(replaceData, row, col);
        }
        loadData();
        model.fireTableDataChanged();
    }


    public void mark(ArrayList<Integer[]> list) {
        int r = list.get(0)[0], c = list.get(0)[1];
        while (r * getNumRows() + c != list.get(1)[0] * getNumRows() + list.get(1)[1] + 1) {
            if (c > getColumnCount() - 1) {
                r++;
                c = 1;
            }
            changeSelection(r, c, true, true);
            c++;
            //}
        }
        ListSelectionModel selectionModel = this.getSelectionModel();
//           selectionModel.setAnchorSelectionIndex(list.get(0)[0]);
//           selectionModel.setSelectionInterval(list.get(0)[0],list.get(1)[0]);
        //selectionModel.removeSelectionInterval();
        //        super.setValueAt(Color.RED,0,0);
        //        super.setValueAt(Color.RED,1,1);
        //        changeSelection(1,1,true,true);
        //changeSelection(list.get(0)[0],list.get(0)[1],false,true );
        //changeSelection(list.get(1)[0],list.get(1)[1],false,true);
    }

    public String copyCut() {
        return "asd";
    }


    public ArrayList<ArrayList<Byte>> getVisibleRows(int startIndex, int endIndex) {
        ArrayList<ArrayList<Byte>> visibleRows = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            ArrayList<Byte> row = model.getRow(i);
            visibleRows.add(row);
        }
        return visibleRows;
    }


    // Класс для ограничения символов до 2
    class JTextFieldLimit extends PlainDocument {
        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        //jformatjtextfield
        public void insertString(int offset, String str, AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            if (str.matches("[0-9a-fA-F]")) {
                if ((getLength() + str.length()) <= limit) {
//                    System.out.println("offset:"+offset+"str:"+str+"attr"+attr);
//                    System.out.println("row:"+editingRow+"col:"+editingColumn);

                    //      if(model.getValueAt(editingRow,editingColumn).toString().isEmpty())

                    super.insertString(offset, str, attr);
                    //          System.out.println(model.getValueAt(editingRow, editingColumn));
                    //      System.out.println("insert");
//                    lastR=editingRow;
//                    lastC=editingColumn;
                    //        else {super.replace(offset,1,str,attr);}
//                    System.out.println("length:"+getLength());
//                    System.out.println("str.length:"+str.length());
                }
                /*     if (getLength() == 2) */
                else {
                    if (editingColumn < getColumnCount() - 1) {//переход на следующую ячейку
                        //        System.out.println("next");
                        changeSelection(editingRow, editingColumn + 1, false, false);
                        //    model.setValueAt(str, editingRow, editingColumn +1);
                        editCellAt(editingRow, editingColumn + 1);

                    } else if (editingRow < getRowCount() - 1) {//переход на новую строку
                        //    System.out.println("data" + model.getValueAt(editingRow + 1, 1));
                        //      System.out.println("next row");

                        changeSelection(editingRow + 1, 1, false, false);
                        //      model.setValueAt(str, editingRow + 1, 1);
                        editCellAt(editingRow + 1, 1);
                    } else if (editingRow == 17) {
                        changeSelection(1, 1, false, false);
                        editCellAt(1, 1);
                        model.fireTableCellUpdated(16, 4);

                        //    System.out.println("limit exceed");
                        //  System.out.println(model.getValueAt(editingRow, editingColumn));
                        try {
                            updateData();//if file is open need to upload data to the next page
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        changeSelection(1, 1, false, false);
                        editCellAt(1, 1);
                    } else {//добавление новой строки
                        //   System.out.println("new row");
                        model.addRow();
                        model.fireTableRowsInserted(0, getRowCount());
                        // model.fireTableStructureChanged();
                        changeSelection(editingRow + 1, 1, false, false);
                        //model.setValueAt(str, editingRow + 1, 1);
                        editCellAt(editingRow + 1, 1);
                    }
                    transferFocus();

                    //   model.setValueAt(str, editingRow, editingColumn);
                    super.insertString(0, str, attr);

                    // ((JTextField) getCellEditor().getTableCellEditorComponent(,"",true, editingRow, editingColumn)).appendText("");
                }
//                System.out.println("str=" + str);
                //         System.out.println("Row:" + editingRow + " Col" + editingColumn);
                model.fireTableCellUpdated(editingRow, editingColumn);
            }
        }


    }


    //    @Override
//    public void valueChanged(ListSelectionEvent e) {
//        System.out.println("Selection has changed.\n"+"was row:"+getSelectedRow()+"was col"+getSelectedColumn());
//        System.out.println("e first"+lastR+" "+lastC+" last "+e.getLastIndex());
//        if(model.getValueAt(lastR,lastC).toString().length()==1)
//            model.setValueAt("0"+model.getValueAt(lastR,lastC),lastR,lastC);
//    }
}