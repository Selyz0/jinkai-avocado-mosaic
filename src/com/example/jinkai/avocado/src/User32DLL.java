package com.example.jinkai.avocado.src;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class User32DLL {
    static {
        Native.register("user32");
    }
    public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
    public static native HWND GetForegroundWindow();
    public static native boolean GetWindowRect(HWND hWnd, RECT rect);
    public static native boolean BringWindowToTop(HWND hWnd);
    public static native boolean SetForegroundWindow(HWND hWnd);
    public static native boolean SetWindowPos(HWND hWnd, int hWndInsertAfter, int x, int y, int cx, int cy, int uFlags);
    public static native boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer lParam);
    public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
    public static native boolean PrintWindow(HWND hWnd, HDC hdcBlt, int nFlags);
}
