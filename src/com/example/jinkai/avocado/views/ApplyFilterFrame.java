package com.example.jinkai.avocado.views;

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

import com.example.jinkai.avocado.main.*;
import com.example.jinkai.avocado.filters.*;

class ApplyFilterFrame extends JFrame implements ActionListener {
    final int MAX_FILTER_NUM = 10;
    public int windowHeight;
    public int windowWidth;
    float resizeWidth, resizeHeight;
    int x, y;
    // クリック時の座標
    int x0, y0, x1, y1;
    int innerWidth, innerHeight;
    int appliedButtonId = -1, applied_x0, applied_x1, applied_y0, applied_y1;
    BufferedImage buf;
    Image resizedImg;
    ImageIcon img;
    Insets insets;
    FilterFrame fframe;
    JLabel picLabel;
    JPanel picPanel;
    
    ApplyFilterFrame(HWND hWnd, String title, String targetTitle, Rectangle rect) {
        fframe = new FilterFrame("Avocado Safe Sharing (Components)");
        this.windowHeight = 1080;
        this.windowWidth = 1920 - fframe.getWindowWidth();
        this.x = fframe.getX() + fframe.getWidth() - 15;
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
        picLabel = new JLabel();
        picLabel.setBackground(Color.white);
        picLabel.setBounds(new Rectangle(0, 0, picPanel.getWidth(), picPanel.getHeight()));
        picLabel.setHorizontalAlignment(JLabel.CENTER);

        img = new ImageIcon(buf);
        if(img.getIconHeight()/img.getIconWidth() > picLabel.getHeight()/picLabel.getWidth()){
            resizeWidth = picLabel.getWidth();
            float ratio = (float)picLabel.getWidth()/img.getIconWidth();
            resizeHeight = img.getIconHeight()*ratio;
        } else {
            resizeHeight = picLabel.getHeight();
            float ratio = (float)picLabel.getHeight()/img.getIconHeight();
            resizeWidth = img.getIconWidth()*ratio;
        }
        System.out.println("resize:" + img.getIconWidth() + ", " + img.getIconHeight());
        System.out.println("resize:" + picLabel.getWidth() + ", " + picLabel.getHeight());
        System.out.println("resize:" + resizeWidth + ", " + resizeHeight);
        resizedImg = img.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
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
                    if(img.getIconHeight()/img.getIconWidth() > picLabel.getHeight()/picLabel.getWidth()){
                        resizeWidth = picLabel.getWidth();
                        float ratio = (float)picLabel.getWidth()/img.getIconWidth();
                        resizeHeight = img.getIconHeight()*ratio;
                    } else {
                        resizeHeight = picLabel.getHeight();
                        float ratio = (float)picLabel.getHeight()/img.getIconHeight();
                        resizeWidth = img.getIconWidth()*ratio;
                    }
                    System.out.println("resize:" + img.getIconWidth() + ", " + img.getIconHeight());
                    System.out.println("resize:" + picLabel.getWidth() + ", " + picLabel.getHeight());
                    System.out.println("resize:" + resizeWidth + ", " + resizeHeight);
                    resizedImg = img.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
                    //ImageIcon resizedIcon = new ImageIcon(resizedImg);

                    //UI更新
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            resizedImg = applyFilter(appliedButtonId);
                            ImageIcon resizedIcon = new ImageIcon(resizedImg);
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

    public Image applyFilter(int buttonId){
        ImageIcon src = new ImageIcon(resizedImg);

        // ぼかし
        if(buttonId == 0){
            ImageIcon dstImg = MyFilter.blur(src, applied_x0, applied_y0, applied_x1, applied_y1);
            //ImageIcon dstImg = targetButton.performAction();
            resizedImg = dstImg.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
        } else if (buttonId == 1){
            System.out.println("fill");
            ImageIcon dstImg = MyFilter.fill(src, applied_x0, applied_y0, applied_x1, applied_y1);
            //ImageIcon dstImg = targetButton.performAction();
            resizedImg = dstImg.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
        } else if (buttonId == 2){
            ImageIcon dstImg = MyFilter.paintImage(src, applied_x0, applied_y0, applied_x1, applied_y1);
            //ImageIcon dstImg = targetButton.performAction();
            resizedImg = dstImg.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
        } else if (buttonId == 3){
            ImageIcon dstImg = MyFilter.setWipe(img, applied_x0, applied_y0+insets.top+5, applied_x1, applied_y1+insets.top+5);
            resizedImg = dstImg.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
        }

        return resizedImg;
    }

    class MouseCheck extends MouseInputAdapter {
        public void mousePressed (MouseEvent me) {
            x0 = me.getX()-(picLabel.getWidth()-resizedImg.getWidth(null))/2;
            y0 = me.getY();
        }

        public void mouseDragged (MouseEvent me) {
        }

        public void mouseReleased(MouseEvent me) {
            Graphics go = picPanel.getGraphics();
            x1 = me.getX()-(picLabel.getWidth()-resizedImg.getWidth(null))/2;
            y1 = me.getY();
            go.setColor(Color.BLUE);

            // 始点、終点が必要な処理を記述
            System.out.println(x0 + ", " + y0 + " -> " + x1 + ", " + y1);
            //go.drawOval(x0, y0, x1 - x0, y1 - y0);

            FilterButton targetButton = fframe.getEnabledButton();
            if(!(targetButton == null)){
                appliedButtonId = targetButton.getId();

                if(x0 > x1){
                    int x_tmp = x0;
                    x0 = x1;
                    x1 = x_tmp;
                }
                if(y0 > y1){
                    int y_tmp = y0;
                    y0 = y1;
                    y1 = y_tmp;
                }

                applied_x0 = x0; applied_x1 = x1;
                applied_y0 = y0; applied_y1 = y1;

                ImageIcon src = new ImageIcon(resizedImg);

                // ぼかし
                if(targetButton.getId() == 0){
                    ImageIcon dstImg = MyFilter.blur(src, x0, y0, x1, y1);
                    //ImageIcon dstImg = targetButton.performAction();
                    resizedImg = dstImg.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
                    ImageIcon resizedIcon = new ImageIcon(resizedImg);
                    picLabel.setIcon(resizedIcon);
                } else if (targetButton.getId() == 1){
                    System.out.println("fill");
                    ImageIcon dstImg = MyFilter.fill(src, x0, y0, x1, y1);
                    //ImageIcon dstImg = targetButton.performAction();
                    resizedImg = dstImg.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
                    ImageIcon resizedIcon = new ImageIcon(resizedImg);
                    picLabel.setIcon(resizedIcon);
                } else if (targetButton.getId() == 2){
                    ImageIcon dstImg = MyFilter.paintImage(src, x0, y0, x1, y1);
                    //ImageIcon dstImg = targetButton.performAction();
                    resizedImg = dstImg.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
                    ImageIcon resizedIcon = new ImageIcon(resizedImg);
                    picLabel.setIcon(resizedIcon);
                } else if (targetButton.getId() == 3){
                    ImageIcon dstImg = MyFilter.setWipe(img, x0, y0+insets.top+5, x1, y1+insets.top+5);
                    resizedImg = dstImg.getImage().getScaledInstance((int)resizeWidth, (int)resizeHeight, Image.SCALE_SMOOTH);
                    ImageIcon resizedIcon = new ImageIcon(resizedImg);
                    picLabel.setIcon(resizedIcon);
                }
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
