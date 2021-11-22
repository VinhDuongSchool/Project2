package bounce.common;


import jig.Vector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class lib {



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

