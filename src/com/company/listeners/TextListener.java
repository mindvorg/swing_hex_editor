package com.company.listeners;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TextListener {
    private JTextArea textArea_normal;
    private JTextArea textArea_hex;
    private JTextArea numCols;
    private boolean isUpdating = false;


    public TextListener(JTextArea textArea_normal, JTextArea textArea_hex, JTextArea numCols) {
        this.textArea_normal = textArea_normal;
        this.textArea_hex = textArea_hex;
        this.numCols = numCols;
    }

    //найти у алисы можно ли использовать document listener и document filter в одном классе и привести пример
    public void sync() {

        //inserting text in hex from normal
        textArea_normal.getDocument().addDocumentListener(new DocumentListener() {//выносить отдельно в два листенера, поскольку у нас конвертация в hex формат
            @Override
            public void insertUpdate(DocumentEvent e) {

                if (!isUpdating) {
                    isUpdating = true;
                    String tmp = textArea_normal.getText().replaceAll("\n", "");
                    StringBuilder encrypted = new StringBuilder();
                    for (int i = 0; i < tmp.length(); i++) {
                        int c = tmp.charAt(i);
                        if (i != 0 && i % 8 == 0) encrypted.append("\n");
                        encrypted.append(Integer.toHexString(c).length() == 2 ? Integer.toHexString(c) : "0" + Integer.toHexString(c));
                        //   System.out.println("i="+i);

                    }
                    System.out.println("enc=" + encrypted);

                    textArea_hex.setText(String.valueOf(encrypted));//можно вставлять текст, это ускорит, но надо понимать куда вставлять(позиция обычная*2) но надо узнать позицию
                    isUpdating = false;
                }

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    String tmp = textArea_normal.getText().replaceAll("\n", "");
                    StringBuilder encrypted = new StringBuilder();
                    for (int i = 0; i < tmp.length(); i++) {
                        int c = tmp.charAt(i);
                        if (i != 0 && i % 8 == 0) encrypted.append("\n");
                        encrypted.append(Integer.toHexString(c).length() == 2 ? Integer.toHexString(c) : "0" + Integer.toHexString(c));
                    }
                    textArea_hex.setText(String.valueOf(encrypted));//можно вставлять текст, это ускорит, но надо понимать куда вставлять(позиция обычная*2) но надо узнать позицию
                    isUpdating = false;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });


        //from hex to normal
        textArea_hex.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isUpdating && textArea_hex.getDocument().getLength() % 2 == 0) {
                    isUpdating = true;
                    StringBuilder sb = new StringBuilder();
                    String tmp = textArea_hex.getText().replaceAll("\n", "");//надо двигать условие по четности нечетности, поскольку из-за \n у нас становится четным строка, что не есть правда
                    //System.out.println("tmp=" + tmp);
                    for (int i = 2; i < tmp.length(); i += 2) {
                        String hexPair = tmp.substring(i - 2, i);
                        int intValue = Integer.parseInt(hexPair, 16);
                        char[] charArray = Character.toChars(intValue);
                        if (i != 0 && i % 16 == 0) sb.append("\n");
                        sb.append(charArray);
                        //  System.out.print("i="+i);
                    }
//                    System.out.println("sb=" + sb);
                    textArea_normal.setText(String.valueOf(sb));


                    //добавим изменения текста самого textArea_hex

                    // if(textArea_hex.getText().length()%16==0&&textArea_hex.getText().length()>0)textArea_hex.append("\n");
                    //

                    isUpdating = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating && textArea_hex.getDocument().getLength() % 2 == 0) {
                    isUpdating = true;
                    StringBuilder sb = new StringBuilder();
                    String tmp = textArea_hex.getText().replaceAll("\n", "");
                    for (int i = 2; i < tmp.length(); i += 2) {
                        String hexPair = tmp.substring(i - 2, i);
                        int intValue = Integer.parseInt(hexPair, 16);
                        char[] charArray = Character.toChars(intValue);
                        if (i != 0 && i % 16 == 0) sb.append("\n");
                        sb.append(charArray);
                        // System.out.println("i="+i);
                    }
                    System.out.println("sb=" + sb);
                    textArea_normal.setText(String.valueOf(sb));

                    //добавим изменения текста самого textArea_hex
//                    StringBuilder addN=new StringBuilder(textArea_hex.getText());
//                    for(int i=1;i<addN.length();i++){
//                        if (i%16==0) addN.insert(i++,"\n");
//                    }
//                    textArea_hex.setText(addN.toString());
                    //

                    isUpdating = false;
                }
//                if (!isUpdating && textArea_hex.getDocument().getLength() % 2 == 0) {
//                    isUpdating = true;
//                    StringBuilder sb = new StringBuilder();
//                    for (int i = 0; i < textArea_hex.getText().length(); i += 2) {
//                        String hexPair = textArea_hex.getText().substring(i, i + 2);
//                        int intValue = Integer.parseInt(hexPair, 16);
//                        char[] charArray = Character.toChars(intValue);
//                        sb.append(charArray);
//                    }
//
//                    textArea_normal.setText(String.valueOf(sb));
//                    isUpdating = false;
//                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;
                    textArea_normal.setText(textArea_hex.getText());
                    isUpdating = false;
                }
            }
        });

    }

    public void syncPerenos() {
        textArea_normal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (textArea_normal.getText().length() % 8 == 0 && textArea_normal.getText().length() != 0) {
                    textArea_normal.append("\n");
                }
            }
        }); //до лучших времен, тут работает перенос текста, но проблема с \n Делать в конце
        textArea_hex.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (textArea_hex.getText().length() % 16 == 0 && textArea_hex.getText().length() != 0) {
                    textArea_hex.append("\n");
                }
            }
        }); //до лучших времен, тут работает перенос текста, но проблема с \n Делать в конце
    }

    public void syncToHex() {
        textArea_normal.getDocument().addDocumentListener(new DocumentListener() {//выносить отдельно в два листенера, поскольку у нас конвертация в hex формат
            @Override
            public void insertUpdate(DocumentEvent e) {
//                textArea_normal.setRows(Math.max(textArea_normal.getRows(), textArea_hex.getRows()));
//                textArea_hex.setRows(Math.max(textArea_normal.getRows(), textArea_hex.getRows()));
//                try {
//                    System.out.println("text:"+e.getDocument().getText(e.getOffset(),e.getLength()));
//                    System.out.println("text:"+textArea_normal.getText());
//                } catch (BadLocationException ex) {
//                    ex.printStackTrace();
//                }
                /**
                 * Стринг текст= ареа.геттекст
                 * текст-> убрать \n
                 * синхронизация текста
                 * проверка на лимиты
                 *вроде бы какая-то фигня
                 * */

                /*
                 * попробуем просто добавлять в внутри insert и remove \n и пропускать его, если наткыаюсь
                 * */
                System.out.println(0);
                if (!isUpdating) {
                    isUpdating = true;
                    StringBuilder encrypted = new StringBuilder();
                    String str = "";
                    System.out.println(2);

                    try {
                        str = e.getDocument().getText(e.getOffset(), e.getLength());
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                    for (int i = 0; i < str.length(); i++) {
                        int c = 0;
                        c = str.charAt(i);
                        encrypted.append(Integer.toHexString(c).length() == 2 ? Integer.toHexString(c) : "0" + Integer.toHexString(c));
                    }

//                    textHex.insert(e.getOffset()*2, encrypted);
//                    textArea_hex.setText(String.valueOf(textHex));
                    //выше стало ниже было
                    textArea_hex.insert(String.valueOf(encrypted), e.getOffset() * 2);//можно вставлять текст, это ускорит, но надо понимать куда вставлять(позиция обычная*2) но надо узнать позицию

                    isUpdating = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating) {
                    isUpdating = true;

                    StringBuilder encrypted = new StringBuilder("");
                    System.out.println("length:" + textArea_normal.getText().length());//////////////////////////////доделать!!! проверить почему при удалении всего всё не удаляется?!
                    for (int i = 0; i < textArea_normal.getText().length(); i++) {
                        int c = textArea_normal.getText().charAt(i);
                        encrypted.append(Integer.toHexString(c));//фильтр мб не пропускает пустой символ и стоит его добавить в допустимые
                    }
//                    if( textArea_normal.getText().isEmpty())
//                        textArea_hex.setText(textArea_normal.getText());
//                    else

                    textArea_hex.setText(String.valueOf(encrypted));
//                    try {
//                        textArea_hex.getDocument().remove(e.getOffset()*2,e.getLength()*2);
//                    } catch (BadLocationException ex) {
//                        ex.printStackTrace();
//                    }
                    isUpdating = false;
                    System.out.println("after delete normal:" + textArea_normal.getText());
                    System.out.println("after delete hex:" + textArea_hex.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    public void syncToNorm() {//from hex to normal

        textArea_hex.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {

//                StringBuilder str=new StringBuilder();
//                System.out.println("cols"+textArea_normal.getColumns());
//                System.out.println("rows"+textArea_normal.getRows());
//
//                for (int i = 0; i < textArea_normal.getColumns(); i++) {
//                    str.append(String.format("%08X", i)).append("\n");
//                }
//                numCols.setText(String.valueOf(str));


//                textArea_normal.setRows(Math.max(textArea_normal.getRows(), textArea_hex.getRows()));
//                textArea_hex.setRows(Math.max(textArea_normal.getRows(), textArea_hex.getRows()));


                if (!isUpdating && textArea_hex.getDocument().getLength() % 2 == 0) {
                    isUpdating = true;

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < textArea_hex.getText().length(); i += 2) {
                        String hexPair = textArea_hex.getText().substring(i, i + 2);
                        int intValue = Integer.parseInt(hexPair, 16);
                        char[] charArray = Character.toChars(intValue);
                        sb.append(charArray);
                    }
                    textArea_normal.setText(String.valueOf(sb));
                    isUpdating = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isUpdating && textArea_hex.getDocument().getLength() % 2 == 0) {
                    isUpdating = true;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < textArea_hex.getText().length(); i += 2) {
                        String hexPair = textArea_hex.getText().substring(i, i + 2);
                        int intValue = Integer.parseInt(hexPair, 16);
                        char[] charArray = Character.toChars(intValue);
                        sb.append(charArray);
                        System.out.println(666);
                    }

                    textArea_normal.setText(String.valueOf(sb));
                    isUpdating = false;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    private void checkLimit(JTextArea area, int limit) throws BadLocationException {
        Document doc = area.getDocument();
        int caretPosition = area.getCaretPosition();
        String text = doc.getText(0, doc.getLength());
        String[] lines = text.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > limit) {
                doc.remove(Math.max(0, caretPosition - 1), 1);
                String line = lines[i].substring(0, limit);
                if (i == lines.length - 1) {
                    line += lines[i].substring(limit);
                    lines[i] = line;
                } else {
                    lines[i + 1] = lines[i + 1] + lines[i].substring(limit);
                }
                doc.insertString(Math.min(doc.getLength(), caretPosition), line, null);
                area.requestFocusInWindow();
                area.moveCaretPosition(Math.min(caretPosition, doc.getLength()));
            }
        }
    }

    private static void checkWrap(JTextArea textArea, int wrap) throws BadLocationException {
        Document doc = textArea.getDocument();
        if (doc == null) return;
        String text = doc.getText(0, doc.getLength());
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                int lineLength = 0;
                for (int j = i; j >0 && text.charAt(j-1)!='\n'; j--) {
                    lineLength++;
                }
                if(lineLength>wrap){
                    text=text.substring(0,i)+"\n"+text.substring(i);
                    doc.remove(i-1,1);
                    doc.insertString(i,"\n",null);
                }
                else {break;}
            }
        }
    }
}
