package processing;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;

public class Processor {

    public static final int CAMERA_X_RESOLUTION = 1280;
    public static final int CAMERA_Y_RESOLUTION = 720;
    public static final double PIXELS_PER_DEGREE = 21.44;
    public static final double DEG_TO_RADIANS = 2.0 * 3.1415 / 360.0;

    protected NetworkTable smartDashboard;

    public Processor(NetworkTableInstance networkTableInstance) {
        smartDashboard = networkTableInstance.getTable("vision");
    }

    public void process(VisionPipeline pipeline) {

    }

}
