package com.example.jinkai.avocado.main;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public interface User32 extends com.sun.jna.platform.win32.User32 {
	User32 INSTANCE = (User32) Native.load( User32.class, W32APIOptions.UNICODE_OPTIONS );
	HWND GetDesktopWindow();
    public HDC GetWindowDC(HWND hWnd);
    public boolean GetClientRect(HWND hWnd, RECT rect);
}
