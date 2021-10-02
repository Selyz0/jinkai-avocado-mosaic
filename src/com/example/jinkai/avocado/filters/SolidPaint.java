package com.example.jinkai.avocado.filters;

import java.io.File;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;


public class SolidPaint {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static Mat loadImage(File imageFile) {
        return Imgcodecs.imread(imageFile.getAbsolutePath());
    }

    public static Mat applyFilter(Mat image, Rect rect, Scalar color) {
        Mat destImage = new Mat(image.rows(), image.cols(), CvType.CV_8UC3, color);
        Mat croppedImage = new Mat(image, rect);
        croppedImage.copyTo(destImage.rowRange(rect.y, rect.y + rect.height).colRange(rect.x, rect.x + rect.width));
        return destImage;
    }

    public static void writeImage(File imageFile, Mat image) {
        Imgcodecs.imwrite(imageFile.getAbsolutePath(), image);
    }
}

class Run {
    public static void main(String[] args) {
        // load the base image
        File srcImageFile = new File("assets/wallpaper-full-hd.jpg");
        Mat srcImage = SolidPaint.loadImage(srcImageFile);

        // prepare a fill-area
        Rect rect = new Rect(366, 537, 1138, 109);

        // prepare a fill-color (white)
        Scalar color = new Scalar(255, 255, 255);

        // write the filtered image
        File destImageFile = new File("assets/write.jpg");
        Mat destImage = SolidPaint.applyFilter(srcImage, rect, color);
        SolidPaint.writeImage(destImageFile, destImage);
    }
}