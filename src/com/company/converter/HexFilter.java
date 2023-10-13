package com.company.converter;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class HexFilter extends DocumentFilter {

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

//        Document document = fb.getDocument();
//        StringBuilder newText = null;
//        try {
//            int start = Math.max(0, offset - 8 / 2);
//            int end = offset + length;
//            String substring = document.getText(start, end - start);
//            if (text.length() > 8 && substring.endsWith(text)) {
//                newText = new StringBuilder(document.getText(0, document.getLength()));
//                newText.insert(end, "\n");
//                document.remove(start, end);
//                document.insertString(0, newText.toString(), null);
//                fb.insertString(0, "\n", null);
//            } else {
//                document.remove(offset, length);
//                document.insertString(offset, text, attrs);
//            }
//            System.out.println("goooooood");
//
//        }catch (Exception e) {
//            System.out.println("err");
//        }
//
//        System.out.println("good");

        if (text.matches("[0-9a-fA-F]+")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string.matches("[0-9a-fA-F]+")) {
                super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);

    }
}
