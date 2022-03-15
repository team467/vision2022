package processing;

import org.opencv.core.KeyPoint;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Rect;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import pipelines.BallPipeline;
import org.opencv.imgproc.Imgproc;

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
        tuningValues.put("cameraDownAngleOffsetDeg", 0.0);
        tuningValues.put("cameraTurnOffsetDeg", 0.0);

        tuningValues.put("minBoundingRectWidth", 25.0);
        tuningValues.put("boundingRectRatio", 1.1);
        tuningValues.put("boundingRectRatioTolerance", 0.3);

    }

    // boolean first = true;

    public void process(VisionPipeline pipeline) {

        BallPipeline ballPipeline = (BallPipeline) pipeline;
       // Rect boundingRectRed = ballPipeline.boundingRectRed;
       // Rect boundingRectBlue = ballPipeline.boundingRectBlue;

      MatOfKeyPoint redBlob =  ballPipeline.findBlobs0Output();
      MatOfKeyPoint blueBlob = ballPipeline.findBlobs1Output();

        Rect boundingRectRed = Imgproc.boundingRect( MatOfKeyPoint2MatOfPoint.toMatOfPoint(redBlob));
        Rect boundingRectBlue = Imgproc.boundingRect( MatOfKeyPoint2MatOfPoint.toMatOfPoint(blueBlob));

        hasRed.setBoolean(false);
     //   KeyPoint[] kp = redBlob.toArray();
     //   double rx =  kp[0].pt.x;
     //   double ry = kp[0].pt.y;

      //  System.out.println ("Length of the red blob array" + kp.length);
      System.out.println("RedBlob : " + redBlob.dump());
      System.out.println("BlueBlob : " + blueBlob.dump());
    


      System.out.println("RedX: " + boundingRectRed.x + " RedY: " + boundingRectRed.y);

      System.out.println("BlueX: " + boundingRectBlue.x + " BlueY: " + boundingRectBlue.y);
        
//f (

        if (boundingRectRed != null && boundingRectRed.width > 100 && boundingRectRed.width < 300 ) {
                int topLeftY = boundingRectRed.y;
                int centerX = (boundingRectRed.x + boundingRectRed.width / 2);
                double distanceToTargetRed = distance(topLeftY);
                double turningAngleRed = angle(centerX);
                hasRed.setBoolean(true);
                redDistance.setDouble(distanceToTargetRed);
                redAngle.setDouble(turningAngleRed);
                redFrameNumber.setDouble(++redFrameCount);
                table.getEntry("RedX").setDouble(centerX);
                table.getEntry("RedY").setDouble(topLeftY);
                table.getEntry("RedHasBall").setBoolean(true);
            // }
        }
        // // else{
            
        //     table.getEntry("RedHasBall").setBoolean(false);
        //     redTable.getEntry("HasBall").setBoolean(false);
        // }

        hasBlue.setBoolean(false);
        if (boundingRectBlue != null && boundingRectBlue.width > 100 && boundingRectBlue.width < 300 ) {
            
                int topLeftY = boundingRectBlue.y;
                int centerX = (boundingRectBlue.x + boundingRectBlue.width / 2);
                double distanceToTargetBlue = distance(topLeftY);
                double turningAngleBlue = angle(centerX);
                hasBlue.setBoolean(true);
                blueDistance.setDouble(distanceToTargetBlue);
                blueAngle.setDouble(turningAngleBlue);
                blueFrameNumber.setDouble(++blueFrameCount);
                // table.getEntry("BlueX").setDouble(centerX);
                // table.getEntry("BlueY").setDouble(topLeftY);
                
                // table.getEntry("BlueHasBall").setBoolean(true);

            // }
        }
        // else
        // {
            
        //     table.getEntry("BlueHasBall").setBoolean(false);
        //     blueTable.getEntry("HasBall").setBoolean(false);
        // }

    }

    private double distance(int y) {
        double distance = (tuningValues.get("cameraHeight")
                - tuningValues.get("ballHeight")) *
                Math.tan((90.0 - tuningValues.get("cameraDownAngleDeg")
                        - tuningValues.get("cameraDownAngleOffsetDeg")
                        - (y - cameraFrameHeight / 2.0)
                                / pixelPerYDegree)
                        * DEG_TO_RADIANS);
    //    distance = Math.pow(1.20377, distance) - 52.521;
        distance *= 0.0254; // Convert inches to meters
        return distance;
    }

    private double angle(int x) {
        return (x - cameraFrameWidth / 2.0)
                / pixelPerXDegree
                + tuningValues.get("cameraTurnOffsetDeg");
    }
}
