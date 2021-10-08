package src.com.example.jinkai.avocado.filters;

import src.com.example.jinkai.avocado.models.Image;
import org.opencv.core.*;


public class SolidPaint {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static Mat applyFilter(Mat image, Rect rect, Scalar color) {
        Mat destImage = new Mat(image.rows(), image.cols(), CvType.CV_8UC3, color);
        Mat croppedImage = new Mat(image, rect);
        croppedImage.copyTo(destImage.rowRange(rect.y, rect.y + rect.height).colRange(rect.x, rect.x + rect.width));
        return destImage;
    }

    public static Image fill() {
        // load the base image
        Mat srcImage = new Image("assets/wallpaper-full-hd.jpg").loadImage();

        // prepare a fill-area
        Rect rect = new Rect(366, 537, 1138, 109);

        // prepare a fill-color (white)
        Scalar color = new Scalar(255, 255, 255);

        // write the filtered image
        Mat destImage = SolidPaint.applyFilter(srcImage, rect, color);
        Image destImageFile = new Image("assets/write-solid-paint.jpg");
        destImageFile.writeImage(destImage);

        return destImageFile;
    }
}