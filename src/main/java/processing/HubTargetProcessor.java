package processing;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.HubTargetPipeline;

public class HubTargetProcessor extends Processor {

    private long frameCount = 0;

    public HubTargetProcessor(VideoSource camera, NetworkTableInstance networkTableInstance) {
        super(camera, networkTableInstance, "HubTarget");
        tuningValues.put("hubTargetHeightFt", 8.700);
        tuningValues.put("cameraHeightFt", 32.0 / 12.0);
        tuningValues.put("cameraUpAngleDeg", 45.0);
        tuningValues.put("cameraTurnOffsetDeg", 0.0);
        tuningValues.put("yMidpointTolerance", 50.0);
    }

    public void process(VisionPipeline pipeline) {
        int x = 0;
        int y = 0;

        HubTargetPipeline hubTargetPipeline = (HubTargetPipeline) pipeline;

        if (hubTargetPipeline.filterContoursOutput().size() != 0) {
            int i = 0;
            int beforeMean = 0;
            for (MatOfPoint contour : hubTargetPipeline.filterContoursOutput()) {
                Rect box = Imgproc.boundingRect(contour);
                beforeMean += box.y;
                i++;
            }

            if (i != 0) {
                beforeMean /= i;

                i = 0;
                Mat mat = hubTargetPipeline.hslThresholdOutput();
                for (MatOfPoint contour : hubTargetPipeline.filterContoursOutput()) {
                    Rect box = Imgproc.boundingRect(contour);
                    if (Math.abs(beforeMean - box.y) < tuningValues.get("yMidpointTolerance")) {
                        x += box.x;
                        y += box.y;
                        i++;
                        Imgproc.rectangle(mat,
                                new Point(box.x, box.y),
                                new Point(box.x + box.width, box.y + box.height),
                                new Scalar(255, 255, 255), 5);
                    }
                }

                if (i != 0) {
                    x /= i;
                    y /= i;

                    calcDistance(y);
                    calcAngle(x);
                    table.getEntry("isValid").setBoolean(true);
                    table.getEntry("frameCount").setDouble(++frameCount);

                }
            }

        } else {
            table.getEntry("isValid").setBoolean(false);
        }

    }

    public void calcAngle(int x) {

        double angle = (x - cameraFrameWidth / 2.0)
                / pixelPerXDegree
                + tuningValues.get("cameraTurnOffsetDeg");
        table.getEntry("angle").setDouble(angle);
    }

    public void calcDistance(int y) {

        double distance = (tuningValues.get("hubTargetHeightFt") - tuningValues.get("cameraHeightFt"))
                / Math.tan((((cameraFrameHeight / 2.0) - y)
                        / pixelPerYDegree
                        + tuningValues.get("cameraUpAngleDeg"))
                        * DEG_TO_RADIANS);
        table.getEntry("distance").setDouble(distance);

    }
}
