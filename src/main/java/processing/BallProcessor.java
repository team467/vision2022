package processing;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.BallPipeline;

public class BallProcessor extends Processor {

    public static final String NETWORK_TABLE_NAME = "balls";
    public static final double BALL_HEIGHT_IN = 9.5;
    public static final double CAMERA_HEIGHT_IN = 16.5;
    public static final double DOWN_ANGLE_DEG = 20.0;
    public static final double TURN_ANGLE_OFFSET_DEG = 0.0;

    private NetworkTable table;

    public BallProcessor(NetworkTableInstance networkTableInstance) {
        super(networkTableInstance);
        table = smartDashboard.getSubTable(NETWORK_TABLE_NAME);
    }

    public void process(VisionPipeline pipeline) {
        BallPipeline ballPipeline = (BallPipeline) pipeline;
        distance(1);
        angle(1);
        isRed(true);
    }

    private void distance(int y) {
        double distance = (CAMERA_HEIGHT_IN - BALL_HEIGHT_IN) *
                Math.tan((90.0 - DOWN_ANGLE_DEG
                        - (y - CAMERA_Y_RESOLUTION / 2.0)
                                / PIXELS_PER_DEGREE)
                        * DEG_TO_RADIANS);
        table.getEntry("distance").setDouble(distance);
    }

    private void angle(int x) {
        double angle = (x - CAMERA_X_RESOLUTION / 2.0)
                / PIXELS_PER_DEGREE
                + TURN_ANGLE_OFFSET_DEG;
        table.getEntry("angle").setDouble(angle);
    }

    private void isRed(boolean isRed) {
        table.getEntry("isRed").setBoolean(isRed);

    }

}
