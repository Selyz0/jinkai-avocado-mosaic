package com.example.jinkai.avocado.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;
import javax.swing.*;

import com.example.jinkai.avocado.filters.*;

public class FilterButton extends JToggleButton implements ActionListener{
    // Filterクラスでメソッドをまとめておく
    int buttonId;
    FilterSAM applyFilter;
    List<FilterButton> otherButtons = new ArrayList<>();

    public int getId(){return buttonId; }

    FilterButton (int id, FilterSAM filterMethod) {
        this.buttonId = id;
        applyFilter = filterMethod;
        addActionListener(this);
    }

    public void setOtherButtons(List<FilterButton> buttons){
        otherButtons = buttons;
    }

    public void addOtherButtons(FilterButton button){
        otherButtons.add(button);
    }

    public void setOtherButtonNotSelected() {
        for (int index = 0;index < otherButtons.size();index++){
            FilterButton targetButton = otherButtons.get(index);
            if (!(targetButton == this)){
                targetButton.setSelected(false);
            }
        }
    }

    public ImageIcon performAction(ImageIcon img, int x0, int y0, int x1, int y1) {
        // 画像に加工
        return applyFilter.filter(img, x0, y0, x1, y1);
    }

    public void actionPerformed (ActionEvent e) {
		String cmd = e.getActionCommand();
        System.out.println("pressed!" + cmd);

        setOtherButtonNotSelected();
	}
}
