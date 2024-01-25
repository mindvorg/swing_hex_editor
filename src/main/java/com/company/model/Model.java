package com.company.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class Model extends AbstractTableModel {
    private int numCol, numRow;
    private int counterPages = 0;

    private ArrayList<ArrayList<Byte>> data = new ArrayList<>();

    public Model(int numCol, int numRow) {
        this.numCol = numCol + 1;
        this.numRow = numRow + 1;

        for (int i = 0; i < this.numRow; i++) {
            data.add(new ArrayList<>());
            data.get(i).add((byte) (16 * counterPages + (i - 1)));
            for (int j = 0; j < this.numCol; j++) {
                data.get(i).add((byte) 0);
            }
        }
        data.get(0).set(0, (byte) 0);
        for (int i = 1; i < this.numCol; i++) {
            data.get(0).set(i, (byte) (i - 1));
        }


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
        ArrayList<ArrayList<Byte>> tmp = new ArrayList<>(data);
        tmp.remove(0);
        return tmp;
    }

    public void setData(ArrayList<ArrayList<Byte>> data) {
        this.data = data;
        this.numRow = data.size();
        for (int i = 0; i < this.numRow; i++) {
            data.get(i).add((byte) (16 * counterPages + (i - 1)));
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return String.format("%02X", data.get(rowIndex).get(columnIndex));
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (aValue.toString().isEmpty())
            aValue = "0";
        data.get(rowIndex).set(columnIndex, (byte) Integer.parseInt((String) aValue, 16));
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String toString() {
        return "Model{" +
                "counter" + counterPages +
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
        ArrayList<Byte> tmp = new ArrayList<>();
        tmp.add((byte) (16 * counterPages + numRow - 2));
        for (int j = 1; j < numCol; j++) {
            tmp.add((byte) 0);
        }
        data.add(tmp);
    }

    public void resize(int newNumCol) {
        ++newNumCol;
        this.numCol = newNumCol;
    }

    public void clear() {
        data.clear();
        for (int i = 0; i < 2; i++) {
            data.add(new ArrayList<>());
            for (int j = 0; j < this.numCol; j++) {
                data.get(i).add((byte) 0);
            }
        }
        data.get(0).set(0, (byte) 0);
        data.get(1).set(0, (byte) (counterPages * 16));
        for (int i = 1; i < this.numCol; i++) {
            data.get(0).set(i, (byte) (i - 1));
        }
        numRow = 2;
    }

    public void counterPlus() {
        counterPages++;
    }

    public void counterMinus() {
        counterPages--;
    }
}
