package processing;

import org.opencv.core.Rect;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.BallPipeline;

public class BallProcessor extends Processor {

    public static final String NETWORK_TABLE_NAME = "BallTracking";
    public static final double BALL_HEIGHT_IN_DEFAULT = 9.5;
    public static final double CAMERA_HEIGHT_IN_DEFAULT = 16.5;
    public static final double DOWN_ANGLE_DEG_DEFAULT = 20.0;
    public static final double TURN_ANGLE_OFFSET_DEG_DEFAULT = 0.0;
    public static final int MIN_BOUNDING_RECT_WIDTH_DEFAULT = 25;
    public static final double BOUNDING_RECT_RATIO_DEFAULT = 1.1;
    public static final double BOUNDING_RECT_RATIO_TOLERANCE_DEFAULT = 0.3;

    private double ballHeight;
    private double cameraHeight;
    private double cameraDownAngleDeg;
    private double cameraTurnOffsetDeg;

    private int minBoundingRectWidth;
    private double boundingRectRatio;
    private double boundingRectRatioTolerance;

    private NetworkTable redTable;
    private NetworkTableEntry hasRed;
    private NetworkTableEntry redDistance;
    private NetworkTableEntry redAngle;
    private NetworkTable blueTable;
    private NetworkTableEntry hasBlue;
    private NetworkTableEntry blueDistance;
    private NetworkTableEntry blueAngle;

    private NetworkTableEntry blueWidth;
    private NetworkTableEntry blueHeight;
    private NetworkTableEntry blueRatio;
    private NetworkTableEntry blueX;
    private NetworkTableEntry blueY;
    private NetworkTableEntry redWidth;
    private NetworkTableEntry redHeight;
    private NetworkTableEntry redRatio;
    private NetworkTableEntry redX;
    private NetworkTableEntry redY;

    private NetworkTableEntry ntBallHeight;
    private NetworkTableEntry ntCameraHeight;
    private NetworkTableEntry ntCameraDownAngle;
    private NetworkTableEntry ntCameraTurnOffset;

    private NetworkTableEntry ntMinBoundingRectWidth;
    private NetworkTableEntry ntBoundingRectRatio;
    private NetworkTableEntry ntBoundingRectRatioTolerance;

    public BallProcessor(VideoSource camera, NetworkTableInstance networkTableInstance) {
        super(camera, networkTableInstance);

        NetworkTable table = visionTable.getSubTable(NETWORK_TABLE_NAME);
        redTable = table.getSubTable("Red");
        hasRed = redTable.getEntry("HasBall");
        redDistance = redTable.getEntry("Distance");
        redAngle = redTable.getEntry("Angle");
        blueTable = table.getSubTable("Blue");
        hasBlue = blueTable.getEntry("HasBall");
        blueDistance = blueTable.getEntry("Distance");
        blueAngle = blueTable.getEntry("Angle");

        blueWidth = blueTable.getEntry("Width");
        blueHeight = blueTable.getEntry("Height");
        blueRatio = blueTable.getEntry("Ratio");
        blueX = blueTable.getEntry("X");
        blueY = blueTable.getEntry("Y");

        redWidth = redTable.getEntry("Width");
        redHeight = redTable.getEntry("Height");
        redRatio = redTable.getEntry("Ratio");
        redX = redTable.getEntry("X");
        redY = redTable.getEntry("Y");

        ntBallHeight = table.getEntry("BallHeight");
        ballHeight = BALL_HEIGHT_IN_DEFAULT;
        ntBallHeight.setDouble(ballHeight);
        ntBallHeight.addListener(event -> {
            ballHeight = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntCameraHeight = table.getEntry("CameraHeight");
        cameraHeight = CAMERA_HEIGHT_IN_DEFAULT;
        ntCameraHeight.setDouble(cameraHeight);
        ntCameraHeight.addListener(event -> {
            cameraHeight = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntCameraDownAngle = table.getEntry("CameraDownAngle");
        cameraDownAngleDeg = DOWN_ANGLE_DEG_DEFAULT;
        ntCameraDownAngle.setDouble(cameraDownAngleDeg);
        ntCameraDownAngle.addListener(event -> {
            cameraDownAngleDeg = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntCameraTurnOffset = table.getEntry("CameraTurnOffset");
        cameraTurnOffsetDeg = TURN_ANGLE_OFFSET_DEG_DEFAULT;
        ntCameraTurnOffset.getDouble(cameraTurnOffsetDeg);
        ntCameraTurnOffset.addListener(event -> {
            cameraTurnOffsetDeg = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntMinBoundingRectWidth = table.getEntry("BoundingRectMinWidth");
        minBoundingRectWidth = MIN_BOUNDING_RECT_WIDTH_DEFAULT;
        ntMinBoundingRectWidth.setDouble(minBoundingRectWidth);
        ntMinBoundingRectWidth.addListener(event -> {
            minBoundingRectWidth = (int) event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntBoundingRectRatio = table.getEntry("BoundingRectRatio");
        boundingRectRatio = BOUNDING_RECT_RATIO_DEFAULT;
        ntBoundingRectRatio.setDouble(boundingRectRatio);
        ntBoundingRectRatio.addListener(event -> {
            boundingRectRatio = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        ntBoundingRectRatioTolerance = table.getEntry("BoundingRectRatioTolerance");
        boundingRectRatioTolerance = BOUNDING_RECT_RATIO_TOLERANCE_DEFAULT;
        ntBoundingRectRatioTolerance.setDouble(boundingRectRatioTolerance);
        ntBoundingRectRatioTolerance.addListener(event -> {
            boundingRectRatioTolerance = event.getEntry().getValue().getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    }

    public void process(VisionPipeline pipeline) {
        BallPipeline ballPipeline = (BallPipeline) pipeline;
        Rect boundingRectRed = ballPipeline.boundingRectRed;
        Rect boundingRectBlue = ballPipeline.boundingRectBlue;

        hasRed.setBoolean(false);
        if (boundingRectRed != null) {
            double redRatioCalc = (double) boundingRectRed.height / ((double) boundingRectRed.width);
            redRatio.setDouble(redRatioCalc);
            redWidth.setDouble(boundingRectRed.width);
            redHeight.setDouble(boundingRectRed.height);
            redX.setDouble(boundingRectRed.x);
            redY.setDouble(boundingRectRed.y);
            if (Math.abs(redRatioCalc - boundingRectRatio) < boundingRectRatioTolerance
                    && (boundingRectRed.width >= minBoundingRectWidth)) {
                int topLeftY = boundingRectRed.y;
                int centerX = (boundingRectRed.x + boundingRectRed.width / 2);
                double distanceToTargetRed = distance(topLeftY);
                double turningAngleRed = angle(centerX);
                hasRed.setBoolean(true);
                redDistance.setDouble(distanceToTargetRed);
                redAngle.setDouble(turningAngleRed);
            }
        }

        hasBlue.setBoolean(false);
        if (boundingRectBlue != null) {
            blueWidth.setDouble(boundingRectBlue.width);
            blueHeight.setDouble(boundingRectBlue.height);
            blueX.setDouble(boundingRectBlue.x);
            blueY.setDouble(boundingRectBlue.y);
            double blueRatioCalc = (double) boundingRectBlue.height / ((double) boundingRectBlue.width);
            blueRatio.setDouble(blueRatioCalc);

            if (Math.abs(blueRatioCalc - boundingRectRatio) < boundingRectRatioTolerance
                    && (boundingRectBlue.width >= minBoundingRectWidth)) {
                int topLeftY = boundingRectBlue.y;
                int centerX = (boundingRectBlue.x + boundingRectBlue.width / 2);
                double distanceToTargetBlue = distance(topLeftY);
                double turningAngleBlue = angle(centerX);
                hasBlue.setBoolean(true);
                blueDistance.setDouble(distanceToTargetBlue);
                blueAngle.setDouble(turningAngleBlue);
            }
        }
    }

    private double distance(int y) {
        return (cameraHeight - ballHeight) *
                Math.tan((90.0 - cameraDownAngleDeg
                        - (y - cameraFrameHeight / 2.0)
                                / pixelPerYDegree)
                        * DEG_TO_RADIANS);
    }

    private double angle(int x) {
        return (x - cameraFrameWidth / 2.0)
                / pixelPerXDegree
                + cameraTurnOffsetDeg;
    }
}
