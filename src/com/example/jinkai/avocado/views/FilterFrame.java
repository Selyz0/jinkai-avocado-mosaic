package src.com.example.jinkai.avocado.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;

import java.util.*;
import javax.swing.*;

import src.com.example.jinkai.avocado.filters.*;

// TODO: 画像ウィンドウの移動を検出した際に、これを隣に移動する
public class FilterFrame extends JFrame {
    final int MAX_FILTER_NUM = 10;
    public int windowHeight = 1050;
    public int windowWidth = windowHeight / MAX_FILTER_NUM;
    final int padding = 5;
    int x, y;
    int innerWidth, innerHeight;
    Insets insets;
    JPanel filterPanel;
    List<FilterButton> filterButtonList = new ArrayList<FilterButton>();

    public int getWindowWidth() { return this.windowWidth; }
    public int getWindowHeight() { return this.windowHeight; }
    public int getX() { return this.x; }
    public int getY() { return this.y; }

    public FilterButton getEnabledButton(){
        for (int i=0;i < filterButtonList.size();i++){
            FilterButton filterButton = filterButtonList.get(i);
            boolean isSelected = filterButton.isSelected();
            if(isSelected) return filterButton;
        }

        return null;
    }

    public FilterFrame(String title) {
        // 各パラメータ設定
        this.x = 0;
        this.y = 10;
        setTitle(title);
        setLocation(this.x, this.y);
        setResizable(false);
        setVisible(true);
        setSize(this.windowWidth, this.windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 内側領域の大きさ取得
        insets = getInsets();
        innerWidth = getSize().width - insets.right - insets.left;
        innerHeight = getSize().height - insets.bottom - insets.top;

        // フィルターパネル作成
        filterPanel = new JPanel();
        filterPanel.setLayout(null);
        //filterPanel.setLayout(new FlowLayout());
        filterPanel.setBackground(Color.white);
        filterPanel.setPreferredSize(new Dimension(innerWidth, innerHeight));

        // 各フィルターのボタンをセット
        System.out.println("----");
        System.out.println("Window: " + windowWidth + ", " + windowHeight);
        for (int index = 0; index < MAX_FILTER_NUM;index++){
            int w = innerWidth - padding*2;
            int h = (innerHeight ) / MAX_FILTER_NUM - padding;
            int x = padding;
            int y = (h + padding)*index + padding/2;

            FilterSAM applyFilter = MyFilter::blur;
            FilterButton fButton = new FilterButton(applyFilter);
            fButton.setBounds(x, y, w, h);
            if(filterButtonList.size() > 0){
                fButton.setOtherButtons(filterButtonList);
            }

            // ボタン設定
            filterButtonList.add(fButton);
            filterPanel.add(fButton);

            // 対応するアイコン設定
            ImageIcon img = new ImageIcon("./src/com/example/jinkai/avocado/assets/wipe.png");
            Image resizedImg = img.getImage().getScaledInstance(fButton.getWidth(), fButton.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImg);
            fButton.setIcon(resizedIcon);

            // 自身を有効にしたときに他を無効にするため、他のボタンの情報も持つ
            for (int i = 0; i < index;i++){
                FilterButton targetButton = filterButtonList.get(i);
                targetButton.addOtherButtons(fButton);
            }
        }

        getContentPane().add(filterPanel);
        //getContentPane().setLayout(new FlowLayout());
    }
}
