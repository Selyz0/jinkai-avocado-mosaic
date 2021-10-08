package com.example.jinkai.avocado.models;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

public class MyImage {
    private final String path;

    public MyImage(String path) {
        this.path = new File(path).getAbsolutePath();
    }

    public Mat loadImage() {
        return Imgcodecs.imread(this.path);
    }

    public void writeImage(Mat image) {
        Imgcodecs.imwrite(this.path, image);
    }

}