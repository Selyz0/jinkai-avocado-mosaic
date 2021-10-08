package src.com.example.jinkai.avocado.filters;

import src.com.example.jinkai.avocado.models.MyImage;
import org.opencv.core.*;


public class ImagePaint {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static Mat applyFilter(Mat srcImage, Rect rect, Mat paintImage) {
        Mat croppedImage = new Mat(srcImage, rect);
        croppedImage.copyTo(paintImage.rowRange(rect.y, rect.y + rect.height).colRange(rect.x, rect.x + rect.width));
        return paintImage;
    }

    public static MyImage paint() {
        // load the over-write image
        Mat srcImage = new MyImage("assets/wallpaper-full-hd.jpg").loadImage();

        // prepare a fill-area
        Rect rect = new Rect(366, 537, 1138, 109);

        // load the base image
        Mat paintImage = new MyImage("assets/wallpaper-full-hd2.jpg").loadImage();

        // write the filtered image
        Mat destImage = ImagePaint.applyFilter(srcImage, rect, paintImage);
        MyImage destImageFile = new MyImage("assets/write-image-paint.jpg");
        destImageFile.writeImage(destImage);

        return destImageFile;
    }
}