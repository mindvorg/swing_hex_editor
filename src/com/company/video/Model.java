package com.company.video;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Model extends AbstractTableModel {
    private int numCol = 8, numRow = 8;
    private int counterPages=0;

    //private ArrayList<ArrayList<String>> data = new ArrayList<>();
    private ArrayList<ArrayList<Byte>> data = new ArrayList<>();

    public Model(int numCol, int numRow) {
        this.numCol = numCol + 1;
        this.numRow = numRow + 1;

        System.out.println("col"+this.numCol+"row"+this.numRow);

        for (int i = 0; i < this.numRow; i++) {
            data.add(new ArrayList<>());
            System.out.println();
            data.get(i).add((byte) (16*counterPages+(i-1)));
            for (int j = 0; j < this.numCol; j++) {
                data.get(i).add((byte) 0);
            }
        }
        data.get(0).set(0, (byte) 0);
        for (int i = 1; i < this.numCol; i++) {
            //data.get(0).set(i, Integer.toHexString(i - 1));
            data.get(0).set(i, (byte)(i - 1));
        }




        for (int i = 1; i < this.numRow; i++) {
            for (int j = 1; j < this.numCol; j++) {
                data.get(i).set(j,(byte)(65+(i-1)*(this.numCol-1)+j));
            }
        }




    }

    public Model(ArrayList<ArrayList<Byte>> input) {
        data = input;
        numCol = input.get(0).size();
        numRow = input.size();
        //System.out.println(output());
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        return columnIndex != 0 && rowIndex != 0;

    }

    @Override
    public int getRowCount() {
        return numRow;

    }

    @Override
    public int getColumnCount() {
        return numCol;

    }

    public ArrayList<ArrayList<Byte>> getData() {
        ArrayList<ArrayList<Byte>> tmp=new ArrayList<ArrayList<Byte>>(data);
        tmp.remove(0);
        return tmp;
    }

    public void setData(ArrayList<ArrayList<Byte>> data) {
        this.data = data;
        this.numRow=data.size();
        for (int i = 0; i < this.numRow; i++) {
            data.get(i).add((byte) (16*counterPages+(i-1)));
        }
        numRow=data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        //System.out.println("want to get" + rowIndex + " " + columnIndex);
        return String.format("%02X",data.get(rowIndex).get(columnIndex));
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        //data.get(rowIndex).set(columnIndex, (String) aValue);
        if(aValue.toString().isEmpty())
            aValue="0";
        data.get(rowIndex).set(columnIndex, (byte) Integer.parseInt((String) aValue,16));
//        System.out.println("r:" + rowIndex + "col:" + columnIndex + "aVal:" + aValue.toString());
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String toString() {
        return "Model{" +
                "counter"+counterPages+
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
        //ArrayList<String> tmp = new ArrayList<>();
        ArrayList<Byte> tmp = new ArrayList<>();
        tmp.add((byte)(16*counterPages+numRow - 2));
        for (int j = 1; j < numCol; j++) {
            tmp.add((byte) 0);
        }
        //        tmp.set(0,Integer.toHexString(numRow));
        System.out.println("add row");
        data.add(tmp);
    }


    public void resize(int newNumCol) {
        ++newNumCol;
        this.numCol=newNumCol;
        /*ArrayList<ArrayList<String>> newData = new ArrayList<>();
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
                    newCol.add(Integer.toHexString(newData.size() - 1));
                }
            }
        }
        for (int i = 0; i < newNumCol - k; i++) {
            newCol.add("");
        }

        newData.add(newCol);
        data = newData;
        numCol = newNumCol;
        numRow = data.size();
        System.out.println("clear");
        clearData();
        fixAfterClear();*/
    }


/*
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
*/

/*
    private void fixAfterClear() {
        if (!data.get(numRow - 1).get(numCol - 1).isEmpty()) {
            System.out.println("add");
            data.add(new ArrayList<>());
            numRow++;
            data.get(numRow - 1).add(Integer.toHexString(numRow - 2));
            for (int j = 1; j < numCol; j++) {
                data.get(numRow - 1).add("");
            }
        }
        for (int i = 1; i < this.numCol; i++) {
            data.get(0).set(i, Integer.toHexString(i - 1));
        }
    }
*/

/*
    public Integer[] findInData(String text) {
        ArrayList<String> findData = new ArrayList<>();
        if (text.length() >= 2) {
            for (int i = 0; i < text.length(); i = i + 2) {
                findData.add(text.substring(i, i + 2));
                //    System.out.println(text.substring(i,i+2));
            }
        } else {
            findData.add(text);
        }
        System.out.println(findData);
        ArrayList<String> existData = new ArrayList<>();
        for (int i = 1; i < numRow; i++) {
            ArrayList<String> tmp = new ArrayList<>(data.get(i));
            tmp.remove(0);
            for (int j = 0; j < tmp.size(); j++) {
                if (tmp.get(j).length() == 1) tmp.set(j, "0" + tmp.get(j));
            }
            existData.addAll(tmp);
//            System.out.println(tmp);
//            System.out.println(data.get(i));
        }
        //     System.out.println(existData);
        //System.out.println(data.stream().anyMatch(l-> Collections.indexOfSubList(l,findData)!=-1));
        int index = Collections.indexOfSubList(existData, findData);
        System.out.println(index + "cords: " + "row" + ((index / (numCol - 1)) + 1) + "col: " + (index % (numCol - 1) + 1));
        return new Integer[]{(index / (numCol - 1)) + 1, (index % (numCol - 1) + 1)};
    }
*/

/*
    public void insertWithShift(ArrayList<String> insertData, int row, int col) {
        ArrayList<String> existData = new ArrayList<>();
        for (int i = row; i < numRow; i++) {
            ArrayList<String> tmp = new ArrayList<>(data.get(i));
            tmp.remove(0);
            existData.addAll(tmp);
        }
        for (int i = 0; i < col - 1; i++) {
            existData.remove(0);
        }
        System.out.println("check");
        for (String replaceDatum : insertData) {//обновляю ячейки прошлой data на новую insertData далее надо вставить следом изначальную existData
            if (col > numCol - 1) {
                row++;
                if (row > getRowCount() - 1)
                    addRow();
                col = 1;
            }
            data.get(row).set(col, replaceDatum);
            col++;
        }
        for (String replaceDatum : existData) {//обновляю ячейки прошлой data на новую insertData далее надо вставить следом изначальную existData
            if (col > numCol - 1) {
                row++;
                if (row > getRowCount() - 1)
                    addRow();
                col = 1;
            }
            data.get(row).set(col, replaceDatum);
            col++;
        }

    }
*/

    public void deleteWithShift(Integer numBytes, int row, int col) {
//        ArrayList<String> existData = new ArrayList<>();
        ArrayList<Byte> existData = new ArrayList<>();
        for (int i = row; i < numRow; i++) {
//            ArrayList<String> tmp = new ArrayList<>(data.get(i));
//            data.get(i).replaceAll(e -> "");
//            data.get(i).set(0, String.valueOf(i - 1));
            ArrayList<Byte> tmp=new ArrayList<>(data.get(i));
            tmp.remove(0);
            existData.addAll(tmp);
        }
        System.out.println("check");
        for (int i = 0; i < numBytes; i++) {
            existData.remove(col - 1);
        }
//        for (int i = 0; i < numBytes; i++) {
//            existData.remove(0);
//        }
        System.out.println("check");
        //   for (String replaceDatum : insertData) {//обновляю ячейки прошлой data на новую insertData далее надо вставить следом изначальную existData
//            while(row*numCol+col!=numBytes){//обновляю ячейки прошлой data на новую insertData далее надо вставить следом изначальную existData
//                if (col > numCol - 1) {
//                row++;
//                if (row > getRowCount() - 1)
//                    addRow();
//                col = 1;
//            }
//            data.get(row).set( col,"");
//            col++;
//        }
        col = 1;
        for (Byte replaceDatum : existData) {//обновляю ячейки прошлой data на новую insertData далее надо вставить следом изначальную existData
            if (col > numCol - 1) {
                row++;
                if (row > getRowCount() - 1)
                    addRow();
                col = 1;
            }
            data.get(row).set(col, replaceDatum);
            col++;
        }

    }


    public void loadData(File file) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                data.clear();
                //ArrayList<String> tmp = new ArrayList<>();
                ArrayList<Byte> tmp = new ArrayList<>();
                for (int i = 0; i < numCol; i++) {
                    //tmp.add((Integer.toHexString(i - 1)));
                    tmp.add((byte)(i - 1));
                }
                tmp.set(0, (byte) 0);
                data.add(tmp);
                numRow++;
                tmp = new ArrayList<>();
                tmp.add((byte) 0);
                try {
                    FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(fr);
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        for (int i = 0; i < line.length(); i++) {
//                            tmp.add(Integer.toHexString(line.charAt(i)));
//                            if (tmp.size() == numCol) {
//                                data.add(tmp);
//                                numRow++;
//                                //fireTableDataChanged(); // Обновление данных в таблице
//                                fireTableRowsInserted(numRow,numRow);
//                                tmp = new ArrayList<>();
//                                tmp.add(Integer.toHexString(data.size() - 1));
//                            }
//                        }
//                    }
                    System.out.println("bef while");
                    int byteRead;
                    while((byteRead=br.read())!=-1){
                        tmp.add((byte) byteRead);
                        System.out.println(tmp.toString());
                            if (tmp.size() == numCol) {
                                data.add(tmp);
                                numRow++;
                                //fireTableDataChanged(); // Обновление данных в таблице
                                fireTableRowsInserted(numRow,numRow);
                                tmp = new ArrayList<>();
                                tmp.add((byte)(data.size() - 1));
                    }
                    br.close();
                    fr.close();
                }
                }catch (IOException e) {
                    e.printStackTrace();
                }

                while (tmp.size() != numCol) {
                    tmp.add((byte) 0);
                }
                data.add(tmp);
                output();
                return null;
            }

            @Override
            protected void done() {

                numRow=data.size();
                fireTableDataChanged(); // Обновление данных в таблице
                System.out.println("done");
            }

            @Override
            protected void process(List<Void> chunks) {
                super.process(chunks);
            }
        };

        worker.execute();
    }

    public ArrayList<Byte> getRow(int i) {
        return data.get(i);
    }
    public void clear(){
        data.clear();
        for (int i = 0; i < 2; i++) {
            data.add(new ArrayList<>());
            System.out.println();
            for (int j = 0; j < this.numCol; j++) {
                data.get(i).add((byte) 0);
            }
        }
        data.get(0).set(0, (byte) 0);
            //data.get(i).set(0, Integer.toHexString(i - 1));
            data.get(1).set(0, (byte) (counterPages*16));
        for (int i = 1; i < this.numCol; i++) {
            //data.get(0).set(i, Integer.toHexString(i - 1));
            data.get(0).set(i, (byte)(i - 1));
        }
        numRow=2;

    }
    public void counterPlus(){
        counterPages++;
    }
    public void counterMinus(){
        counterPages--;
    }
}
