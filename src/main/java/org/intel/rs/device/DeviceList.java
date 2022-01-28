package org.intel.rs.device;

import static org.bytedeco.librealsense2.global.realsense2.rs2_create_device;
import static org.bytedeco.librealsense2.global.realsense2.rs2_delete_device_list;
import static org.bytedeco.librealsense2.global.realsense2.rs2_device_list_contains;
import static org.bytedeco.librealsense2.global.realsense2.rs2_get_device_count;
import static org.intel.rs.util.RealSenseUtil.toBoolean;

import java.util.Iterator;

import org.bytedeco.librealsense2.rs2_device;
import org.bytedeco.librealsense2.rs2_device_list;
import org.intel.rs.util.NativeDecorator;
import org.intel.rs.util.NativeList;
import org.intel.rs.util.NativeListIterator;
import org.intel.rs.util.RealSenseError;

public class DeviceList implements NativeDecorator<rs2_device_list>, NativeList<Device> {
    protected rs2_device_list instance;

    public DeviceList(rs2_device_list instance) {
        this.instance = instance;
    }

    public boolean contains(Device device) {
        int result = rs2_device_list_contains(instance, device.instance, RealSenseError.getInstance());
        RealSenseError.checkError();
        return toBoolean(result);
    }

    @Override
    public Device get(int index) {
        rs2_device device = rs2_create_device(instance, index, RealSenseError.getInstance());
        RealSenseError.checkError();
        return new Device(device);
    }

    @Override
    public int count() {
        int deviceCount = rs2_get_device_count(instance, RealSenseError.getInstance());
        RealSenseError.checkError();

        return deviceCount;
    }

    @Override
    public rs2_device_list getInstance() {
        return instance;
    }

    @Override
    public void release() {
        rs2_delete_device_list(instance);
    }

    @Override
    public Iterator<Device> iterator() {
        return new NativeListIterator<>(this);
    }
}
