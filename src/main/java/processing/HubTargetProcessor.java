package processing;

import java.util.ArrayList;
import java.util.Collections;

import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.HubTargetPipeline;

public class HubTargetProcessor {

    public static final String NETWORK_TABLE_NAME = "hub_target";

    private NetworkTable table;

    public HubTargetProcessor(NetworkTableInstance networkTableInstance) {
        NetworkTable smartDashboard = networkTableInstance.getTable("SmartDashboard");
        table = smartDashboard.getSubTable(NETWORK_TABLE_NAME);
    }

    public void process(VisionPipeline pipeline) {
        int x = 0;
        int y = 0;

        HubTargetPipeline hubTargetPipeline = (HubTargetPipeline) pipeline;

        // Scanner scanner = new Scanner(System.in);

        ArrayList<Integer> yArray = new ArrayList<Integer>();
        System.out.println("Filter Counters - IN:" + hubTargetPipeline.findContoursOutput().size() + " OUT:"
                + hubTargetPipeline.filterContoursOutput().size());
        int i = 0;
        for (MatOfPoint contour : hubTargetPipeline.filterContoursOutput()) {
            Rect box = Imgproc.boundingRect(contour);

           // if (contour.size().height == 1 && contour.size().width == 1) {

                System.out.println(" (x,y) =  (" + box.x + "," + box.y + ") " + " (width, height) =  (" + box.width
                        + "," + box.height + ") " + " Contour Size = " + contour.size());

                yArray.add(box.y);

        //    } // System.out.println(contour.height() + "Done");

            if (hubTargetPipeline.filterContoursOutput().isEmpty()) {
                System.out.println("darn");
            }
        }

        Collections.sort(yArray);
        if (yArray.size() > 3) {

        }

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
