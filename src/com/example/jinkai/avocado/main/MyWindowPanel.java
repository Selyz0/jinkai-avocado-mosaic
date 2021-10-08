package com.example.jinkai.avocado.main;

import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.event.*;

import javax.swing.*;

import com.sun.jna.platform.win32.WinDef.HWND;

public class MyWindowPanel extends JPanel{
    private HWND hWnd;
    private String windowTitle;
    private Rectangle rect;

    public HWND getHWND(){ return this.hWnd; }
    public String getTitle(){ return this.windowTitle; }
    public Rectangle getRectangle(){ return this.rect; }

    MyWindowPanel(HWND hWnd, String title, Rectangle rect){
        this.hWnd = hWnd;
        this.windowTitle = title;
        this.rect = rect;
        this.addMouseListener(new myListener());
    }

    public class myListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e){
            //e.
            // メッセージダイアログを表示
        }
    }
}
