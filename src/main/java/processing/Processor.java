package processing;

import java.util.concurrent.ConcurrentSkipListMap;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;

public class Processor {

        public static final double DEG_TO_RADIANS = 2.0 * 3.1415 / 360.0;

        public static final int CAMERA_MAX_X_RESOLUTION = 1280;
        public static final int CAMERA_MAX_Y_RESOLUTION = 720;
        private static final double PIXELS_PER_DEGREE_AT_MAX_RESOLUTION = 21.44;

        protected VideoSource camera;
        protected CvSource outputStream;

        protected NetworkTable smartDashboard;
        protected NetworkTable visionTable;
        protected NetworkTable table;
        protected boolean enableNetworkTuning = false;

        protected int cameraFrameWidth;
        protected int cameraFrameHeight;

        protected double pixelPerXDegree;
        protected double pixelPerYDegree;

        public Processor(VideoSource camera, NetworkTableInstance networkTableInstance, String tableName) {

                visionTable = networkTableInstance.getTable("Vision");
                table = visionTable.getSubTable(tableName);

                smartDashboard = networkTableInstance.getTable("SmartDashboard");
                table.getEntry("enableNetworkTuning").setBoolean(enableNetworkTuning);
                table.getEntry("enableNetworkTuning").addListener(event -> {
                        enableNetworkTuning = event.getEntry().getValue().getBoolean();
                        if (enableNetworkTuning) {
                                enableTuning();
                        } else {
                                disableTuning();
                        }
                }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

                this.camera = camera;
                VideoMode videoMode = camera.getVideoMode();
                cameraFrameWidth = videoMode.width;
                cameraFrameHeight = videoMode.height;
                outputStream = CameraServer.getInstance().putVideo(camera.getName() + " Processed",
                                cameraFrameWidth, cameraFrameHeight);
                pixelPerXDegree = PIXELS_PER_DEGREE_AT_MAX_RESOLUTION * (double) cameraFrameWidth
                                / (double) CAMERA_MAX_X_RESOLUTION;
                pixelPerYDegree = PIXELS_PER_DEGREE_AT_MAX_RESOLUTION * (double) cameraFrameHeight
                                / (double) CAMERA_MAX_Y_RESOLUTION;
        }

        protected ConcurrentSkipListMap<String, Integer> tuningIds = new ConcurrentSkipListMap<String, Integer>();
        protected ConcurrentSkipListMap<String, Double> tuningValues = new ConcurrentSkipListMap<String, Double>();

        protected void enableTuning() {
                for (String name : tuningValues.keySet()) {
                        NetworkTableEntry entry = table.getEntry(name);
                        entry.setDouble(tuningValues.get(name));
                        tuningIds.put(name, entry.addListener(event -> {
                                tuningValues.put(name, event.getEntry().getValue().getDouble());
                        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate));
                }
        }

        boolean hasOneBall = false;

        protected void disableTuning() {
                for (String entry : tuningIds.keySet()) {
                        table.getEntry(entry).removeListener(0);
                }
        }

        public void process(VisionPipeline pipeline) {

        }

}
