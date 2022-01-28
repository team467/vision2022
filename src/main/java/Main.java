// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.intel.rs.frame.DepthFrame;
import org.intel.rs.frame.FrameList;
import org.intel.rs.frame.VideoFrame;
import org.intel.rs.pipeline.Config;
import org.intel.rs.pipeline.Pipeline;
import org.intel.rs.processing.Align;
import org.intel.rs.types.Format;
import org.intel.rs.types.Stream;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSource;
import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableInstance;
import pipeline.YellowStickyNotePipeline;

/*
   JSON format:
   {
       "team": <team number>,
       "ntmode": <"client" or "server", "client" if unspecified>
       "cameras": [
           {
               "name": <camera name>
               "path": <path, e.g. "/dev/video0">
               "pixel format": <"MJPEG", "YUYV", etc>   // optional
               "width": <video mode width>              // optional
               "height": <video mode height>            // optional
               "fps": <video mode fps>                  // optional
               "brightness": <percentage brightness>    // optional
               "white balance": <"auto", "hold", value> // optional
               "exposure": <"auto", "hold", value>      // optional
               "properties": [                          // optional
                   {
                       "name": <property name>
                       "value": <property value>
                   }
               ],
               "stream": {                              // optional
                   "properties": [
                       {
                           "name": <stream property name>
                           "value": <stream property value>
                       }
                   ]
               }
           }
       ]
       "switched cameras": [
           {
               "name": <virtual camera name>
               "key": <network table key used for selection>
               // if NT value is a string, it's treated as a name
               // if NT value is a double, it's treated as an integer index
           }
       ]
   }
 */

public final class Main {
  private static String configFile = "/boot/frc.json";

  // For RealSense
  private static Align align = new Align(Stream.Color);
  private static Pipeline realSenseFramePipeline = new Pipeline();
  private static volatile boolean running = true;

  @SuppressWarnings("MemberName")
  public static class CameraConfig {
    public String name;
    public String path;
    public JsonObject config;
    public JsonElement streamConfig;
  }

  @SuppressWarnings("MemberName")
  public static class SwitchedCameraConfig {
    public String name;
    public String key;
  };

  public static int team;
  public static boolean server;
  public static List<CameraConfig> cameraConfigs = new ArrayList<>();
  public static List<SwitchedCameraConfig> switchedCameraConfigs = new ArrayList<>();
  public static List<VideoSource> cameras = new ArrayList<>();

  private Main() {
  }

  /**
   * Report parse error.
   */
  public static void parseError(String str) {
    System.err.println("config error in '" + configFile + "': " + str);
  }

  /**
   * Read single camera configuration.
   */
  public static boolean readCameraConfig(JsonObject config) {
    CameraConfig cam = new CameraConfig();

    // name
    JsonElement nameElement = config.get("name");
    if (nameElement == null) {
      parseError("could not read camera name");
      return false;
    }
    cam.name = nameElement.getAsString();

    // path
    JsonElement pathElement = config.get("path");
    if (pathElement == null) {
      parseError("camera '" + cam.name + "': could not read path");
      return false;
    }
    cam.path = pathElement.getAsString();

    // stream properties
    cam.streamConfig = config.get("stream");

    cam.config = config;

    cameraConfigs.add(cam);
    return true;
  }

  /**
   * Read single switched camera configuration.
   */
  public static boolean readSwitchedCameraConfig(JsonObject config) {
    SwitchedCameraConfig cam = new SwitchedCameraConfig();

    // name
    JsonElement nameElement = config.get("name");
    if (nameElement == null) {
      parseError("could not read switched camera name");
      return false;
    }
    cam.name = nameElement.getAsString();

    // path
    JsonElement keyElement = config.get("key");
    if (keyElement == null) {
      parseError("switched camera '" + cam.name + "': could not read key");
      return false;
    }
    cam.key = keyElement.getAsString();

    switchedCameraConfigs.add(cam);
    return true;
  }

  /**
   * Read configuration file.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  public static boolean readConfig() {
    // parse file
    JsonElement top;
    try {
      top = new JsonParser().parse(Files.newBufferedReader(Paths.get(configFile)));
    } catch (IOException ex) {
      System.err.println("could not open '" + configFile + "': " + ex);
      return false;
    }

    // top level must be an object
    if (!top.isJsonObject()) {
      parseError("must be JSON object");
      return false;
    }
    JsonObject obj = top.getAsJsonObject();

    // team number
    JsonElement teamElement = obj.get("team");
    if (teamElement == null) {
      parseError("could not read team number");
      return false;
    }
    team = teamElement.getAsInt();

    // ntmode (optional)
    if (obj.has("ntmode")) {
      String str = obj.get("ntmode").getAsString();
      if ("client".equalsIgnoreCase(str)) {
        server = false;
      } else if ("server".equalsIgnoreCase(str)) {
        server = true;
      } else {
        parseError("could not understand ntmode value '" + str + "'");
      }
    }

    // cameras
    JsonElement camerasElement = obj.get("cameras");
    if (camerasElement == null) {
      parseError("could not read cameras");
      return false;
    }
    JsonArray cameras = camerasElement.getAsJsonArray();
    for (JsonElement camera : cameras) {
      if (!readCameraConfig(camera.getAsJsonObject())) {
        return false;
      }
    }

    if (obj.has("switched cameras")) {
      JsonArray switchedCameras = obj.get("switched cameras").getAsJsonArray();
      for (JsonElement camera : switchedCameras) {
        if (!readSwitchedCameraConfig(camera.getAsJsonObject())) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Start running the camera.
   */
  public static VideoSource startCamera(CameraConfig config) {
    System.out.println("Starting camera '" + config.name + "' on " + config.path);
    CameraServer inst = CameraServer.getInstance();
    UsbCamera camera = new UsbCamera(config.name, config.path);
    // MjpegServer server = inst.startAutomaticCapture(camera);

    // Gson gson = new GsonBuilder().create();

    // camera.setConfigJson(gson.toJson(config.config));
    // camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    // if (config.streamConfig != null) {
    // server.setConfigJson(gson.toJson(config.streamConfig));
    // }

    return camera;
  }

  /**
   * Start running the switched camera.
   */
  public static MjpegServer startSwitchedCamera(SwitchedCameraConfig config) {
    System.out.println("Starting switched camera '" + config.name + "' on " + config.key);
    MjpegServer server = new MjpegServer("change", "error", 0);// =
                                                               // CameraServer.addSwitchedCamera(config.name);

    // NetworkTableInstance.getDefault()
    // .getEntry(config.key)
    // .addListener(event -> {
    // if (event.value.isDouble()) {
    // int i = (int) event.value.getDouble();
    // if (i >= 0 && i < cameras.size()) {
    // server.setSource(cameras.get(i));
    // }
    // } else if (event.value.isString()) {
    // String str = event.value.getString();
    // for (int i = 0; i < cameraConfigs.size(); i++) {
    // if (str.equals(cameraConfigs.get(i).name)) {
    // server.setSource(cameras.get(i));
    // break;
    // }
    // }
    // }
    // },
    // EntryListenerFlags.kImmediate | EntryListenerFlags.kNew |
    // EntryListenerFlags.kUpdate);

    return server;
  }

  /**
   * Main.
   */
  public static void main(String... args) {
    if (args.length > 0) {
      configFile = args[0];
    }

    // // read configuration
    // if (!readConfig()) {
    // return;
    // }

    // // start NetworkTables
    // NetworkTableInstance networkTableInstance =
    // NetworkTableInstance.getDefault();

    // if (server) {
    // System.out.println("Setting up NetworkTables server");
    // networkTableInstance.startServer();
    // } else {
    // System.out.println("Setting up NetworkTables client for team " + team);
    // networkTableInstance.startClientTeam(team);
    // networkTableInstance.startDSClient();
    // // System.out.println(networkTableInstance);
    // }

    // // start cameras
    // for (CameraConfig config : cameraConfigs) {
    // cameras.add(startCamera(config));
    // }

    // // start switched cameras
    // for (SwitchedCameraConfig config : switchedCameraConfigs) {
    // startSwitchedCamera(config);
    // }

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

    // // start image processing on camera 0 if present
    // if (cameras.size() >= 1) {
    // VisionThread visionThread1 = new VisionThread(cameras.get(0),
    // new YellowStickyNotePipeline(), pipeline -> {
    // for (MatOfPoint mat : pipeline.convexHullsOutput()) {
    // System.out.println(mat.height());
    // }
    // });
    // /*
    // * something like this for GRIP:
    // * VisionThread visionThread = new VisionThread(cameras.get(0),
    // * new GripPipeline(), pipeline -> {
    // * ...
    // * });
    // */
    // visionThread1.start();
    // }

    // loop forever
    for (;;) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
        return;
      }
    }
  }

  private static YellowStickyNotePipeline yellowStickyNotePipeline = new YellowStickyNotePipeline();

  public static void readFrames() {
    FrameList frames = realSenseFramePipeline.waitForFrames();
    FrameList alignedFrames = align.process(frames);

    VideoFrame colorFrame = alignedFrames.getColorFrame();
    DepthFrame depthFrame = alignedFrames.getDepthFrame();

    Mat colorMat = new Mat(colorFrame.getHeight(), colorFrame.getWidth(), CvType.CV_8UC3);
    colorMat.put(0, 0, colorFrame.getBytes());

    yellowStickyNotePipeline.process(colorMat);
    for (MatOfPoint hull : yellowStickyNotePipeline.convexHullsOutput()) {
      System.out.println(depthFrame.getDistance(hull.width() / 2, hull.height() / 2));
    }

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
