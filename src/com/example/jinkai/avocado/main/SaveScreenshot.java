package src.com.example.jinkai.avocado.main;

import java.awt.Rectangle;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFOHEADER;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class SaveScreenshot {
    public static BufferedImage capture(HWND hWnd) {
        HDC hdcWindow = User32.INSTANCE.GetDC(hWnd);
        HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

        RECT bounds = new RECT();
        USER.GetClientRect(hWnd, bounds);

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

        HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, WinGDIExtra.SRCCOPY);

        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);

        BITMAPINFO bmi = new BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        User32.INSTANCE.ReleaseDC(hWnd, hdcWindow);

        return image;
    }

	public static BufferedImage getScreenshot( HWND hWnd, String title, Rectangle bounds ) {
        // arrange
        //HDC windowDC = GDI.GetDC( USER.GetDesktopWindow() );
        //hWnd = USER.FindWindow(null, title);
        System.out.println("title: " + title + ", rect: " + bounds);
        //User32DLL.SetWindowPos(hWnd, -1, bounds.x, bounds.y, bounds.width, bounds.height, 16384);
        //HDC windowDC = GDI.GetDC(null);
		HDC windowDC = GDI.GetDC( null );
        HBITMAP outputBitmap = GDI.CreateCompatibleBitmap( windowDC, bounds.width, bounds.height );
		try {
			HDC blitDC = GDI.CreateCompatibleDC( windowDC );
			try {
				HANDLE oldBitmap = GDI.SelectObject( blitDC, outputBitmap );
				try {
                    //boolean success = User32DLL.PrintWindow(hWnd, blitDC, 0);
                    //System.out.println(hWnd + ", " + success);
                    //GDI.BitBlt( blitDC, 0, 0, bounds.width, bounds.height, windowDC, 0, 0, GDI32.SRCCOPY );
                    GDI.BitBlt( blitDC, 0, 0, bounds.width, bounds.height, windowDC, bounds.x, bounds.y, GDI32.SRCCOPY );
				} finally {
					GDI.SelectObject( blitDC, oldBitmap );
				}
				BITMAPINFO bi = new BITMAPINFO( 40 );
				bi.bmiHeader.biSize = 40;
				boolean ok = GDI.GetDIBits( blitDC, outputBitmap, 0, bounds.height, (byte[]) null, bi, WinGDI.DIB_RGB_COLORS );
				if ( ok ) {
					BITMAPINFOHEADER bih = bi.bmiHeader;
					bih.biHeight = -Math.abs( bih.biHeight );
					bi.bmiHeader.biCompression = 0;
					return bufferedImageFromBitmap( blitDC, outputBitmap, bi );
				} else
					return null;
			} finally {
				GDI.DeleteObject( blitDC );
			}
		} finally {
			GDI.DeleteObject( outputBitmap );
		}
	}

	public static BufferedImage bufferedImageFromBitmap( HDC blitDC, HBITMAP outputBitmap, BITMAPINFO bi ) {
		BITMAPINFOHEADER bih = bi.bmiHeader;
		int height = Math.abs( bih.biHeight );
		final ColorModel cm;
		final DataBuffer buffer;
		final WritableRaster raster;
		int strideBits = ( bih.biWidth * bih.biBitCount );
		int strideBytesAligned = ( ( ( strideBits - 1 ) | 0x1F ) + 1 ) >> 3;
		final int strideElementsAligned;
		
		switch ( bih.biBitCount ) {
		case 16:
			strideElementsAligned = strideBytesAligned / 2;
			cm = new DirectColorModel( 16, 0x7C00, 0x3E0, 0x1F );
			buffer = new DataBufferUShort( strideElementsAligned * height );
			raster = Raster.createPackedRaster( buffer, bih.biWidth, height, strideElementsAligned, ( (DirectColorModel) cm ).getMasks(), null );
			break;
		case 32:
			strideElementsAligned = strideBytesAligned / 4;
			cm = new DirectColorModel( 32, 0xFF0000, 0xFF00, 0xFF );
			buffer = new DataBufferInt( strideElementsAligned * height );
			raster = Raster.createPackedRaster( buffer, bih.biWidth, height, strideElementsAligned, ( (DirectColorModel) cm ).getMasks(), null );
			break;
		default:
			throw new IllegalArgumentException( "Unsupported bit count: " + bih.biBitCount );
		}
		
		final boolean ok;
		switch ( buffer.getDataType() ) {
		case DataBuffer.TYPE_INT : {
			int[] pixels = ( (DataBufferInt) buffer ).getData();
			ok = GDI.GetDIBits( blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0 );
			break;
		}
		case DataBuffer.TYPE_USHORT : {
			short[] pixels = ( (DataBufferUShort) buffer ).getData();
			ok = GDI.GetDIBits( blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0 );
			break;
		}
		default:
			throw new AssertionError( "Unexpected buffer element type: " + buffer.getDataType() );
		}
		
		return ok ? new BufferedImage( cm, raster, false, null ) : null;
	}
	
	private static final User32 USER = User32.INSTANCE;
	private static final GDI32 GDI = GDI32.INSTANCE;
}
