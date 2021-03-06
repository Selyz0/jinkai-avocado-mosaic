package com.example.jinkai.avocado.filters;

import com.example.jinkai.avocado.models.MyImage;
import org.opencv.core.*;


public class SolidPaint {

    public static Mat applyFilter(Mat image, Rect rect, Scalar color) {
        Mat destImage = new Mat(image.rows(), image.cols(), CvType.CV_8UC3, color);
        Mat croppedImage = new Mat(image, rect);
        croppedImage.copyTo(destImage.rowRange(rect.y, rect.y + rect.height).colRange(rect.x, rect.x + rect.width));
        return destImage;
    }

    public static MyImage fill() {
        // load the base image
        Mat srcImage = new MyImage("assets/wallpaper-full-hd.jpg").loadImage();

        // prepare a fill-area
        Rect rect = new Rect(366, 537, 1138, 109);

        // prepare a fill-color (white)
        Scalar color = new Scalar(255, 255, 255);

        // write the filtered image
        Mat destImage = SolidPaint.applyFilter(srcImage, rect, color);
        MyImage destImageFile = new MyImage("assets/write-solid-paint.jpg");
        destImageFile.writeImage(destImage);

        return destImageFile;
    }
}