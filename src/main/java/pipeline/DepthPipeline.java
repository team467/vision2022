package pipeline;

import org.intel.rs.Context;
import org.intel.rs.device.Device;
import org.intel.rs.device.DeviceList;
import org.intel.rs.frame.DepthFrame;
import org.intel.rs.frame.FrameList;
import org.intel.rs.frame.VideoFrame;
import org.intel.rs.option.CameraOption;
import org.intel.rs.pipeline.Config;
import org.intel.rs.pipeline.Pipeline;
import org.intel.rs.processing.Align;
import org.intel.rs.types.Format;
import org.intel.rs.types.Option;
import org.intel.rs.types.Stream;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import static org.bytedeco.librealsense2.global.realsense2.RS2_CAMERA_INFO_NAME;

public class DepthPipeline {

    private Pipeline pipeline = new Pipeline();
    private Align align = new Align(Stream.Color);
    private HoopTargetRealSenseJava gripPipeline = new HoopTargetRealSenseJava();

    private volatile boolean running = true;

    public DepthPipeline() {
        Context context = new Context();
        DeviceList list = context.queryDevices();
        int count = list.count();
        if (count < 1) {
            System.err.println("No devices");
            return;
        }

        for (int id = 0; id < count; id++) {
            Device device = list.get(0);
            String info = device.getInfo(RS2_CAMERA_INFO_NAME);
            System.out.println("Name: " + info);
            device.release();
        }

        list.release();

    }

    public void initialize() {

        // Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        // // shutdown camera
        // running = false;

        // pipeline.stop();
        // System.out.println("camera has been shutdown!");
        // }));

        // // create camera
        // Config cfg = new Config();
        // cfg.enableStream(Stream.Depth, 640, 480);
        // cfg.enableStream(Stream.Color, Format.Rgb8);

        // pipeline.start(cfg);

        // System.out.println("camera has been started!");

        // // setting up thread to read data
        // Thread thread = new Thread(() -> {
        // while (running) {
        // readFrames();
        // }
        // });
        // thread.start();
    }

    public void readFrames() {
        FrameList frames = pipeline.waitForFrames();
        FrameList alignedFrames = align.process(frames);

        VideoFrame colorFrame = alignedFrames.getColorFrame();
        DepthFrame depthFrame = alignedFrames.getDepthFrame();

        Mat colorMat = new Mat(
                colorFrame.getHeight(), colorFrame.getWidth(),
                CvType.CV_8UC3,
                colorFrame.getData());

        float distance = 1.0f;
        gripPipeline.process(colorMat);
        for (MatOfPoint contour : gripPipeline.filterContoursOutput()) {
            Rect rect = Imgproc.boundingRect(contour);
            int midX = (rect.width - rect.x) / 2;
            int midY = (rect.height - rect.y) / 2;
            distance = depthFrame.getDistance(midX, midY);
            System.out.println("Distance of (" + rect.x + "," + rect.y
                    + ") to (" + rect.width + "," + rect.height + ") is " + distance);
        }

        colorFrame.release();
        depthFrame.release();

        alignedFrames.release();
        frames.release();
    }

}
