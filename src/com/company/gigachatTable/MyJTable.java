package com.company.gigachatTable;

import com.company.video.Model;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MyJTable extends JTable implements ListSelectionListener {
    private Model model;
    public MyJTable() {
        //   super(new DefaultTableModel(8, 8));

        // model = (DefaultTableModel) getModel();
        super(new Model(5, 5));
        System.out.println(getColumnCount()+getRowCount());
        model = (Model) getModel();
        setCellSelectionEnabled(true);

        for (int i = 1; i < model.getRowCount(); i++) {
            for (int j = 1; j < model.getColumnCount(); j++) {
                JTextField textField = new JTextField();
/* PlainDocument docHex = (PlainDocument) textArea_hex.getDocument();
        docHex.setDocumentFilter(new HexFilter());*/
//                PlainDocument doc=(PlainDocument) textField.getDocument();
//                doc.setDocumentFilter(new Filter());
                textField.setDocument(new JTextFieldLimit(2)); // Ограничение символов до 2
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setFont(new Font("Arial", Font.PLAIN, 12));

                //     textField.setBorder(BorderFactory.createEmptyBorder());
//                textField.addKeyListener(new java.awt.event.KeyAdapter() {
//                    public void keyTyped(KeyEvent e) {
//                        System.out.println("typed");
//                        if (textField.getText().length() >= 2) {
//                            System.out.println("before consume");
//                            e.consume();  // Запрет ввода больше 2 символов
//                            System.out.println("after consume");
//                            int row = getSelectedRow();
//                            int col = getSelectedColumn();
//                            if (row + 1 >= getRowCount()) {
//                                System.out.println("37");
//                                changeSelection(row + 1, col, false, false);
//                            }
//                        }
//                    }
//                });
                setDefaultEditor(Object.class, new DefaultCellEditor(textField));
            }
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
        int row=rowAtPoint(event.getPoint());
        int col=columnAtPoint(event.getPoint());
        StringBuilder tmp= new StringBuilder(getValueAt(row, col).toString());
        while(tmp.length()!=2){
            tmp.insert(0, "0");
        }
//        if (tmp.length()==1) tmp.insert(0, "0");
        int intValue = Integer.parseInt(tmp.toString(), 16);
        char[] charArray = Character.toChars(intValue);
        return "hex: "+getValueAt(row,col)+"\n normal:"+String.valueOf(charArray);
    }

    public void changeNumCols(int newNumCol)
    {
        model.resize(newNumCol);
        System.out.println(model.toString());
        model.fireTableStructureChanged();
    }

    public void find(String text) {
        Integer[] result;
        result=model.findInData(text);
        clearSelection();
        changeSelection(result[0], result[1], false, false);
       // setValueAt(getValueAt(result[0], result[1]),result[0], result[1]);
//        editCellAt(1, 3);
//        transferFocus();
        //model.fireTableDataChanged();
        //model.fireTableCellUpdated(result[0], result[1]);

    }

    public void replace(boolean selected, int row, int col, String text) {
        if(text.length()%2!=0)text+=0;
        ArrayList<String> replaceData=new ArrayList<>();
        for (int i = 0; i < text.length(); i+=2) {
            replaceData.add(text.substring(i,i+2));
        }
        if (selected)
        {//insert with replace
            for (String replaceDatum : replaceData) {
                if (col > model.getColumnCount() - 1) {
                    row++;
                    if (row > getRowCount() - 1)
                        model.addRow();//рассмотреть случай когда возможно надо добавлять ряд
                    model.fireTableRowsInserted(0, getRowCount());
                    col = 1;
                }
                if (row > getRowCount() - 1)//если вставлять после в новый ряд
                    model.addRow();
                model.fireTableRowsInserted(0, getRowCount());

                model.setValueAt(replaceDatum, row, col);
                col++;
            }
        }
        else
        {//just insert and move data
            if(col==getNumCols()&&row==getNumRows())
                col--;
            if(row>getRowCount()-1)
            {col=getNumCols()-1;row--;}

            model.insertWithShift(replaceData,row,col);
            model.fireTableDataChanged();
        }
        model.fireTableDataChanged();
    }


    /*   public void mark(ArrayList<Integer[]> list){
           list.sort(Comparator.comparingInt(o -> o[0]));
           System.out.println(list);
           for (int i = list.get(0)[0]; i <=list.get(1)[0]; i++) {
               for (int j = list.get(0)[1]; j <= list.get(1)[1]; j++) {
               //    getCellRenderer(i,j).getTableCellRendererComponent(this,Color.BLUE,false,false,i,j);
               //addColumnSelectionInterval(list.get(0)[1],list.get(1)[1]);
                     //  getCellRenderer(i,j).getTableCellRendererComponent(this,getValueAt(i,j),true,false,i,j).setBackground(Color.BLUE);
               }
           }
   //        super.setValueAt(Color.RED,0,0);
   //        super.setValueAt(Color.RED,1,1);
   //        changeSelection(1,1,true,true);
           //changeSelection(list.get(0)[0],list.get(0)[1],false,true );
           //changeSelection(list.get(1)[0],list.get(1)[1],false,true);
       }*/
    // Класс для ограничения символов до 2
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
//                    System.out.println("offset:"+offset+"str:"+str+"attr"+attr);
//                    System.out.println("row:"+editingRow+"col:"+editingColumn);
//                    System.out.println("length:"+getLength());
              //      if(model.getValueAt(editingRow,editingColumn).toString().isEmpty())
                        super.insertString(offset, str, attr);
//                    lastR=editingRow;
//                    lastC=editingColumn;
            //        else {super.replace(offset,1,str,attr);}
                } else {
                    System.out.println("next");
                    if (editingColumn < getColumnCount() - 1) {//переход на следующую ячейку
                        changeSelection(editingRow, editingColumn + 1, false, false);
                        model.setValueAt(str, editingRow, editingColumn + 1);
                        editCellAt(editingRow, editingColumn + 1);

                    } else if (editingRow < getRowCount() - 1) {//переход на новую строку
                        System.out.println("data" + model.getValueAt(editingRow + 1, 1));

                        changeSelection(editingRow + 1, 1, false, false);
                        model.setValueAt(str, editingRow + 1, 1);
                        editCellAt(editingRow + 1, 1);
                    }else{//добавление новой строки
                        model.addRow();
                        model.fireTableRowsInserted(0,getRowCount());
                       // model.fireTableStructureChanged();
                        changeSelection(editingRow + 1, 1, false, false);
                        model.setValueAt(str, editingRow + 1, 1);
                        editCellAt(editingRow + 1, 1);
                    }
                    transferFocus();

                    System.out.println("str=" + str);
                    System.out.println("Row:" + editingRow + " Col" + editingColumn);

                    model.fireTableCellUpdated(editingRow, editingColumn);
                    // ((JTextField) getCellEditor().getTableCellEditorComponent(,"",true, editingRow, editingColumn)).appendText("");
                }
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