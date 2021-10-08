package src.com.example.jinkai.avocado.filters;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.*;
import static org.opencv.imgproc.Imgproc.*;

public class Wipe {
    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void setWipe() {
        Imgcodecs imageCodecs = new Imgcodecs();

        //Loading image
        Mat original = imageCodecs.imread("./assets/wallpaper-full-hd.jpg");

        //Video capturing
        VideoCapture capture = new VideoCapture(0);
        Mat picture = new Mat();
        capture.read(picture);

        //Resize camera
        Mat wipe = new Mat();
        Size scaleSize = new Size(480,360);
        resize(picture, wipe, scaleSize, 0, 0, INTER_AREA);
    
        Rect rect = new Rect(1440, 720, 480, 360);

        wipe.copyTo(original.rowRange(rect.y, rect.y + rect.height).colRange(rect.x, rect.x + rect.width));

        //Writing the image
        imageCodecs.imwrite("./assets/test_resaved6.png", original);
    }
}