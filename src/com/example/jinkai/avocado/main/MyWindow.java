package com.example.jinkai.avocado.main;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.sun.jna.platform.win32.WinDef.HWND;

public class MyWindow {
    private HWND hWnd;
    private String title;
    private boolean visible;
    private Rectangle rect;
    private BufferedImage buf;

    public HWND getHWND() { return this.hWnd; }
    public String getTitle(){ return this.title; }
    public boolean getVisible() { return this.visible; }
    public Rectangle getRectangle() { return this.rect; }
    public BufferedImage getBuf(){ return this.buf; }
    
    public void setHWND(HWND hWnd){ this.hWnd = hWnd; }
    public void setVisible(boolean visible){ this.visible = visible; }
    public void setBuf(BufferedImage buf){ this.buf = buf; }

    MyWindow(){}

    MyWindow(String title) {
        this.title = title;
    }

    MyWindow(String title, Rectangle rect) {
        this.title = title;
        this.rect = rect;
    }

    MyWindow(String title, boolean visible, Rectangle rect) {
        this.title = title;
        this.visible = visible;
        this.rect = rect;
    }

    MyWindow(HWND hWnd, String title, boolean visible, Rectangle rect) {
        this.hWnd = hWnd;
        this.title = title;
        this.visible = visible;
        this.rect = rect;
    }

    MyWindow(HWND hWnd, String title, boolean visible, Rectangle rect, BufferedImage buf) {
        this.hWnd = hWnd;
        this.title = title;
        this.visible = visible;
        this.rect = rect;
        this.buf = buf;
    }
}
