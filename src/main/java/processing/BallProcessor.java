package processing;

import org.opencv.core.Rect;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import pipelines.BallPipeline;

public class BallProcessor extends Processor {

    public static final String NETWORK_TABLE_NAME = "BallTracking";
    public static final double BALL_HEIGHT_IN = 9.5;
    public static final double CAMERA_HEIGHT_IN = 16.5;
    public static final double DOWN_ANGLE_DEG = 20.0;
    public static final double TURN_ANGLE_OFFSET_DEG = 0.0;

    private NetworkTable redTable;
    private NetworkTableEntry hasRed;
    private NetworkTableEntry redDistance;
    private NetworkTableEntry redAngle;
    private NetworkTable blueTable;
    private NetworkTableEntry hasBlue;
    private NetworkTableEntry blueDistance;
    private NetworkTableEntry blueAngle;

    public BallProcessor(NetworkTableInstance networkTableInstance) {
        super(networkTableInstance);
        NetworkTable table = networkTableInstance.getTable(NETWORK_TABLE_NAME);
        redTable = table.getSubTable("Red");
        hasRed = redTable.getEntry("HasBall");
        redDistance = redTable.getEntry("Distance");
        redAngle = redTable.getEntry("Angle");
        blueTable = table.getSubTable("Blue");
        hasBlue = blueTable.getEntry("HasBall");
        blueDistance = blueTable.getEntry("Distance");
        blueAngle = blueTable.getEntry("Angle");
    }

    public void process(VisionPipeline pipeline) {
        BallPipeline ballPipeline = (BallPipeline) pipeline;
        Rect boundingRectRed = ballPipeline.boundingRectRed;
        Rect boundingRectBlue = ballPipeline.boundingRectBlue;
        hasRed.setBoolean(false);
        if (boundingRectRed != null){
            int topLeftY = boundingRectRed.y;
            //int topLeftX = CAMERA_X_RESOLUTION - boundingRectRed.x;
            int centerX = (boundingRectRed.x + boundingRectRed.width/2); 
            double distanceToTargetRed = distance(topLeftY);
            double turningAngleRed = angle(centerX);
            hasRed.setBoolean(true);
            redDistance.setDouble(distanceToTargetRed);
            redAngle.setDouble(turningAngleRed);
            System.out.println("Area of Red Bounding rect = " + boundingRectRed.area());
            System.out.println("Top left of red rect: " + String.valueOf(topLeftY));
            System.out.println("Distance to red : " + String.valueOf(distanceToTargetRed));
            System.out.println("Angle to red : " + String.valueOf(turningAngleRed));
        }
        hasBlue.setBoolean(false);
        if (boundingRectBlue != null)
        {
            int topLeftY = boundingRectBlue.y;
            //int topLeftX = CAMERA_X_RESOLUTION - boundingRectBlue.x;
            int centerX = (boundingRectBlue.x + boundingRectBlue.width/2);
            double distanceToTargetBlue = distance(topLeftY);
            double turningAngleBlue = angle(centerX);
            hasBlue.setBoolean(true);
            blueDistance.setDouble(distanceToTargetBlue);
            blueAngle.setDouble(turningAngleBlue);
            System.out.println("Area of Blue Bounding rect = " + boundingRectBlue.area());
            System.out.println("Top left of blue rect: " + String.valueOf(topLeftY));
            System.out.println("Distance to blue : " + String.valueOf(distanceToTargetBlue));
            System.out.println("Angle to blue : " + String.valueOf(turningAngleBlue));

            //String.valueOf(topLeftX)
        }
    }

    private double distance(int y) {
        return (CAMERA_HEIGHT_IN - BALL_HEIGHT_IN) *
                Math.tan((90.0 - DOWN_ANGLE_DEG
                        - (y - CAMERA_Y_RESOLUTION / 2.0)
                                / PIXELS_PER_DEGREE)
                        * DEG_TO_RADIANS);
    }

    private double angle(int x) {
        return (x - CAMERA_X_RESOLUTION / 2.0)
                / PIXELS_PER_DEGREE
                + TURN_ANGLE_OFFSET_DEG;
    }
}
