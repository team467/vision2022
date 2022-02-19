package pipelines;

import java.util.concurrent.ConcurrentSkipListMap;

import org.opencv.core.Mat;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;

public class Pipeline implements VisionPipeline {

    protected NetworkTable visionTable;
    protected NetworkTable table;
    private boolean usePipeline = true;
    protected boolean enableNetworkTuning = false;
    protected ConcurrentSkipListMap<String, Integer> tuningIds = new ConcurrentSkipListMap<String, Integer>();
    protected ConcurrentSkipListMap<String, Double> tuningValues = new ConcurrentSkipListMap<String, Double>();

    protected Pipeline(NetworkTableInstance networkTableInstance, String tableName) {
        visionTable = networkTableInstance.getTable("Vision");
        table = visionTable.getSubTable(tableName);

        table.getEntry("enableNetworkTuning").setBoolean(enableNetworkTuning);
        table.getEntry("enableNetworkTuning").addListener(event -> {
            enableNetworkTuning = event.getEntry().getValue().getBoolean();
            if (enableNetworkTuning) {
                enableTuning();
            } else {
                disableTuning();
            }
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        table.getEntry("usePipeline").setBoolean(usePipeline);
        table.getEntry("usePipeline").addListener(event -> {
            usePipeline = event.getEntry().getValue().getBoolean();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    }

    @Override
    public void process(Mat arg0) {
    }

    protected void enableTuning() {
        for (String name : tuningValues.keySet()) {
            NetworkTableEntry entry = table.getEntry(name);
            entry.setDouble(tuningValues.get(name));
            tuningIds.put(name, entry.addListener(event -> {
                tuningValues.put(name, event.getEntry().getValue().getDouble());
                updatePipelineSettings();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate));
        }
    }

    protected void disableTuning() {
        for (String entry : tuningIds.keySet()) {
            table.getEntry(entry).removeListener(0);
        }
    }

    protected void updatePipelineSettings() {

    }

}
