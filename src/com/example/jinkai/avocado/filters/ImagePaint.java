package com.example.jinkai.avocado.filters;

import com.example.jinkai.avocado.models.Image;
import org.opencv.core.*;


public class ImagePaint {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static Mat applyFilter(Mat srcImage, Rect rect, Mat paintImage) {
        Mat croppedImage = new Mat(srcImage, rect);
        croppedImage.copyTo(paintImage.rowRange(rect.y, rect.y + rect.height).colRange(rect.x, rect.x + rect.width));
        return paintImage;
    }
}

class RunImagePaint {
    public static void main(String[] args) {
        // load the over-write image
        Mat srcImage = new Image("assets/wallpaper-full-hd.jpg").loadImage();

        // prepare a fill-area
        Rect rect = new Rect(366, 537, 1138, 109);

        // load the base image
        Mat paintImage = new Image("assets/wallpaper-full-hd2.jpg").loadImage();

        // write the filtered image
        Mat destImage = ImagePaint.applyFilter(srcImage, rect, paintImage);
        Image destImageFile = new Image("assets/write-image-paint.jpg");
        destImageFile.writeImage(destImage);
    }
}