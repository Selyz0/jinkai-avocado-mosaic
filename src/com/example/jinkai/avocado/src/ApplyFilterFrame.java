package com.example.jinkai.avocado.src;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;

import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import com.sun.jna.platform.win32.WinDef.HWND;

class ApplyFilterFrame extends JFrame implements ActionListener {
    final int MAX_FILTER_NUM = 10;
    public int windowHeight = 1000;
    public int windowWidth = windowHeight*1920/1080;
    int x, y;
    // クリック時の座標
    int x0, y0, x1, y1;
    int innerWidth, innerHeight;
    ImageIcon img;
    Insets insets;
    FilterFrame fframe;
    JPanel picPanel;
    
    ApplyFilterFrame(HWND hWnd, String title, String targetTitle, Rectangle rect) {
        fframe = new FilterFrame("Shared Window Avocado Mosaic (Components)");
        this.windowHeight = fframe.getWindowHeight();
        this.windowWidth = 1900 - fframe.getWindowWidth();
        this.x = fframe.getX() + fframe.getWidth() - 10;
        this.y = fframe.getY();

        //final int MAX_FILTER_NUM = 10;
        //HBITMAP oldbmp = (HBITMAP)GDI32.INSTANCE.SelectObject(mhdc, mbmp);
        setTitle(title);
        setVisible(true);
        setSize(this.windowWidth, this.windowHeight);
        setLocation(this.x, this.y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 対象のウィンドウの画像取得
        User32DLL.SetForegroundWindow(hWnd);
        BufferedImage buf = SaveScreenshot.getScreenshot(hWnd, targetTitle, rect);
        //BufferedImage buf = saveScreenshot.getScreenshot(null, targetTitle, rect);

        // デバッグ用
        /*
        if (!(buf == null)){
            try{
                ImageIO.write(buf, "PNG", new File("./assets/applyfilterframe_test.png"));
            } catch(IOException ie){}
        }
        */

        // 内側領域の大きさ取得
        insets = getInsets();
        innerWidth = getSize().width - insets.right - insets.left;
        innerHeight = getSize().height - insets.bottom - insets.top;

        System.out.println(insets.top + ", " + insets.bottom);
        System.out.println(innerWidth + ", "  + innerHeight);

        //System.out.println(success);
        // 画像用のパネル作成
        picPanel = new JPanel();
        picPanel.setLayout(null);
        picPanel.setBackground(Color.white);
        picPanel.setBounds(0, 0, innerWidth, innerHeight);

        // 画像ラベル用意
        JLabel picLabel = new JLabel();
        picLabel.setBackground(Color.white);
        picLabel.setBounds(new Rectangle(0, 0, picPanel.getWidth(), picPanel.getHeight()));
        img = new ImageIcon(buf);
        Image resizedImg = img.getImage().getScaledInstance(picLabel.getWidth(), picLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImg);
        picLabel.setIcon(resizedIcon);

        picPanel.add(picLabel);
        picPanel.addMouseListener(new MouseCheck());
		picPanel.addMouseMotionListener(new MouseCheck());

        //p.add(labelText, BorderLayout.WEST);

        // 各種パラメータ設定
        // WARNING: setTitleを末尾にしないと画像が表示されなくなる
        getContentPane().add(picPanel);
        getContentPane().setLayout(null);

        Thread thread = new Thread(){
            @Override
            public void run(){
                while(true){
                    BufferedImage buf = SaveScreenshot.getScreenshot(hWnd, targetTitle, rect);
                    //BufferedImage buf = saveScreenshot.getScreenshot(hWnd, targetTitle, rect);

                    // TODO: bufに加工を加える
                    // TODO: ここで加工

                    img = new ImageIcon(buf);
                    Image resizedImg = img.getImage().getScaledInstance(innerWidth, innerHeight, Image.SCALE_SMOOTH);
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
        //thread.start();
    }

    class MouseCheck extends MouseInputAdapter {
        public void mousePressed (MouseEvent me) {
            x0 = me.getX();
            y0 = me.getY();
        }

        public void mouseDragged (MouseEvent me) {
            System.out.print(me.getX());
            System.out.print(me.getY());
        }

        public void mouseReleased(MouseEvent me) {
            Graphics go = picPanel.getGraphics();
            x1 = me.getX();
            y1 = me.getY();
            go.setColor(Color.BLUE);

            // 始点、終点が必要な処理を記述
            System.out.println(x0 + ", " + y0 + " -> " + x1 + ", " + y1);
            go.drawOval(x0, y0, x1 - x0, y1 - y0);

            FilterButton targetButton = fframe.getEnabledButton();
            if(!(targetButton == null)){
                targetButton.performAction(img, x0, y0, x1, y1);
            }
        }
    }
    
    public void actionPerformed (ActionEvent e) {
        System.out.println("Clicked!");
		String cmd = e.getActionCommand();
		if (cmd.equals("open")) {
			//OpenImage();
		}
	}
}
