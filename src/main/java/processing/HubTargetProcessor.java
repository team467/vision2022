package processing;

import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.HubTargetPipeline;
public class HubTargetProcessor extends Processor {

    public static final String NETWORK_TABLE_NAME = "hub_target";
    public static final double TARGET_HEIGHT_FT = 1.4583; // 8.700;
    public static final double CAMERA_HEIGHT_FT = 0.83; // 2.875;
    public static final double UP_ANGLE_DEG = 0.0;// 45.0;
    public static final double TURN_ANGLE_OFFSET_DEG = 0.0;

    public static final int RANGE = 50;

    private NetworkTable table;

    public HubTargetProcessor(NetworkTableInstance networkTableInstance) {
        super(networkTableInstance);
        table = smartDashboard.getSubTable(NETWORK_TABLE_NAME);
    }

    public void process(VisionPipeline pipeline) {
        int x = 0;
        int y = 0;

        HubTargetPipeline hubTargetPipeline = (HubTargetPipeline) pipeline;

        System.out.println("Filter Counters - IN:" + hubTargetPipeline.findContoursOutput().size() + " OUT:"
                + hubTargetPipeline.filterContoursOutput().size());

        if (hubTargetPipeline.filterContoursOutput().size() != 0) {
            int i = 0;
            int beforeMean = 0;
            for (MatOfPoint contour : hubTargetPipeline.filterContoursOutput()) {
                Rect box = Imgproc.boundingRect(contour);
                beforeMean += box.y;
                i++;
            }

            beforeMean /= i;

            i = 0;
            for (MatOfPoint contour : hubTargetPipeline.filterContoursOutput()) {
                Rect box = Imgproc.boundingRect(contour);
                if (Math.abs(beforeMean - box.y) < RANGE) {
                    x += box.x;
                    y += box.y;
                    i++;
                }
            }

            x /= i;
            y /= i;

            calcDistance(y);
            calcAngle(x);
            table.getEntry("isValid").setBoolean(true);

        } else {

            table.getEntry("angle").setDouble(0.0);
            table.getEntry("distance").setDouble(0.0);
            table.getEntry("isValid").setBoolean(false);

        }

    }

    public void calcAngle(int x) {

        double angle = (x - CAMERA_X_RESOLUTION / 2.0) / PIXELS_PER_DEGREE + TURN_ANGLE_OFFSET_DEG;
        table.getEntry("angle").setDouble(angle);
        System.out.println(" Angle " + angle);

    }

    public void calcDistance(int y) {

        double distance = (TARGET_HEIGHT_FT - CAMERA_HEIGHT_FT)
                / Math.tan((((CAMERA_Y_RESOLUTION / 2.0) - y) / PIXELS_PER_DEGREE + UP_ANGLE_DEG) * DEG_TO_RADIANS);
        table.getEntry("distance").setDouble(distance);
        System.out.println(" Distance " + distance);

    }
}
