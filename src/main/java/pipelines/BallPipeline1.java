package pipelines;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Hub Target Pipeline class.
 *
 * <p>
 * An OpenCV pipeline generated by GRIP.
 *
 * @author GRIP
 */
public class BallPipeline1 extends Pipeline {

  public Rect boundingRectBlue;
  public Rect boundingRectRed;

  Scalar blueColorLow;
  Scalar blueColorHigh;
  Scalar redColorLow;
  Scalar redColorHigh;

  public BallPipeline1(NetworkTableInstance networkTableInstance) {
    super(networkTableInstance, "BallPipeline");

    tuningValues.put("lowHueBlue", (double) 100);
    tuningValues.put("lowSatBlue", (double) 150);
    tuningValues.put("lowValBlue", (double) 0);
    tuningValues.put("highHueBlue", (double) 140);
    tuningValues.put("highSatBlue", (double) 255);
    tuningValues.put("highValBlue", (double) 255);

    tuningValues.put("lowHueRed", (double) 0);
    tuningValues.put("lowSatRed", (double) 136);
    tuningValues.put("lowValRed", (double) 8);
    tuningValues.put("highHueRed", (double) 6);
    tuningValues.put("highSatRed", (double) 255);
    tuningValues.put("highValRed", (double) 255);

    updatePipelineSettings();
  }

  protected void updatePipelineSettings() {
    blueColorLow = new Scalar(
        (int) tuningValues.get("lowHueBlue").doubleValue(),
        (int) tuningValues.get("lowSatBlue").doubleValue(),
        (int) tuningValues.get("lowValBlue").doubleValue());
    blueColorHigh = new Scalar(
        (int) tuningValues.get("highHueBlue").doubleValue(),
        (int) tuningValues.get("highSatBlue").doubleValue(),
        (int) tuningValues.get("highValBlue").doubleValue());

    redColorLow = new Scalar(
        (int) tuningValues.get("lowHueRed").doubleValue(),
        (int) tuningValues.get("lowSatRed").doubleValue(),
        (int) tuningValues.get("lowValRed").doubleValue());
    redColorHigh = new Scalar(
        (int) tuningValues.get("highHueRed").doubleValue(),
        (int) tuningValues.get("highSatRed").doubleValue(),
        (int) tuningValues.get("highValRed").doubleValue());
  }

  public Rect findBoundingRectCommon(Scalar colorLow, Scalar colorHigh, Mat frame) {
    Mat frameHSV = new Mat();
    Mat mask = new Mat();
    Mat hierarchy = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.cvtColor(frame, frameHSV, Imgproc.COLOR_BGR2HSV);

    Core.inRange(frameHSV, colorLow, colorHigh, mask);

    Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    Rect boundingRect = null;
    if (contours.size() > 0) {
      double contourArea;
      double maxContourArea = 0;
      int maxContourIdx = 0;
      for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
        contourArea = Imgproc.contourArea(contours.get(contourIdx));
        if (contourArea > maxContourArea) {
          maxContourArea = contourArea;
          maxContourIdx = contourIdx;
        }
      }
      boundingRect = Imgproc.boundingRect(contours.get(maxContourIdx));
    }
    return boundingRect;
  }

  public void findBoundingRectBlue(Mat frame) {
    boundingRectBlue = findBoundingRectCommon(blueColorLow, blueColorHigh, frame);
  }

  public void findBoundingRectRed(Mat frame) {
    boundingRectRed = findBoundingRectCommon(redColorLow, redColorHigh, frame);
  }

  public void findBoundingRect(Mat frame) {
    findBoundingRectBlue(frame);
    findBoundingRectRed(frame);
  }

  @Override
  public void process(Mat mat) {
	findBoundingRect(mat);
	
  }

}