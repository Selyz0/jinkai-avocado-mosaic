package com.example.jinkai.avocado.src;

import java.util.*;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinGDI;

//import jdk.jpackage.internal.ApplicationLayout;

interface Kernel32 extends Library {
    Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);
    void Sleep(int dwMilliseconds);
}

interface WinGDIExtra extends WinGDI {
    public DWORD SRCCOPY = new DWORD(0x00CC0020);
}

public class Main {
    public static void main(String[] args) {
        String title = WindowInfo.getActiveWindowTitle();
        System.out.println(title);
        
        List<MyWindow> windowList = WindowInfo.getWindowList();
        new WindowListFrame("Shared Window Avocado Mosaic", windowList);
    }
}
