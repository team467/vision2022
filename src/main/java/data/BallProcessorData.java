package data;

public class BallProcessorData extends DataHandler {

    double ballHeight = 9.5;
    double cameraHeight = 16.5;
    double cameraDownAngleDeg = 20.0;
    double cameraTurnOffsetDeg = 0.0;

    private static BallProcessorData ballProcessingData = null;

    public static BallProcessorData get() {
        // if (ballProcessingData == null) {
        // ballProcessingData = (BallProcessorData) load(new BallProcessorData());
        // }
        if (ballProcessingData == null) {
            // file does not exist
            ballProcessingData = new BallProcessorData();
        }
        return ballProcessingData;
    }

    private BallProcessorData() {
        super("BallProcessingData");
    }

    public double getBallHeight() {
        return ballHeight;
    }

    public void setBallHeight(double ballHeight) {
        this.ballHeight = ballHeight;
        table.getEntry("ballHeight").setDouble(ballHeight);
    }

    public double getCameraHeight() {
        return cameraHeight;
    }

    public void setCameraHeight(double cameraHeight) {
        this.cameraHeight = cameraHeight;
        table.getEntry("cameraHeight").setDouble(cameraHeight);
    }

    public double getCameraDownAngleDeg() {
        return cameraDownAngleDeg;
    }

    public void setCameraDownAngleDeg(double cameraDownAngleDeg) {
        this.cameraDownAngleDeg = cameraDownAngleDeg;
        table.getEntry("cameraDownAngleDeg").setDouble(cameraDownAngleDeg);
    }

    public double getCameraTurnOffsetDeg() {
        return cameraTurnOffsetDeg;
    }

    public void setCameraTurnOffsetDeg(double cameraTurnOffsetDeg) {
        this.cameraTurnOffsetDeg = cameraTurnOffsetDeg;
        table.getEntry("cameraTurnOffsetDeg").setDouble(cameraTurnOffsetDeg);
    }
}
