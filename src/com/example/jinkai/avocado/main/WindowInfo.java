package com.example.jinkai.avocado.main;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.HWND;

public class WindowInfo {
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

    public static List<MyWindow> getWindowList() {
        List<DesktopWindow> allWindows = WindowUtils.getAllWindows(true);
        List<MyWindow> windowList = new ArrayList<>();

        // モニタの情報を取得
        // # ひとまずハードコーディング
        final int width = 1920;
        final int height = 1080;
        //final int HWND_TOP = -1;
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
            BufferedImage buf = SaveScreenshot.capture(hWnd);
            //BufferedImage buf = saveScreenshot.getScreenshot(hWnd, title, rect);
            //success = User32DLL.SetWindowPos(hWnd, HWND_TOP, rect.x, rect.y, rect.width, rect.height, 16384);
            
            if (!(buf == null)){
                try{
                    ImageIO.write(buf, "PNG", new File("./src/com/example/jinkai/avocado/assets/capture.png"));
                } catch(IOException ie){}
            }

            windowList.add(new MyWindow(hWnd, title, visible, rect, buf));
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
