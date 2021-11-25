package bounce.common;


import jig.ConvexPolygon;
import jig.Shape;
import jig.Vector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class lib {

    //Kevin, a square, to be used to make collision shapes for entities, so we dont have float arrays everywhere when all we want is offset squares
    public static Shape sqr = new ConvexPolygon(new float[]{
            -16, 16,
            -16, -16,
            16, -16,
            16, 16
    });

    public static enum DIRS {
        NORTH,
        NORTHEAST,
        NORTHWEST,
        WEST,
        EAST,
        SOUTH,
        SOUTHWEST,
        SOUTHEAST
    }

    //Kevin, Turn input into the direction ingame, assuming input is a bool array of "wasd" true if the key is down
    public static HashMap<List<Boolean>,DIRS> input_to_dir = new HashMap<>(){{
        put(List.of(new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE}), DIRS.WEST);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE}), DIRS.SOUTH);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE}), DIRS.EAST);
        put(List.of(new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE}), DIRS.NORTH);
        put(List.of(new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE}), DIRS.NORTHWEST);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE}), DIRS.SOUTHWEST);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE}), DIRS.SOUTHEAST);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE}), DIRS.NORTHEAST);
    }};

    // returns a new vector that turns the world coordinates to screen coordinates
    public static Vector to_screen(float wx, float wy, Vector screen_orgin){
        return screen_orgin.transform(new float[]{1,0, 0, 1, (wx + wy),   (wy - wx)/2});

    }
    public static Vector to_screen(float wx, float wy, Vector screen_orgin, float h){
        return screen_orgin.transform(new float[]{1,0, 0, 1, (wx + wy),  -h + (wy - wx)/2});

    }
    public static Vector to_screen(Vector wxy, Vector screen_orgin, float h){
        float wx = wxy.getX();
        float wy = wxy.getY();
        return screen_orgin.transform(new float[]{1,0, 0, 1, (wx + wy),  -h + (wy - wx)/2});

    }
    public static Vector to_screen(float wx, float wy){
        return new Vector((wy + wx), (wy - wx)/2);
    }
    public static Vector to_screen(Vector wxy){
        float wx = wxy.getX();
        float wy = wxy.getY();
        return new Vector((wy + wx), (wy - wx)/2);
    }
    public static Vector to_screen(Vector wxy, Vector screen_orgin){
        float wx = wxy.getX();
        float wy = wxy.getY();
        return screen_orgin.transform(new float[]{1,0, 0, 1, (wx + wy),  wy/2 - wx/2});
    }

    public static Vector screen_to_game(float wx, float wy){
        return new Vector( 1/(wx + wy),  2/(wy -wx));
    }

    public static Vector screen_to_game(Vector sxy){
        float sx = sxy.getX();
        float sy = sxy.getY();
        return new Vector( 1/(sx + sy),  2/(sy -sx));
    }

    // (Kevin) functions to make and start a thread that deals with a queue and socket stream
    public static Thread make_and_start_reader(ConcurrentLinkedQueue<Message> msg_queue, ObjectInputStream ois){
        var t = new Thread(() -> {
            while (true) {
                try {
                    msg_queue.add((Message) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        });
        t.start();
        return t;
    }

    public static Thread make_and_start_writer(ConcurrentLinkedQueue<Message> msg_queue, ObjectOutputStream oos){
        var t = new Thread(() -> {
            while (true){
                var m = msg_queue.poll();
                if (m == null) continue;
                try {
                    oos.writeObject(m);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        });
        t.start();
        return t;
    }
}

