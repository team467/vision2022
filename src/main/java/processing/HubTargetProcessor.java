package processing;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class HubTargetProcessor {

    public static final String NETWORK_TABLE_NAME = "hub_target";

    private NetworkTable table;

    public HubTargetProcessor(NetworkTableInstance networkTableInstance) {
        NetworkTable smartDashboard = networkTableInstance.getTable("SmartDashboard");
        table = smartDashboard.getSubTable(NETWORK_TABLE_NAME);
    }

    public void process(int x, int y) {

        double distance = 0.0;
        double angle = 0.0;

        // TODO: Calculate Distance and Angle

        table.getEntry("distance").setDouble(distance);
        table.getEntry("angle").setDouble(angle);

    }

}
