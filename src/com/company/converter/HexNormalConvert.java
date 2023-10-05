package com.company.converter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class HexNormalConvert extends DocumentFilter {
    private int key;

    public HexNormalConvert(int key) {
        this.key = key;
    }

    public void setKey(int key) {
        this.key = key;
    }


    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        System.out.println("Insert");
        text = encipher(text, key);
        super.insertString(fb, offset, text, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        System.out.println("Remove");
        super.remove(fb, offset, length);
    }



    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

        text = encipher(text, key);
        super.replace(fb, offset, length, text, attrs);
    }

    public static String encipher(String istring, int key) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < istring.length(); i++) {

            int c = istring.charAt(i);
            encrypted.append( Integer.toHexString(c) );
        }

        return (encrypted.toString());
    }

}
