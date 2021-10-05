package com.example.jinkai.avocado.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import java.awt.event.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.GDI32Util;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFOHEADER;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;

class User32DLL {
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

interface Kernel32 extends Library {
    Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);
    void Sleep(int dwMilliseconds);
}

interface GDI32 extends com.sun.jna.platform.win32.GDI32 {
	GDI32 INSTANCE = (GDI32) Native.load( GDI32.class );
	boolean BitBlt( HDC hdcDest, int nXDest, int nYDest, int nWidth, int nHeight, HDC hdcSrc, int nXSrc, int nYSrc, int dwRop );
    boolean BitBlt(HDC hObject, int nXDest, int nYDest, int nWidth, int nHeight, HDC hObjectSource, int nXSrc, int nYSrc, DWORD dwRop);
	HDC GetDC( HWND hWnd );
	boolean GetDIBits( HDC dc, HBITMAP bmp, int startScan, int scanLines, byte[] pixels, BITMAPINFO bi, int usage );
	boolean GetDIBits( HDC dc, HBITMAP bmp, int startScan, int scanLines, short[] pixels, BITMAPINFO bi, int usage );
	boolean GetDIBits( HDC dc, HBITMAP bmp, int startScan, int scanLines, int[] pixels, BITMAPINFO bi, int usage );
	int SRCCOPY = 0xCC0020;
}

interface User32 extends com.sun.jna.platform.win32.User32 {
	User32 INSTANCE = (User32) Native.load( User32.class, W32APIOptions.UNICODE_OPTIONS );
	HWND GetDesktopWindow();
    public HDC GetWindowDC(HWND hWnd);
    public boolean GetClientRect(HWND hWnd, RECT rect);
}

interface WinGDIExtra extends WinGDI {
    public DWORD SRCCOPY = new DWORD(0x00CC0020);
}

// To Save Screenshot
class saveScreenshot {
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

class myWindow {
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

    myWindow(){}

    myWindow(String title) {
        this.title = title;
    }

    myWindow(String title, Rectangle rect) {
        this.title = title;
        this.rect = rect;
    }

    myWindow(String title, boolean visible, Rectangle rect) {
        this.title = title;
        this.visible = visible;
        this.rect = rect;
    }

    myWindow(HWND hWnd, String title, boolean visible, Rectangle rect) {
        this.hWnd = hWnd;
        this.title = title;
        this.visible = visible;
        this.rect = rect;
    }

    myWindow(HWND hWnd, String title, boolean visible, Rectangle rect, BufferedImage buf) {
        this.hWnd = hWnd;
        this.title = title;
        this.visible = visible;
        this.rect = rect;
        this.buf = buf;
    }
}

class WindowInfo {
    public static String getActiveWindowTitle() {
        final int MAX_TITLE_LENGTH = 1024;
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];

        HWND foregroundWindow = User32DLL.GetForegroundWindow();
        User32DLL.GetWindowTextW(foregroundWindow, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }

    public static Rectangle getActiveWindowRectangle() {
        String title = WindowInfo.getActiveWindowTitle();

        final Rectangle rect = new Rectangle(0, 0, 0, 0);
        WindowUtils.getAllWindows(true).forEach(desktopWindow -> {
            if (desktopWindow.getTitle().contains(title)) {
                rect.setRect(desktopWindow.getLocAndSize());
            }
        });
        return rect;
    }

    public static List<myWindow> getWindowList() {
        List<DesktopWindow> allWindows = WindowUtils.getAllWindows(true);
        List<myWindow> windowList = new ArrayList<>();

        // モニタの情報を取得
        // # ひとまずハードコーディング
        final int width = 1920;
        final int height = 1080;
        final int HWND_TOP = -1;
        /*
        final int MAX_MONITOR_NUM = 10;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        double[] width = new double[MAX_MONITOR_NUM];
        double[] height = new double[MAX_MONITOR_NUM];
        for (int i = 0;i < gs.length;i++){
            width[i] = gs[i].getDisplayMode().getWidth();
            height[i] = gs[i].getDisplayMode().getHeight();

            System.out.println(width[i] +  ", " + height[i]);
        }
        */

        for (int index = 0; index < allWindows.size(); index++){
            DesktopWindow desktopWindow = allWindows.get(index);
            HWND hWnd = desktopWindow.getHWND();
            String title = desktopWindow.getTitle();
            Rectangle rect = desktopWindow.getLocAndSize();
            boolean visible = User32.INSTANCE.IsWindowVisible(hWnd);

            // オーバーレイなどを除外したい
            if (title.isEmpty() || !(rect.x > -32000) || (rect.x == 0 && rect.y == 0 && rect.height % height == 0 && rect.width % width == 0)) {
                continue;
            }

            //boolean success = User32DLL.SetWindowPos(hWnd, HWND_TOP, rect.x, rect.y, rect.width, rect.height, 16384);
            if(isFullScreen(desktopWindow)){
                rect.x += 8; rect.y += 8;
                rect.width -= 16; rect.height -= 16;
            }
            BufferedImage buf = saveScreenshot.capture(hWnd);
            //BufferedImage buf = saveScreenshot.getScreenshot(hWnd, title, rect);
            //success = User32DLL.SetWindowPos(hWnd, HWND_TOP, rect.x, rect.y, rect.width, rect.height, 16384);
            
            if (!(buf == null)){
                try{
                    ImageIO.write(buf, "PNG", new File("./assets/capture.png"));
                } catch(IOException ie){}
            }

            windowList.add(new myWindow(hWnd, title, visible, rect, buf));
        }
        
        return windowList;
    }

    public static boolean isFullScreen(DesktopWindow targetWindow){
        HWND desktopWindow = User32.INSTANCE.GetDesktopWindow();
        RECT desktopWindowRect = new RECT();
        User32.INSTANCE.GetWindowRect(desktopWindow, desktopWindowRect);
        RECT fullScreenRect = new RECT();
        fullScreenRect.top = -8; fullScreenRect.left = -8;
        fullScreenRect.bottom = desktopWindowRect.bottom+8;
        fullScreenRect.right = desktopWindowRect.right+8;
        System.out.println("a: " + targetWindow.getLocAndSize().toString());
        System.out.println("b: " + fullScreenRect.toString());
        return (fullScreenRect.top == targetWindow.getLocAndSize().y && fullScreenRect.bottom == targetWindow.getLocAndSize().y+targetWindow.getLocAndSize().height)
            && (fullScreenRect.left == targetWindow.getLocAndSize().x && fullScreenRect.right == targetWindow.getLocAndSize().x+targetWindow.getLocAndSize().width);
    }
}

class windowPanel extends JPanel{
    private HWND hWnd;
    private String windowTitle;
    private Rectangle rect;

    public HWND getHWND(){ return this.hWnd; }
    public String getTitle(){ return this.windowTitle; }
    public Rectangle getRectangle(){ return this.rect; }

    windowPanel(HWND hWnd, String title, Rectangle rect){
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

class WindowListFrame extends JFrame {
    private int width = 1200;
    private int height = 500;
    private int padding = 10;
    int panelWidth, panelHeight;
    private List<myWindow> windowList;
    private List<windowPanel> panelList = new ArrayList<windowPanel>();

    public List<windowPanel> getPanelList(){ return this.panelList; }

    private void calcParameters(final int MAX_ROW_NUM){
        panelWidth = (width - padding*(MAX_ROW_NUM+1)) / MAX_ROW_NUM;
        if(windowList.size() % MAX_ROW_NUM > 0){
            panelHeight = (height - padding*(windowList.size())) / (windowList.size() / MAX_ROW_NUM + 1);
        } else {
            panelHeight = (height - padding*(windowList.size())) / (windowList.size() / MAX_ROW_NUM);
        }
    }

    WindowListFrame(String title, List<myWindow> windowList) {
        final int MAX_ROW_NUM = windowList.size() / 2 + windowList.size() % 2;
        this.windowList = windowList;
        
        calcParameters(MAX_ROW_NUM);

        for (int index = 0; index < windowList.size(); index++){
            myWindow window = windowList.get(index);
            
            int x = (padding + this.panelWidth) * (index % MAX_ROW_NUM);
            int y = (padding + this.panelHeight) * (index / MAX_ROW_NUM);

            // コンポーネント初期化
            windowPanel p = new windowPanel(window.getHWND(), window.getTitle(), window.getRectangle());
            p.setLayout(new BorderLayout());
            p.setBackground(Color.WHITE);
            p.setPreferredSize(new Dimension(panelWidth, panelHeight));

            // ラベルの設定
            JLabel label = new JLabel();
            JLabel labelText = new JLabel(window.getTitle());
            label.setBounds(new Rectangle(x, y, panelWidth, panelHeight-20));
            labelText.setBounds(new Rectangle(x, y+panelHeight-20, panelWidth, 15));


            // スクリーンショットをラベルに適用
            ImageIcon screenshotImg = new ImageIcon(window.getBuf());
            Image resizedImg = screenshotImg.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImg);
            label.setIcon(resizedIcon);

            /* デバッグ用
            try{
                ImageIO.write(buf, "PNG", new File("./assets/windowlist_test.png"));
            } catch(IOException e){}
            */

            // パネルに適用
            p.add(label);
            p.add(labelText, BorderLayout.SOUTH);
            p.addMouseListener(new myListener());
            this.panelList.add(p);

            getContentPane().add(p);

            //System.out.println("title: " + window.getTitle() + ", rect: " + window.getRectangle());
        }

        // 各種パラメータ設定
        setVisible(true);
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(this.width, this.height);
        getContentPane().setLayout(new FlowLayout());
    }

    public class myListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e){
            // ウィンドウを閉じる
            Component c = (Component)e.getSource();
            Window w = SwingUtilities.getWindowAncestor(c);
            w.dispose();

            // 完全に閉じられるまでスリープ
            Kernel32.INSTANCE.Sleep(500);

            // クリックした対象のウィンドウを共有する
            panelList = getPanelList();
            for (int index = 0; index < panelList.size(); index++){
                windowPanel p = panelList.get(index);
                if(p == e.getSource()){
                    new applyFilterFrame(p.getHWND(), "Shared Window Avocado Mosaic (Target: " + p.getTitle() + ")", p.getTitle(), p.getRectangle());
                }
            }
        }
    }
}

class applyFilterFrame extends JFrame {
    applyFilterFrame(HWND hWnd, String title, String targetTitle, Rectangle rect) {
        final int MAX_FILTER_NUM = 10;
        //HBITMAP oldbmp = (HBITMAP)GDI32.INSTANCE.SelectObject(mhdc, mbmp);

        // 対象のウィンドウの画像取得
        User32DLL.SetForegroundWindow(hWnd);
        BufferedImage buf = saveScreenshot.getScreenshot(hWnd, targetTitle, rect);
        //BufferedImage buf = saveScreenshot.getScreenshot(null, targetTitle, rect);

        // デバッグ用
        /*
        if (!(buf == null)){
            try{
                ImageIO.write(buf, "PNG", new File("./assets/applyfilterframe_test.png"));
            } catch(IOException ie){}
        }
        */

        //System.out.println(success);

        // フィルターパネル作成
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setPreferredSize(new Dimension(100, 1050));

        // 各フィルターのボタンをセット
        List<JButton> filterButtonList = new ArrayList<JButton>();
        for (int index = 0; index < MAX_FILTER_NUM;index++){
            JButton filterButton = new JButton();
            int w = 100;
            int h = (950-10*MAX_FILTER_NUM)/(MAX_FILTER_NUM);
            filterButton.setPreferredSize(new Dimension(w, h));
            filterButtonList.add(filterButton);
            filterPanel.add(filterButton, BorderLayout.NORTH);
        }

        // 画像用のパネル作成
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(1810, 1050));

        // 画像ラベル用意
        JLabel picLabel = new JLabel();
        picLabel.setBounds(new Rectangle(110, 110, 880, 880));
        ImageIcon img = new ImageIcon(buf);
        Image resizedImg = img.getImage().getScaledInstance(1810, 1050, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImg);
        picLabel.setIcon(resizedIcon);

        p.add(picLabel);

        //p.add(labelText, BorderLayout.WEST);

        // 各種パラメータ設定
        // WARNING: setTitleを末尾にしないと画像が表示されなくなる
        setTitle(title);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().add(filterPanel);
        getContentPane().add(p);
        getContentPane().setLayout(new FlowLayout());

        Thread thread = new Thread(){
            @Override
            public void run(){
                while(true){
                    BufferedImage buf = saveScreenshot.getScreenshot(hWnd, targetTitle, rect);
                    //BufferedImage buf = saveScreenshot.getScreenshot(hWnd, targetTitle, rect);

                    // TODO: bufに加工を加える
                    // TODO: ここで加工

                    ImageIcon img = new ImageIcon(buf);
                    Image resizedImg = img.getImage().getScaledInstance(1810, 1050, Image.SCALE_REPLICATE);
                    ImageIcon resizedIcon = new ImageIcon(resizedImg);

                    //UI更新
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            picLabel.setIcon(resizedIcon);
                        }
                    });

                    // Sleep
                    try {
                        // TODO: どれくらいSleepするべき?
                        sleep(100);
                    } catch (InterruptedException e) {}
                }
            }
        };
        thread.start();
    }
}

public class Main {
    public static void main(String[] args) {
        List<myWindow> windowList = WindowInfo.getWindowList();
        new WindowListFrame("Shared Window Avocado Mosaic", windowList);
    }
}
