package com.example.jinkai.avocado.main;

import java.util.*;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.example.jinkai.avocado.views.*;
import com.sun.jna.platform.win32.WinGDI;

//import jdk.jpackage.internal.ApplicationLayout;

interface WinGDIExtra extends WinGDI {
    public DWORD SRCCOPY = new DWORD(0x00CC0020);
}

public class Main {
    public static void main(String[] args) {
        String java_dll_path = "E:/Users/Seri/Documents/work/mws/2021/git/jinkai-avocado-mosaic/lib/opencv_java453.dll";
        System.load(java_dll_path);

        String title = WindowInfo.getActiveWindowTitle();
        System.out.println(title);
        
        List<MyWindow> windowList = WindowInfo.getWindowList();
        new WindowListFrame("Avocado Safe Sharing", windowList);
    }
}
