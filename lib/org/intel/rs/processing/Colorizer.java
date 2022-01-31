package org.intel.rs.processing;

import org.intel.rs.util.RealSenseError;
import static org.bytedeco.librealsense2.global.realsense2.*;

import org.intel.rs.frame.FrameQueue;
import org.intel.rs.frame.VideoFrame;

public class Colorizer extends ProcessingBlock {

    private final FrameQueue queue = new FrameQueue(1);

    public Colorizer() {
        instance = rs2_create_colorizer(RealSenseError.getInstance());
        RealSenseError.checkError();

        rs2_start_processing_queue(instance, queue.getInstance(), RealSenseError.getInstance());
        RealSenseError.checkError();
    }

    public VideoFrame colorize(VideoFrame original) {
        rs2_frame_add_ref(original.getInstance(), RealSenseError.getInstance());
        RealSenseError.checkError();

        rs2_process_frame(instance, original.getInstance(), RealSenseError.getInstance());
        RealSenseError.checkError();
        return (VideoFrame) queue.waitForFrame();
    }
}
