package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class DataHandler {
    private static String JSON_DIRECTORY = "./resources/";

    protected static final boolean USE_NETWORK_TABLES = false;

    protected NetworkTable mainTable;

    protected NetworkTable table;
    protected NetworkTableEntry saveButton;

    public DataHandler(String tableName) {
        File jsonDirectory = new File(JSON_DIRECTORY);
        if (!jsonDirectory.exists()) {
            jsonDirectory.mkdirs();
        }

        if (USE_NETWORK_TABLES) {
            mainTable = NetworkTableInstance.getDefault().getTable("Config");
            table = mainTable.getSubTable(tableName);
            saveButton = table.getEntry(tableName + "_Save");
            saveButton.setBoolean(false);
            saveButton.addListener(event -> {
                save(this);
                saveButton.setBoolean(false);
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
        }

    }

    protected static Object load(Object obj) {
        String className = obj.getClass().getSimpleName();
        try {
            File file = new File(JSON_DIRECTORY + className + ".json");
            if (file.exists()) {
                BufferedReader reader;
                reader = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();
                Object obj2 = gson.fromJson(reader, obj.getClass());
                reader.close();
                obj = (obj2 != null) ? obj2 : obj;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }

    protected void save(Object obj) {
        try {
            File file = new File(JSON_DIRECTORY + obj.getClass().getSimpleName() + ".json");
            if (!file.exists())
                file.createNewFile();
            FileWriter writer = new FileWriter(file);
            Gson gson = new Gson();
            String json = gson.toJson(obj);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        save(this);
    }

    public static void main(String args[]) {
        DataHandler test = new DataHandler(Camera.class.getSimpleName());

        Camera camera = new Camera();
        camera.name = "Hub Target Camera";
        camera.path = "/dev/video0";
        camera.pixelFormat = "YUYV";
        camera.width = 640;
        camera.height = 360;
        camera.fps = 15;
        camera.brightness = 100;
        camera.whiteBalance = "auto";

        test.save(camera);
        Camera newCamera = new Camera();
        newCamera = (Camera) DataHandler.load(newCamera.getClass());
        System.out.println(newCamera.name);

    }

}
