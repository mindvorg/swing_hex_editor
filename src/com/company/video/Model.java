package com.company.video;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Model extends AbstractTableModel {
    private int numCol = 8, numRow = 8;
    private ArrayList<ArrayList<String>> data = new ArrayList<>();


    public Model(int numCol, int numRow) {
        this.numCol = numCol+1;
        this.numRow = numRow+1;

        System.out.println(numCol+numRow);

        for (int i = 0; i < this.numRow; i++) {
            data.add(new ArrayList<>());
            System.out.println();
            for (int j = 0; j < this.numCol; j++) {
                data.get(i).add("");
            }
        }
                                                            for (int i = 1; i < this.numRow; i++) {
                                                                data.get(i).set(0,Integer.toHexString(i-1));
                                                            }
                                                            for (int i = 1; i < this.numCol; i++) {
                                                                data.get(0).set(i,Integer.toHexString(i-1));
                                                            }
        //System.out.println(output());
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

                                                            return columnIndex != 0 && rowIndex != 0;
     //   return true;
    }

    @Override
    public int getRowCount() {
        return numRow;
    }

    @Override
    public int getColumnCount() {
        return numCol;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        System.out.println("want to get"+rowIndex+" "+columnIndex);
        return data.get(rowIndex).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data.get(rowIndex).set(columnIndex, (String) aValue);
        System.out.println("r:" + rowIndex + "col:" + columnIndex + "aVal:" + aValue.toString());
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String toString() {
        return "Model{" +
                "numCol=" + numCol +
                ", numRow=" + numRow +
                ", data=" + output() +
                '}';
    }
    private String output() {
        StringBuilder output = new StringBuilder();
        for (int i = 1; i < numRow; i++)
            for (int j = 1; j < numCol; j++)
                if (data.get(i).get(j) == null) output.append("__");
                else output.append(data.get(i).get(j));

        return String.valueOf(output);
    }

    public void addRow() {
       numRow++;
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(Integer.toHexString(numRow-2));
        for (int j = 1; j < numCol; j++) {
            tmp.add("");
        }
                                                    //        tmp.set(0,Integer.toHexString(numRow));
        System.out.println("add row");
        data.add(tmp);
    }

    public void resize(int newNumCol) {
        ++newNumCol;
        ArrayList<ArrayList<String>> newData = new ArrayList<>();
        ArrayList<String> newCol = new ArrayList<>();
        newCol.add("");
        for (int i = 1; i < newNumCol; i++) {
            newCol.add(Integer.toHexString(i));
        }
        newData.add(newCol);

        newCol = new ArrayList<>();
                                                                     newCol.add(Integer.toHexString(0));

        int k = 0;
        for (int i = 1; i < numRow; i++) {
            System.out.println();

            for (int j = 1; j < numCol; j++) {
                System.out.print(data.get(i).get(j));
                k++;
                newCol.add(data.get(i).get(j));
                if (newCol.size() == newNumCol) {
                    k = 0;
                    newData.add(newCol);
                    newCol = new ArrayList<>();
                                                                    newCol.add(Integer.toHexString(newData.size()-1));
                }
            }
        }
        for (int i = 0; i < newNumCol-k; i++) {
            newCol.add("");
        }

        newData.add(newCol);
        data = newData;
        numCol = newNumCol;
        numRow = data.size();
        System.out.println("clear");
        clearData();
        fixAfterClear();
    }

    private void clearData() {
        for (int i = numRow - 1; i > 1; i--) {
            boolean flag = false;
            for (int j = 1; j < numCol; j++) {
                if (!(data.get(i).get(j).isEmpty())) flag = true;
                System.out.print(data.get(i).get(j));
            }
            System.out.println(flag);
            if (!flag) {
                data.remove(i);
                numRow--;
            } else {
//                if (!data.get(numRow-1).get(numCol-1).isEmpty()) {
//                    System.out.println("add");
//                    data.add(new ArrayList<>());
//                    numRow++;
//                    for (int j = 0; j < numCol; j++) {
//                        data.get(numRow - 1).add("99");
//                    }
//                }
                break;
            }
        }
    }
    private void fixAfterClear(){
        if (!data.get(numRow - 1).get(numCol - 1).isEmpty()) {
            System.out.println("add");
            data.add(new ArrayList<>());
            numRow++;
                data.get(numRow - 1).add(Integer.toHexString(numRow-2 ));
            for (int j = 1; j < numCol; j++) {
                data.get(numRow - 1).add("");
            }
        }
        for (int i = 1; i < this.numCol; i++) {
            data.get(0).set(i,Integer.toHexString(i-1));
        }
    }

    public Integer[] findInData(String text) {
        ArrayList<String> findData= new ArrayList<>();
        for (int i = 0; i < text.length(); i=i+2) {
            findData.add(text.substring(i,i+2));
            System.out.println(text.substring(i,i+2));
        }
        System.out.println(findData);
        ArrayList<String> existData=new ArrayList<>();
        for (int i = 1; i < numRow; i++) {
            ArrayList<String> tmp=new ArrayList<>(data.get(i));
            tmp.remove(0);
            existData.addAll(tmp);
//            System.out.println(tmp);
//            System.out.println(data.get(i));
        }
        System.out.println(existData);
        //System.out.println(data.stream().anyMatch(l-> Collections.indexOfSubList(l,findData)!=-1));
        int index=Collections.indexOfSubList(existData,findData);
        System.out.println(index+"cords: "+"row"+(index/numRow)+"col: "+(index-(index/numRow))%numCol);
        return new Integer[]{index/numRow+1,(index-(index/numRow))%numCol+1};
    }
}
