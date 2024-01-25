package com.company.listeners;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseMarkListener {

    private JTextArea textArea_normal;

    private int startMark;
    private int endMark;
    private JTextArea textArea_hex;

    public MouseMarkListener(JTextArea textArea_normal,JTextArea textArea_hex) {
        this.textArea_normal = textArea_normal;

        this.textArea_hex = textArea_hex;
    }

    public void sync() {
        textArea_normal.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (textArea_hex.getSelectedText() !=null||textArea_normal.getSelectedText() !=null) {
                    textArea_hex.getHighlighter().removeAllHighlights();
                    textArea_normal.getHighlighter().removeAllHighlights();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                textArea_hex.getHighlighter().removeAllHighlights();
                textArea_normal.getHighlighter().removeAllHighlights();
                startMark = textArea_normal.getSelectionStart();
              //  System.out.println("start" + startMark);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (textArea_normal.getSelectedText() != null) {
                    endMark = textArea_normal.getSelectionEnd();
              //      System.out.println("end" + startMark);
                    try {
              //          System.out.println(textArea_normal.getSelectedText());
                        startMark = startMark == endMark ? startMark - textArea_normal.getSelectedText().length() : startMark;

                        Object o = textArea_hex.getHighlighter().addHighlight(startMark * 2, endMark * 2, DefaultHighlighter.DefaultPainter);
                        //textArea_hex.setSelectedTextColor(Color.YELLOW);

                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });


        textArea_hex.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (textArea_hex.getSelectedText() !=null||textArea_normal.getSelectedText() !=null) {
                    textArea_hex.getHighlighter().removeAllHighlights();
                    textArea_normal.getHighlighter().removeAllHighlights();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                textArea_hex.getHighlighter().removeAllHighlights();
                textArea_normal.getHighlighter().removeAllHighlights();
                startMark = textArea_hex.getSelectionStart();
 //               System.out.println("startHex" + startMark);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (textArea_hex.getSelectedText() != null) {
                    endMark = textArea_hex.getSelectionEnd();
                 //   System.out.println("endHex" + startMark);
                    try {
                  //      System.out.println(textArea_hex.getSelectedText());

                        startMark = startMark == endMark ? startMark - textArea_hex.getSelectedText().length() : startMark;
                           System.out.println("start" + startMark);
                           System.out.println("end" + endMark);
                      //if marked not full byte need to move caret left at 1 pos
                        if(startMark%2!=0){textArea_hex.setSelectionStart(textArea_hex.getSelectionStart()-1);}
                        if(endMark%2!=0){textArea_hex.setSelectionEnd(textArea_hex.getSelectionEnd()+1);}
                        textArea_hex.getHighlighter().addHighlight(startMark%2==0?startMark:startMark-1,endMark%2==0?endMark:endMark+1,DefaultHighlighter.DefaultPainter);
                        textArea_normal.getHighlighter().addHighlight(startMark /2, endMark / 2+endMark%2, DefaultHighlighter.DefaultPainter);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

    }
}
