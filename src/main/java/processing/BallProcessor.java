package processing;

import org.opencv.core.Rect;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.BallPipeline;

public class BallProcessor extends Processor {

    public static final String NETWORK_TABLE_NAME = "BallTracking";

    // private double ballHeight = 9.5;
    // private double cameraHeight = 17.5;
    // private double cameraDownAngleDeg = 20.0;
    // private double cameraTurnOffsetDeg = 0.0;

    // private int minBoundingRectWidth = 60;
    // private double boundingRectRatio = 1.1;
    // private double boundingRectRatioTolerance = 0.2;

    private long blueFrameCount = 0;
    private long redFrameCount = 0;

    private NetworkTable redTable;
    private NetworkTableEntry hasRed;
    private NetworkTableEntry redDistance;
    private NetworkTableEntry redAngle;
    private NetworkTableEntry redFrameNumber;

    private NetworkTable blueTable;
    private NetworkTableEntry hasBlue;
    private NetworkTableEntry blueDistance;
    private NetworkTableEntry blueAngle;
    private NetworkTableEntry blueFrameNumber;

    public BallProcessor(VideoSource camera, NetworkTableInstance networkTableInstance) {
        super(camera, networkTableInstance, NETWORK_TABLE_NAME);

        redTable = table.getSubTable("Red");
        hasRed = redTable.getEntry("HasBall");
        redDistance = redTable.getEntry("Distance");
        redAngle = redTable.getEntry("Angle");
        redFrameNumber = redTable.getEntry("Frame Count");

        blueTable = table.getSubTable("Blue");
        hasBlue = blueTable.getEntry("HasBall");
        blueDistance = blueTable.getEntry("Distance");
        blueAngle = blueTable.getEntry("Angle");
        blueFrameNumber = blueTable.getEntry("Frame Count");

        tuningValues.put("ballHeight", 9.5);
        tuningValues.put("cameraHeight", 17.5);
        tuningValues.put("cameraDownAngleDeg", 20.0);
        tuningValues.put("cameraTurnOffsetDeg", 0.0);

        tuningValues.put("minBoundingRectWidth", 50.0);
        tuningValues.put("boundingRectRatio", 1.1);
        tuningValues.put("boundingRectRatioTolerance", 0.3);

    }

    boolean first = true;

    public void process(VisionPipeline pipeline) {

        BallPipeline ballPipeline = (BallPipeline) pipeline;
        Rect boundingRectRed = ballPipeline.boundingRectRed;
        Rect boundingRectBlue = ballPipeline.boundingRectBlue;

        if (first) {
            System.out.println("RUNNING BALL PIPELINE AT LEAST ONCE");
            first = false;
            if (boundingRectBlue == null) {
                System.out.println("BLUE IS NULL");
            }
            if (boundingRectRed == null) {
                System.out.println("BLUE IS NULL");
            }
        }

        hasRed.setBoolean(false);
        if (boundingRectRed != null) {
            double redRatioCalc = (double) boundingRectRed.height / ((double) boundingRectRed.width);
            if (Math.abs(redRatioCalc - tuningValues.get("boundingRectRatio")) < tuningValues
                    .get("boundingRectRatioTolerance")
                    && (boundingRectRed.width >= tuningValues.get("minBoundingRectWidth"))) {
                int topLeftY = boundingRectRed.y;
                int centerX = (boundingRectRed.x + boundingRectRed.width / 2);
                double distanceToTargetRed = distance(topLeftY);
                double turningAngleRed = angle(centerX);
                hasRed.setBoolean(true);
                redDistance.setDouble(distanceToTargetRed);
                redAngle.setDouble(turningAngleRed);
                redFrameNumber.setDouble(++redFrameCount);
            }
        }

        hasBlue.setBoolean(false);
        if (boundingRectBlue != null) {
            double blueRatioCalc = (double) boundingRectBlue.height / ((double) boundingRectBlue.width);
            if (Math.abs(blueRatioCalc - tuningValues.get("boundingRectRatio")) < tuningValues
                    .get("boundingRectRatioTolerance")
                    && (boundingRectBlue.width >= tuningValues.get("minBoundingRectWidth"))) {
                int topLeftY = boundingRectBlue.y;
                int centerX = (boundingRectBlue.x + boundingRectBlue.width / 2);
                double distanceToTargetBlue = distance(topLeftY);
                double turningAngleBlue = angle(centerX);
                hasBlue.setBoolean(true);
                blueDistance.setDouble(distanceToTargetBlue);
                blueAngle.setDouble(turningAngleBlue);
                blueFrameNumber.setDouble(++blueFrameCount);
            }
        }

    }

    private double distance(int y) {
        return (tuningValues.get("cameraHeight") - tuningValues.get("ballHeight")) *
                Math.tan((90.0 - tuningValues.get("cameraDownAngleDeg")
                        - (y - cameraFrameHeight / 2.0)
                                / pixelPerYDegree)
                        * DEG_TO_RADIANS);
    }

    private double angle(int x) {
        return (x - cameraFrameWidth / 2.0)
                / pixelPerXDegree
                + tuningValues.get("cameraTurnOffsetDeg");
    }
}
