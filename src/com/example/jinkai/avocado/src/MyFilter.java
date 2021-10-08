package com.example.jinkai.avocado.src;

import java.awt.Image;
import javax.swing.ImageIcon;

interface filterSAM {
	public abstract ImageIcon filter(ImageIcon img, int x0, int y0, int x1, int y1);
}

public class MyFilter {
    public static ImageIcon blur(ImageIcon img, int x0, int y0, int x1, int y1){
        Image buf = img.getImage();
        System.out.println("Blur here");
        return img;
    }
}
