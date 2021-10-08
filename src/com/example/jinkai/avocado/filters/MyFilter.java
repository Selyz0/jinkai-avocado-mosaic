package src.com.example.jinkai.avocado.filters;

import java.awt.Image;
import javax.swing.ImageIcon;

public class MyFilter {
    public static ImageIcon blur(ImageIcon img, int x0, int y0, int x1, int y1){
        Image buf = img.getImage();
        System.out.println("Blur here");
        return img;
    }

    public static void blurTest(){
        Blur.applyBlur();
    }

    public static void setWipe() {
        Wipe.setWipe();
    }
}
