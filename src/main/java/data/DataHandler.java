package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.google.gson.Gson;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class DataHandler {
    private static String JSON_DIRECTORY = "./resources/";

    private static HashMap<String, Object> networkedClasses = new HashMap<String, Object>();

    protected static NetworkTable mainTable = NetworkTableInstance.getDefault().getTable("Config");

    protected NetworkTable table;
    protected NetworkTableEntry saveButton;

    public DataHandler(String tableName) {
        File jsonDirectory = new File(JSON_DIRECTORY);
        if (!jsonDirectory.exists()) {
            jsonDirectory.mkdirs();
        }
        table = mainTable.getSubTable(tableName);
        saveButton = table.getEntry(tableName + "_Save");
        saveButton.setBoolean(false);
        saveButton.addListener(event -> {
            save(this);
            saveButton.setBoolean(false);
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    }

    protected static <T> Object load(Object obj) {
        String className = obj.getClass().getSimpleName();
        NetworkTable table = mainTable.getSubTable(className);
        try {
            System.out.println(JSON_DIRECTORY + className + ".json");
            File file = new File(JSON_DIRECTORY + className + ".json");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();
                Object obj2 = gson.fromJson(reader, obj.getClass());
                reader.close();

                if (obj2 != null) {
                    obj = obj2;
                }

                networkedClasses.put(className, obj);

                if (obj == null) {
                    System.err.println("Darn 2");
                }

                Field[] fields = obj.getClass().getDeclaredFields();

                for (Field field : fields) {
                    String fieldName = field.getName();
                    String fieldType = field.getType().getSimpleName();
                    NetworkTableEntry entry = table.getEntry(fieldName);
                    if (fieldType.equals("double")) {
                        entry.setDouble(field.getDouble(obj));
                        entry.addListener(event -> {
                            try {
                                field.setDouble(networkedClasses.get(className),
                                        event.getEntry().getValue().getDouble());
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
                    } else if (fieldType.equals("int")) {
                        entry.setDouble(field.getDouble(obj));
                        entry.addListener(event -> {
                            try {
                                field.setInt(networkedClasses.get(className),
                                        (int) event.getEntry().getValue().getDouble());
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
                    } else if (fieldType.equals("String")) {
                        entry.setString((String) field.get(obj));
                        entry.addListener(event -> {
                            try {
                                field.set(networkedClasses.get(className), event.getEntry().getValue());
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
                    }
                }
            }
        } catch (IOException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return networkedClasses.get(className);
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
