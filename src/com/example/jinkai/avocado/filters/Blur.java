package com.example.jinkai.avocado.filters;

import java.nio.ByteBuffer; 
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.core.Scalar;

import org.opencv.core.Core;
import org.opencv.core.MatOfByte;

public class Blur {
    static void paste(Mat src, Mat dst, int x, int y, int width, int height){
        Mat resized_img = new Mat();
        Imgproc.resize(src, resized_img, new Size(width, height));

        if (!(x >= dst.cols() || y >= dst.rows())){
            int w = (x >= 0) ? Math.min(dst.cols()-x, resized_img.cols()) : Math.min(Math.max(resized_img.cols()+x, 0), dst.cols());
            int h = (y >= 0) ? Math.min(dst.rows()-y, resized_img.rows()) : Math.min(Math.max(resized_img.rows()+y, 0), dst.rows());
            int u = (x >= 0) ? 0 : Math.min(-x, resized_img.cols()-1);
            int v = (y >= 0) ? 0 : Math.min(-y, resized_img.rows()-1);
            int px = Math.max(x, 0);
            int py = Math.max(y, 0);

            Mat dst_roi = new Mat(dst, new Rect(px, py, w, h));
            Mat resized_roi = new Mat(resized_img, new Rect(u, v, w, h));
            resized_roi.copyTo(dst_roi);
        }
    }

    public static Mat applyBlur(Mat src, int x0, int y0, int x1, int y1){
        // ぼかしたい領域を切り出す
        Rect roi = new Rect(x0, y0, x1-x0, y1-y0);
        System.out.println(x0 + ", " + y0 + ", " + (x1-x0) + ", " + (y1-y0));
        System.out.println(src.rows() + ", " + src.cols());
        Mat src_roi = new Mat(src, roi);

        // 切り出した領域をぼかす
        Mat blur_roi = new Mat();
        Imgproc.boxFilter(src_roi, blur_roi, src.depth(), new Size(15, 15));

        // 合成画像を生成する
        int B = 255, G = 255, R = 255;
        Scalar sc = new Scalar(B, G, R);
        Mat dst = new Mat(src.rows(), src.cols(), src.type(), sc);

        paste(src, dst, 0, 0, src.width(), src.height());
        paste(blur_roi, dst, x0, y0, x1-x0, y1-y0);

        return dst;
    }
}