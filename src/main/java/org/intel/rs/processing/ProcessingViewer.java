package org.intel.rs.processing;

import org.intel.rs.frame.DepthFrame;
import org.intel.rs.frame.FrameList;
import org.intel.rs.frame.VideoFrame;
import org.intel.rs.option.CameraOption;
import org.intel.rs.pipeline.Config;
import org.intel.rs.pipeline.Pipeline;
import org.intel.rs.types.Format;
import org.intel.rs.types.Option;
import org.intel.rs.types.Stream;
import processing.core.PApplet;
import processing.core.PImage;

public class ProcessingViewer extends PApplet {

    private Align align = new Align(Stream.Color);
    private Pipeline pipeline = new Pipeline();
    private Colorizer colorizer = new Colorizer();

    private TemporalFilter temporalFilter = new TemporalFilter();
    private HoleFillingFilter holeFillingFilter = new HoleFillingFilter();

    PImage colorImage = new PImage(640, 480, RGB);
    PImage depthImage = new PImage(640, 480, RGB);

    @Override
    public void settings() {
        size(1280, 480, P3D);
    }

    @Override
    public void setup() {
        // create camera
        Config cfg = new Config();
        cfg.enableStream(Stream.Depth, 640, 480);
        cfg.enableStream(Stream.Color, Format.Rgb8);

        pipeline.start(cfg);

        // set color scheme settings
        CameraOption colorScheme = colorizer.getOptions().get(Option.ColorScheme);
        colorScheme.setValue(6);

        // setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            pipeline.stop();
            System.out.println("camera has been shutdown!");
        }));
    }

    @Override
    public void draw() {
        background(0);

        FrameList frames = pipeline.waitForFrames();
        FrameList alignedFrames = align.process(frames);

        VideoFrame colorFrame = alignedFrames.getColorFrame();
        DepthFrame depthFrame = alignedFrames.getDepthFrame();

        VideoFrame holeFrame = holeFillingFilter.process(depthFrame);
        VideoFrame tempFrame = temporalFilter.process(holeFrame);
        VideoFrame colorizedDepth = colorizer.colorize(tempFrame);

        // convert to PImage
        copyTo(colorFrame, colorImage);
        copyTo(colorizedDepth, depthImage);

        holeFrame.release();
        tempFrame.release();
        colorizedDepth.release();
        alignedFrames.release();
        frames.release();

        // draw images
        image(colorImage, 0, 0);
        image(depthImage, 640, 0);

        surface.setTitle("FPS: " + nfp(frameRate, 0, 2));
    }

    @Override
    public void runSketch() {
        super.runSketch();
    }

    public void copyTo(VideoFrame frame, PImage image) {
        frame.copyTo(image.pixels);
        image.updatePixels();
    }

    public static void main(String... args) {
        ProcessingViewer viewer = new ProcessingViewer();
        viewer.setup();
        viewer.runSketch();
    }
}
