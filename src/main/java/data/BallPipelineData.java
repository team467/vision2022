package data;

public class BallPipelineData extends DataHandler {

    int minBoundingRectWidth;
    double boundingRectRatio;
    double boundingRectRatioTolerance;

    private static BallPipelineData ballPipelineData = null;

    public static BallPipelineData get() {
        if (ballPipelineData == null) {
            ballPipelineData = (BallPipelineData) load(BallPipelineData.class);
        }
        return ballPipelineData;
    }

    private BallPipelineData() {
        super("BallPipelineData");
    }

    public int getMinBoundingRectWidth() {
        return minBoundingRectWidth;
    }

    public void setMinBoundingRectWidth(int minBoundingRectWidth) {
        this.minBoundingRectWidth = minBoundingRectWidth;
        table.getEntry("minBoundingRectWidth").setDouble(minBoundingRectWidth);
    }

    public double getBoundingRectRatio() {
        return boundingRectRatio;
    }

    public void setBoundingRectRatio(double boundingRectRatio) {
        this.boundingRectRatio = boundingRectRatio;
        table.getEntry("boundingRectRatio").setDouble(boundingRectRatio);
    }

    public double getBoundingRectRatioTolerance() {
        return boundingRectRatioTolerance;
    }

    public void setBoundingRectRatioTolerance(double boundingRectRatioTolerance) {
        this.boundingRectRatioTolerance = boundingRectRatioTolerance;
        table.getEntry("boundingRectRatioTolerance").setDouble(boundingRectRatioTolerance);
    }

}
