package processing;

import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.HubTargetPipeline;

public class HubTargetProcessor {

    public static final String NETWORK_TABLE_NAME = "hub_target";

    public static final int RANGE = 50;

    private NetworkTable table;

    public HubTargetProcessor(NetworkTableInstance networkTableInstance) {
        NetworkTable smartDashboard = networkTableInstance.getTable("SmartDashboard");
        table = smartDashboard.getSubTable(NETWORK_TABLE_NAME);
    }

    public void process(VisionPipeline pipeline) {
        int x = 0;
        int y = 0;

        HubTargetPipeline hubTargetPipeline = (HubTargetPipeline) pipeline;

        System.out.println("Filter Counters - IN:" + hubTargetPipeline.findContoursOutput().size() + " OUT:"
                + hubTargetPipeline.filterContoursOutput().size());
        int i = 0;
        int beforeMean = 0;
        for (MatOfPoint contour : hubTargetPipeline.filterContoursOutput()) {
            Rect box = Imgproc.boundingRect(contour);
            beforeMean += box.y;
            i++;
        }

        beforeMean /= i;

        System.out.println(" beforeMain " + beforeMean);

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

        System.out.println(" X " + x + "; Y " + y );

        calcDistance(y);
        calcAngle(x);

    }

    public void calcAngle(int x) {

        double angle = 0.0;

        // TODO: Calculate Distance and Angle

        table.getEntry("angle").setDouble(angle);

    }

    public void calcDistance(int y) {

        double distance = 0.0;

        // TODO: Calculate Distance and Angle

        table.getEntry("distance").setDouble(distance);

    }
}
