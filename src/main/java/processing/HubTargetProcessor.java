package processing;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.HubTargetPipeline;

public class HubTargetProcessor extends Processor {

    public static final String NETWORK_TABLE_NAME = "HubTarget";
    public static final double TARGET_HEIGHT_FT_DEFAULT = 8.700;
    public static final double CAMERA_HEIGHT_FT_DEFAULT = 2.875;
    public static final double UP_ANGLE_DEG_DEFAULT = 45.0;
    public static final double TURN_ANGLE_OFFSET_DEG_DEFAULT = 0.0;
    public static final int Y_MIDPOINT_TOLERANCE = 50;

    private double hubTargetHeightFt;
    private double cameraHeightFt;
    private double cameraUpAngleDeg;
    private double cameraTurnOffsetDeg;

    private int yMidpointTolerance;

    private NetworkTable table;

    private NetworkTableEntry ntHubTargetHeight;
    private NetworkTableEntry ntCameraHeight;
    private NetworkTableEntry ntCameraDownAngle;
    private NetworkTableEntry ntCameraTurnOffset;
    private NetworkTableEntry ntYMidpointTolerance;

    public HubTargetProcessor(VideoSource camera, NetworkTableInstance networkTableInstance) {
        super(camera, networkTableInstance);
        table = visionTable.getSubTable(NETWORK_TABLE_NAME);

        ntHubTargetHeight = table.getEntry("HubTargetHeightFeet");
        hubTargetHeightFt = TARGET_HEIGHT_FT_DEFAULT;
        ntHubTargetHeight.setDouble(hubTargetHeightFt);
        ntHubTargetHeight.addListener(event -> {
            hubTargetHeightFt = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntCameraHeight = table.getEntry("CameraHeightFeet");
        cameraHeightFt = CAMERA_HEIGHT_FT_DEFAULT;
        ntCameraHeight.setDouble(cameraHeightFt);
        ntCameraHeight.addListener(event -> {
            cameraHeightFt = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntCameraDownAngle = table.getEntry("CameraUpAngleDegrees");
        cameraUpAngleDeg = UP_ANGLE_DEG_DEFAULT;
        ntCameraDownAngle.setDouble(cameraUpAngleDeg);
        ntCameraDownAngle.addListener(event -> {
            cameraUpAngleDeg = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntCameraTurnOffset = table.getEntry("CameraTurnOffsetDegrees");
        cameraTurnOffsetDeg = TURN_ANGLE_OFFSET_DEG_DEFAULT;
        ntCameraTurnOffset.setDouble(cameraTurnOffsetDeg);
        ntCameraTurnOffset.addListener(event -> {
            cameraTurnOffsetDeg = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntYMidpointTolerance = table.getEntry("Y_MidpointTolerance");
        yMidpointTolerance = Y_MIDPOINT_TOLERANCE;
        ntYMidpointTolerance.setDouble(yMidpointTolerance);
        ntYMidpointTolerance.addListener(event -> {
            yMidpointTolerance = (int) event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    }

    public void process(VisionPipeline pipeline) {
        int x = 0;
        int y = 0;

        HubTargetPipeline hubTargetPipeline = (HubTargetPipeline) pipeline;

        // Mat inputStream = pipeline.

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
                    if (Math.abs(beforeMean - box.y) < yMidpointTolerance) {
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
                    outputStream.putFrame(mat);
                }
            }

        } else {
            table.getEntry("angle").setDouble(0.0);
            table.getEntry("distance").setDouble(0.0);
            table.getEntry("isValid").setBoolean(false);
        }

    }

    public void calcAngle(int x) {

        double angle = (x - cameraFrameWidth / 2.0)
                / pixelPerXDegree
                + cameraTurnOffsetDeg;
        table.getEntry("angle").setDouble(angle);
    }

    public void calcDistance(int y) {

        double distance = (hubTargetHeightFt - cameraHeightFt)
                / Math.tan((((cameraFrameHeight / 2.0) - y) / pixelPerYDegree + cameraUpAngleDeg) * DEG_TO_RADIANS);
        table.getEntry("distance").setDouble(distance);

    }
}
