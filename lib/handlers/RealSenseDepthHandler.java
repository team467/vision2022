package handlers;

import org.intel.rs.frame.DepthFrame;
import org.intel.rs.frame.FrameList;
import org.intel.rs.frame.VideoFrame;
import org.intel.rs.pipeline.Config;
import org.intel.rs.pipeline.Pipeline;
import org.intel.rs.processing.Align;
import org.intel.rs.types.Format;
import org.intel.rs.types.Stream;

import pipeline.YellowStickyNotePipeline;

public class RealSenseDepthHandler {
    // For RealSense
    private static Align align = new Align(Stream.Color);
    private static Pipeline realSenseFramePipeline = new Pipeline();
    private static volatile boolean running = true;

    private static YellowStickyNotePipeline yellowStickyNotePipeline = new YellowStickyNotePipeline();

    public RealSenseDepthHandler() {

    }

    public void init() {
        // Enable RealSense Camera Configs
        Config cfg = new Config();
        cfg.enableStream(Stream.Depth, 640, 480);
        cfg.enableStream(Stream.Color, Format.Rgb8);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // shutdown camera
            running = false;

            realSenseFramePipeline.stop();
            System.out.println("camera has been shutdown!");
        }));

        realSenseFramePipeline.start(cfg);

        // setting up thread to read data
        Thread thread = new Thread(() -> {
            while (running) {
                readFrames();
            }
        });
        thread.start();

    }

    public static void readFrames() {
        FrameList frames = realSenseFramePipeline.waitForFrames();
        FrameList alignedFrames = align.process(frames);

        VideoFrame colorFrame = alignedFrames.getColorFrame();
        DepthFrame depthFrame = alignedFrames.getDepthFrame();

        // Mat colorMat = new Mat(colorFrame.getHeight(), colorFrame.getWidth(),
        // CvType.CV_8UC3);
        // colorMat.put(0, 0, colorFrame.getBytes());

        // yellowStickyNotePipeline.process(colorMat);
        // for (MatOfPoint hull : yellowStickyNotePipeline.convexHullsOutput()) {
        // System.out.println(depthFrame.getDistance(hull.width() / 2, hull.height() /
        // 2));
        // }

        // Mat testMap = new Mat(depth.getHeight(), depth.getWidth(), CV_16UC1);
        // int size = (int) (testMap.total() * testMap.elemSize());
        // byte[] return_buff = new byte[size];
        // depth.getData(return_buff);
        // short[] shorts = new short[size / 2];
        // ByteBuffer.wrap(return_buff).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        // testMap.put(0, 0, shorts);
        // // You check a pixel value by using something like this
        // // (double)testMap.get(depth.getHeight()/2,depth.getWidth()/2)[0];

        colorFrame.release();
        depthFrame.release();

        alignedFrames.release();
        frames.release();
    }

}
