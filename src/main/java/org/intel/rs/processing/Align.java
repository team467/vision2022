package org.intel.rs.processing;

import static org.bytedeco.librealsense2.global.realsense2.rs2_create_align;
import static org.bytedeco.librealsense2.global.realsense2.rs2_frame_add_ref;
import static org.bytedeco.librealsense2.global.realsense2.rs2_process_frame;
import static org.bytedeco.librealsense2.global.realsense2.rs2_start_processing_queue;

import org.intel.rs.frame.FrameList;
import org.intel.rs.frame.FrameQueue;
import org.intel.rs.types.Stream;
import org.intel.rs.util.RealSenseError;

public class Align extends ProcessingBlock {

    private final FrameQueue queue = new FrameQueue(1);

    public Align(Stream alignTo) {
        instance = rs2_create_align(alignTo.getIndex(), RealSenseError.getInstance());
        RealSenseError.checkError();

        rs2_start_processing_queue(instance, queue.getInstance(), RealSenseError.getInstance());
        RealSenseError.checkError();
    }

    public FrameList process(FrameList original) {
        rs2_frame_add_ref(original.getInstance(), RealSenseError.getInstance());
        RealSenseError.checkError();

        rs2_process_frame(instance, original.getInstance(), RealSenseError.getInstance());
        RealSenseError.checkError();

        return queue.waitForFrames();
    }

}
