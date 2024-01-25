package com.company.converter;

import javax.swing.*;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import javax.swing.text.*;

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
        System.out.println("problemo el here:"+fb.getDocument().getText(0,fb.getDocument().getLength())+"|offset:"+offset+"|text:"+text);
        if (text.matches("[0-9a-fA-F\n\0]+")||text.isEmpty()) {

     //       System.out.println("before insert"+fb.getDocument().getText(0,fb.getDocument().getLength()));
          //  super.insertString(fb, offset, text , attrs);
       //     System.out.println("after insert"+fb.getDocument().getText(0,fb.getDocument().getLength()));
            //DefaultCaret caret = (DefaultCaret) getCaret();
            //caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
            StringBuilder str=new StringBuilder((fb.getDocument().getText(0,fb.getDocument().getLength())));
            str.insert(offset,text);
            str=new StringBuilder(str.toString().replaceAll("\n",""));
            int count=0;
            for (int i = 0; i <str.length() ; i++,count++) {
                if(count%16==0&&count>0){count =-1;str.insert(i,"\n");
                }
            }
            System.out.println(str);

            super.replace(fb,0,fb.getDocument().getLength(), String.valueOf(str),attrs);
    //        super.remove(fb,0,fb.getDocument().getLength());//какая-то проблема с переносом в нормальном тексте, разобраться через выводы
  //          super.insertString(fb,0, String.valueOf(str),attrs);

//            JTextArea area=(JTextArea) fb.getDocument().getProperty("parent");
//
//            if(area!=null)area.setCaretPosition(offset+length);

            if(offset%15==0&&offset>0) {

                System.out.println("replace need to \\ n");
       //         super.insertString(fb, offset, text + "\n", attrs);
                //переписать с доп функцией, которая каждый раз будет просто прогонять текст и если что добавлять \n
                /*fb.getDocument().getText(0,fb.getDocument().getLength())- документ без последней напечатанной буквы
                 *offset- позиция в строке последней буквы(индекс)
                 * text-последняя вставленная буква
                 *в fb вставляем измененный текст, в text пустой символ?
                 *  */

            }
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        System.out.println("problemka tut");//добавить сюда возможность добавлять \n и по идее смогу делить на 16 и 8 символов
        if(string.length()%16==0) {System.out.println("insert need to \\ n");}
        if (string.matches("[0-9a-fA-F\0]+")) {
            //System.out.println("|"+fb.getDocument().getText(0,fb.getDocument().getLength()));
            StringBuilder str=new StringBuilder(fb.getDocument().getText(0,fb.getDocument().getLength()));
            str.insert(offset,string);
            str=new StringBuilder(str.toString().replaceAll("\n",""));
            System.out.println("|"+str);
            int count=0;
            for (int i = 0; i <str.length() ; i++,count++) {
                if(count%16==0&&count>0){count =-1;str.insert(i,"\n");
                }
            }
            //super.insertString(fb, offset, string, attr);
            super.replace(fb,0,fb.getDocument().getLength(), String.valueOf(str),null);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        System.out.println("delete tut");
        super.remove(fb, offset, length);
        System.out.println(fb.getDocument().getText(0,fb.getDocument().getLength()));
        StringBuilder str=new StringBuilder(fb.getDocument().getText(0,fb.getDocument().getLength()).replaceAll("\n",""));
        System.out.println(str);
        int count=0;
        for (int i = 0; i <str.length() ; i++,count++) {
            if(count%16==0&&count>0){count =-1;str.insert(i,"\n");
            }
        }
        System.out.println(str);

        //super.replace(fb,0,str.length(), String.valueOf(str),attrs);
//        super.remove(fb,0,fb.getDocument().getLength());//какая-то проблема с переносом в нормальном тексте, разобраться через выводы
//        super.insertString(fb,0, String.valueOf(str),attrs);
//        JTextArea text=new JTextArea(String.valueOf(str));
//        text.setCaretPosition(offset);
        super.replace(fb,0,fb.getDocument().getLength(),String.valueOf(str),null);

//        JTextArea area=(JTextArea) fb.getDocument().getProperty("parent");
//        if(area!=null) area.setCaretPosition(offset+length);

    }
    private void resetCaretPosition(DocumentFilter.FilterBypass fb, int pos){
        JTextArea component= (JTextArea) fb.getDocument();
        if(component!=null) component.setCaretPosition(pos);
    }
}
