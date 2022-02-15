package data;

import edu.wpi.first.networktables.EntryListenerFlags;

public class HubTargetPipelineData extends DataHandler {

    double[] hslThresholdHue = { 46.7561222399084, 89.79841567788532 };
    double[] hslThresholdSaturation = { 204.09172661870508, 255.0 };
    double[] hslThresholdLuminance = { 80.26079136690647, 218.51010101010098 };

    double filterContoursMinArea = 10.0;
    double filterContoursMinPerimeter = 175.0;
    double filterContoursMinWidth = 180.0;

    public double[] getHslThresholdHue() {
        return hslThresholdHue;
    }

    public void setHslThresholdHue(double[] hslThresholdHue) {
        this.hslThresholdHue = hslThresholdHue;
    }

    public double[] getHslThresholdSaturation() {
        return hslThresholdSaturation;
    }

    public void setHslThresholdSaturation(double[] hslThresholdSaturation) {
        this.hslThresholdSaturation = hslThresholdSaturation;
    }

    public double[] getHslThresholdLuminance() {
        return hslThresholdLuminance;
    }

    public void setHslThresholdLuminance(double[] hslThresholdLuminance) {
        this.hslThresholdLuminance = hslThresholdLuminance;
    }

    public double getFilterContoursMinArea() {
        return filterContoursMinArea;
    }

    public void setFilterContoursMinArea(double filterContoursMinArea) {
        this.filterContoursMinArea = filterContoursMinArea;
    }

    public double getFilterContoursMinPerimeter() {
        return filterContoursMinPerimeter;
    }

    public void setFilterContoursMinPerimeter(double filterContoursMinPerimeter) {
        this.filterContoursMinPerimeter = filterContoursMinPerimeter;
    }

    public double getFilterContoursMinWidth() {
        return filterContoursMinWidth;
    }

    public void setFilterContoursMinWidth(double filterContoursMinWidth) {
        this.filterContoursMinWidth = filterContoursMinWidth;
    }

    public double getFilterContoursMaxWidth() {
        return filterContoursMaxWidth;
    }

    public void setFilterContoursMaxWidth(double filterContoursMaxWidth) {
        this.filterContoursMaxWidth = filterContoursMaxWidth;
    }

    public double getFilterContoursMinHeight() {
        return filterContoursMinHeight;
    }

    public void setFilterContoursMinHeight(double filterContoursMinHeight) {
        this.filterContoursMinHeight = filterContoursMinHeight;
    }

    public double getFilterContoursMaxHeight() {
        return filterContoursMaxHeight;
    }

    public void setFilterContoursMaxHeight(double filterContoursMaxHeight) {
        this.filterContoursMaxHeight = filterContoursMaxHeight;
    }

    public double[] getFilterContoursSolidity() {
        return filterContoursSolidity;
    }

    public void setFilterContoursSolidity(double[] filterContoursSolidity) {
        this.filterContoursSolidity = filterContoursSolidity;
    }

    public double getFilterContoursMaxVertices() {
        return filterContoursMaxVertices;
    }

    public void setFilterContoursMaxVertices(double filterContoursMaxVertices) {
        this.filterContoursMaxVertices = filterContoursMaxVertices;
    }

    public double getFilterContoursMinVertices() {
        return filterContoursMinVertices;
    }

    public void setFilterContoursMinVertices(double filterContoursMinVertices) {
        this.filterContoursMinVertices = filterContoursMinVertices;
    }

    public double getFilterContoursMinRatio() {
        return filterContoursMinRatio;
    }

    public void setFilterContoursMinRatio(double filterContoursMinRatio) {
        this.filterContoursMinRatio = filterContoursMinRatio;
    }

    public double getFilterContoursMaxRatio() {
        return filterContoursMaxRatio;
    }

    public void setFilterContoursMaxRatio(double filterContoursMaxRatio) {
        this.filterContoursMaxRatio = filterContoursMaxRatio;
    }

    double filterContoursMaxWidth = 500.0;
    double filterContoursMinHeight = 10.0;
    double filterContoursMaxHeight = 95.0;
    double[] filterContoursSolidity = { 60.251798561151084, 100 };
    double filterContoursMaxVertices = 1000.0;
    double filterContoursMinVertices = 30.0;
    double filterContoursMinRatio = 0.0;
    double filterContoursMaxRatio = 1000.0;

    private static HubTargetPipelineData hubTargetPipelineData = null;

    public static HubTargetPipelineData get() {
        if (hubTargetPipelineData == null) {
            hubTargetPipelineData = (HubTargetPipelineData) load(HubTargetPipelineData.class);
        }
        return hubTargetPipelineData;
    }

    private HubTargetPipelineData() {
        super("HubTargetPipelineData");

        if (USE_NETWORK_TABLES) {
            table.getEntry("hslThresholdHueMin").setDouble(hslThresholdHue[0]);
            table.getEntry("hslThresholdHueMin").addListener(event -> {
                hslThresholdHue[0] = (int) event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("hslThresholdHueMax").setDouble(hslThresholdHue[1]);
            table.getEntry("hslThresholdHueMax").addListener(event -> {
                hslThresholdHue[1] = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("hslThresholdSaturationMin").setDouble(hslThresholdSaturation[0]);
            table.getEntry("hslThresholdSaturationMin").addListener(event -> {
                hslThresholdSaturation[0] = (int) event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("hslThresholdSaturationMax").setDouble(hslThresholdSaturation[1]);
            table.getEntry("hslThresholdSaturationMax").addListener(event -> {
                hslThresholdSaturation[1] = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("hslThresholdLuminanceMin").setDouble(hslThresholdLuminance[0]);
            table.getEntry("hslThresholdLuminanceMin").addListener(event -> {
                hslThresholdLuminance[0] = (int) event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("hslThresholdLuminanceMax").setDouble(hslThresholdLuminance[1]);
            table.getEntry("hslThresholdLuminanceMax").addListener(event -> {
                hslThresholdLuminance[1] = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMinArea").setDouble(filterContoursMinArea);
            table.getEntry("filterContoursMinArea").addListener(event -> {
                filterContoursMinArea = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMinPerimeter").setDouble(filterContoursMinPerimeter);
            table.getEntry("filterContoursMinPerimeter").addListener(event -> {
                filterContoursMinPerimeter = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMinWidth").setDouble(filterContoursMinWidth);
            table.getEntry("filterContoursMinWidth").addListener(event -> {
                filterContoursMinWidth = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMaxWidth").setDouble(filterContoursMaxWidth);
            table.getEntry("filterContoursMaxWidth").addListener(event -> {
                filterContoursMaxWidth = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMinHeight").setDouble(filterContoursMinHeight);
            table.getEntry("filterContoursMinHeight").addListener(event -> {
                filterContoursMinHeight = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMaxHeight").setDouble(filterContoursMaxHeight);
            table.getEntry("filterContoursMaxHeight").addListener(event -> {
                filterContoursMaxHeight = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursSolidityMin").setDouble(filterContoursSolidity[0]);
            table.getEntry("filterContoursSolidityMin").addListener(event -> {
                filterContoursSolidity[0] = (int) event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursSolidityMin").setDouble(filterContoursSolidity[1]);
            table.getEntry("filterContoursSolidityMin").addListener(event -> {
                filterContoursSolidity[1] = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMaxVertices").setDouble(filterContoursMaxVertices);
            table.getEntry("filterContoursMaxVertices").addListener(event -> {
                filterContoursMaxVertices = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMinVertices").setDouble(filterContoursMinVertices);
            table.getEntry("filterContoursMinVertices").addListener(event -> {
                filterContoursMinVertices = (int) event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMinRatio").setDouble(filterContoursMinRatio);
            table.getEntry("filterContoursMinRatio").addListener(event -> {
                filterContoursMinRatio = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

            table.getEntry("filterContoursMaxRatio").setDouble(filterContoursMaxRatio);
            table.getEntry("filterContoursMaxRatio").addListener(event -> {
                filterContoursMaxRatio = event.getEntry().getValue().getDouble();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
        }

    }

    public static void main(String args[]) {
        HubTargetPipelineData data = new HubTargetPipelineData();
        data.save();
    }

}
