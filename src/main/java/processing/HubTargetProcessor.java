package processing;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.HubTargetPipeline;

public class HubTargetProcessor extends Processor {

    public static final String NETWORK_TABLE_NAME = "hub_target";
    public static final double TARGET_HEIGHT_FT = 8.700;
    public static final double CAMERA_HEIGHT_FT = 2.875;
    public static final double UP_ANGLE_DEG = 45.0;
    public static final double TURN_ANGLE_OFFSET_DEG = 0.0;

    private NetworkTable table;

    public HubTargetProcessor(NetworkTableInstance networkTableInstance) {
        super(networkTableInstance);
        table = smartDashboard.getSubTable(NETWORK_TABLE_NAME);
    }

    public void process(VisionPipeline pipeline) {
        HubTargetPipeline hubTargetPipeline = (HubTargetPipeline) pipeline;
        distance(1);
        angle(1);
    }

    private void distance(int y) {
        double distance = (TARGET_HEIGHT_FT - CAMERA_HEIGHT_FT) /
                Math.tan((((CAMERA_Y_RESOLUTION / 2.0) - y)
                        / PIXELS_PER_DEGREE + UP_ANGLE_DEG)
                        * DEG_TO_RADIANS);
        table.getEntry("distance").setDouble(distance);
    }

    private void angle(int x) {
        double angle = (x - CAMERA_X_RESOLUTION / 2.0)
                / PIXELS_PER_DEGREE
                + TURN_ANGLE_OFFSET_DEG;
        table.getEntry("angle").setDouble(angle);
    }

}
