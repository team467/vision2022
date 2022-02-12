package data;

public class HubTargetProcessorData extends DataHandler {

    double hubTargetHeightFt = 8.700;
    double cameraHeightFt = 2.875;
    double cameraUpAngleDeg = 45.0;
    double cameraTurnOffsetDeg = 0.0;

    private static HubTargetProcessorData hubTargetProcessorData = null;

    public static HubTargetProcessorData get() {
        if (hubTargetProcessorData == null) {
            hubTargetProcessorData = (HubTargetProcessorData) load(new HubTargetProcessorData());
        }
        return hubTargetProcessorData;
    }

    private HubTargetProcessorData() {
        super("HubTargetProcessorData");
    }

    public double getHubTargetHeightFt() {
        return hubTargetHeightFt;
    }

    public void setHubTargetHeightFt(double hubTargetHeightFt) {
        this.hubTargetHeightFt = hubTargetHeightFt;
        table.getEntry("hubTargetHeightFt").setDouble(hubTargetHeightFt);
    }

    public double getCameraHeightFt() {
        return cameraHeightFt;
    }

    public void setCameraHeightFt(double cameraHeightFt) {
        this.cameraHeightFt = cameraHeightFt;
        table.getEntry("cameraHeightFt").setDouble(cameraHeightFt);
    }

    public double getCameraUpAngleDeg() {
        return cameraUpAngleDeg;
    }

    public void setCameraUpAngleDeg(double cameraUpAngleDeg) {
        this.cameraUpAngleDeg = cameraUpAngleDeg;
        table.getEntry("cameraUpAngleDeg").setDouble(cameraUpAngleDeg);
    }

    public double getCameraTurnOffsetDeg() {
        return cameraTurnOffsetDeg;
    }

    public void setCameraTurnOffsetDeg(double cameraTurnOffsetDeg) {
        this.cameraTurnOffsetDeg = cameraTurnOffsetDeg;
        table.getEntry("cameraTurnOffsetDeg").setDouble(cameraTurnOffsetDeg);
    }

}
