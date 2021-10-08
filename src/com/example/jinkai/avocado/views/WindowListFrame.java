package com.example.jinkai.avocado.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;

import com.sun.jna.Library;
import com.sun.jna.Native;

interface Kernel32 extends Library {
    Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);
    void Sleep(int dwMilliseconds);
}

public class WindowListFrame extends JFrame {
    private int width = 1200;
    private int height = 500;
    private int padding = 10;
    int panelWidth, panelHeight;
    private List<MyWindow> windowList;
    private List<MyWindowPanel> panelList = new ArrayList<MyWindowPanel>();

    public List<MyWindowPanel> getPanelList(){ return this.panelList; }

    private void calcParameters(final int MAX_ROW_NUM){
        panelWidth = (width - padding*(MAX_ROW_NUM+1)) / MAX_ROW_NUM;
        if(windowList.size() % MAX_ROW_NUM > 0){
            panelHeight = (height - padding*(windowList.size())) / (windowList.size() / MAX_ROW_NUM + 1);
        } else {
            panelHeight = (height - padding*(windowList.size())) / (windowList.size() / MAX_ROW_NUM);
        }
    }

    public WindowListFrame(String title, List<MyWindow> windowList) {
        final int MAX_ROW_NUM = windowList.size() / 2 + windowList.size() % 2;
        this.windowList = windowList;
        calcParameters(MAX_ROW_NUM);

        for (int index = 0; index < windowList.size(); index++){
            MyWindow window = windowList.get(index);
            
            int x = (padding + this.panelWidth) * (index % MAX_ROW_NUM);
            int y = (padding + this.panelHeight) * (index / MAX_ROW_NUM);

            // コンポーネント初期化
            MyWindowPanel p = new MyWindowPanel(window.getHWND(), window.getTitle(), window.getRectangle());
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
                MyWindowPanel p = panelList.get(index);
                if(p == e.getSource()){
                    new ApplyFilterFrame(p.getHWND(), "Avocado Safe Sharing", p.getTitle(), p.getRectangle());
                }
            }
        }
    }
}
