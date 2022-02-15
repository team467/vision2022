package processing;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;

public class Processor {

    public static final double DEG_TO_RADIANS = 2.0 * 3.1415 / 360.0;

    public static final int CAMERA_MAX_X_RESOLUTION = 1280;
    public static final int CAMERA_MAX_Y_RESOLUTION = 720;
    private static final double PIXELS_PER_DEGREE_AT_MAX_RESOLUTION = 21.44;

    protected VideoSource camera;
    protected CvSource outputStream;

    protected NetworkTable smartDashboard;
    protected NetworkTable visionTable;

    protected int cameraFrameWidth;
    protected int cameraFrameHeight;

    protected double pixelPerXDegree;
    protected double pixelPerYDegree;

    public Processor(VideoSource camera, NetworkTableInstance networkTableInstance) {
        visionTable = networkTableInstance.getTable("vision");
        smartDashboard = networkTableInstance.getTable("SmartDashboard");
        this.camera = camera;
        VideoMode videoMode = camera.getVideoMode();
        cameraFrameWidth = videoMode.width;
        cameraFrameHeight = videoMode.height;
        outputStream = CameraServer.getInstance().putVideo(camera.getName() + " Processed",
                cameraFrameWidth, cameraFrameHeight);
        pixelPerXDegree = PIXELS_PER_DEGREE_AT_MAX_RESOLUTION * (double) cameraFrameWidth
                / (double) CAMERA_MAX_X_RESOLUTION;
        pixelPerYDegree = PIXELS_PER_DEGREE_AT_MAX_RESOLUTION * (double) cameraFrameHeight
                / (double) CAMERA_MAX_Y_RESOLUTION;
    }

    public void process(VisionPipeline pipeline) {

    }

}
