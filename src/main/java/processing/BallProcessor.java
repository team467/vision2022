package processing;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class BallProcessor {

    public static final String NETWORK_TABLE_NAME = "balls";

    private NetworkTable table;

    public BallProcessor(NetworkTableInstance networkTableInstance) {
        NetworkTable smartDashboard = networkTableInstance.getTable("SmartDashboard");
        table = smartDashboard.getSubTable(NETWORK_TABLE_NAME);
    }

    public void process(int x, int y, boolean isRed) {

        double distance = 0.0;
        double angle = 0.0;

        // TODO: Calculate Distance and Angle
        // Distance should be from top of ball

        table.getEntry("distance").setDouble(distance);
        table.getEntry("angle").setDouble(angle);
        table.getEntry("isRed").setBoolean(isRed);

    }

}
