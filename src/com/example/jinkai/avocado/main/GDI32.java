package com.example.jinkai.avocado.main;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;

public interface GDI32 extends com.sun.jna.platform.win32.GDI32 {
	GDI32 INSTANCE = (GDI32) Native.load( GDI32.class );
	boolean BitBlt( HDC hdcDest, int nXDest, int nYDest, int nWidth, int nHeight, HDC hdcSrc, int nXSrc, int nYSrc, int dwRop );
    boolean BitBlt(HDC hObject, int nXDest, int nYDest, int nWidth, int nHeight, HDC hObjectSource, int nXSrc, int nYSrc, DWORD dwRop);
	HDC GetDC( HWND hWnd );
	boolean GetDIBits( HDC dc, HBITMAP bmp, int startScan, int scanLines, byte[] pixels, BITMAPINFO bi, int usage );
	boolean GetDIBits( HDC dc, HBITMAP bmp, int startScan, int scanLines, short[] pixels, BITMAPINFO bi, int usage );
	boolean GetDIBits( HDC dc, HBITMAP bmp, int startScan, int scanLines, int[] pixels, BITMAPINFO bi, int usage );
	int SRCCOPY = 0xCC0020;
}