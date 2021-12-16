package bounce.common;


import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.SpriteSheet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class lib {
    public static final String SPRITES = "bounce/resource/sprites.png";
    public static final String PROJECTILE = "bounce/resource/projectile.png";
    public static final String UD = "bounce/resource/UD.png";
    public static final String LR = "bounce/resource/LR.png";
    public static final String UR = "bounce/resource/UR.png";
    public static final String DR = "bounce/resource/DR.png";
    public static final String PILEOFGOLD = "bounce/resource/PileOfGold.png";
    public static final String POTION = "bounce/resource/Potion.png";

    public static final String SpearManIdle = "bounce/resource/bSpearman/bSpearman_Idle_strip8.png";

    public static final String SpearManAttackNorth="bounce/resource/bSpearman/_attack/bSpearman_Attack01_Up_strip8.png";
    public static final String SpearManAttackNorthEast="bounce/resource/bSpearman/_attack/bSpearman_Attack01_UpR_strip8.png";
    public static final String SpearManAttackEast="bounce/resource/bSpearman/_attack/bSpearman_Attack01_Right_strip8.png";
    public static final String SpearManAttackSouthEast="bounce/resource/bSpearman/_attack/bSpearman_Attack01_DownR_strip8.png";
    public static final String SpearManAttackSouth="bounce/resource/bSpearman/_attack/bSpearman_Attack01_Down_strip8.png";
    public static final String SpearManAttackSouthWest="bounce/resource/bSpearman/_attack/bSpearman_Attack01_DownL_strip8.png";
    public static final String SpearManAttackWest="bounce/resource/bSpearman/_attack/bSpearman_Attack01_Left_strip8.png";
    public static final String SpearManAttackNorthWest="bounce/resource/bSpearman/_attack/bSpearman_Attack01_UpL_strip8.png";

    public static final String SpearManWalkingNorth="bounce/resource/bSpearman/_walk/bSpearman_Walk_Up_strip10.png";
    public static final String SpearManWalkingNorthEast="bounce/resource/bSpearman/_walk/bSpearman_Walk_UpR_strip10.png";
    public static final String SpearManWalkingEast="bounce/resource/bSpearman/_walk/bSpearman_Walk_Right_strip10.png";
    public static final String SpearManWalkingSouthEast="bounce/resource/bSpearman/_walk/bSpearman_Walk_DownR_strip10.png";
    public static final String SpearManWalkingSouth="bounce/resource/bSpearman/_walk/bSpearman_Walk_Down_strip10.png";
    public static final String SpearManWalkingSouthWest="bounce/resource/bSpearman/_walk/bSpearman_Walk_DownL_strip10.png";
    public static final String SpearManWalkingWest="bounce/resource/bSpearman/_walk/bSpearman_Walk_Left_strip10.png";
    public static final String SpearManWalkingNorthWest="bounce/resource/bSpearman/_walk/bSpearman_Walk_UpL_strip10.png";

    public static final String SpearManDeath = "bounce/resource/bSpearman/_death/bSpearman_Die_Down_strip8.png";


    public static SpriteSheet game_sprites;

    private static boolean LOADEDSPRITES;

    public static void LOAD_SPRITES_ONCE(){
        if(LOADEDSPRITES)
            return;

        ResourceManager.loadImage(SPRITES);
        ResourceManager.loadImage(PROJECTILE);
        ResourceManager.loadImage(UD);
        ResourceManager.loadImage(LR);
        ResourceManager.loadImage(UR);
        ResourceManager.loadImage(DR);

        ResourceManager.loadImage(POTION);
        ResourceManager.loadImage(PILEOFGOLD);

        ResourceManager.loadImage(SpearManIdle);

        ResourceManager.loadImage(SpearManAttackNorth);
        ResourceManager.loadImage(SpearManAttackNorthEast);
        ResourceManager.loadImage(SpearManAttackEast);
        ResourceManager.loadImage(SpearManAttackSouthEast);
        ResourceManager.loadImage(SpearManAttackSouth);
        ResourceManager.loadImage(SpearManAttackSouthWest);
        ResourceManager.loadImage(SpearManAttackWest);
        ResourceManager.loadImage(SpearManAttackNorthWest);

        ResourceManager.loadImage(SpearManWalkingNorth);
        ResourceManager.loadImage(SpearManWalkingNorthEast);
        ResourceManager.loadImage(SpearManWalkingEast);
        ResourceManager.loadImage(SpearManWalkingSouthEast);
        ResourceManager.loadImage(SpearManWalkingSouth);
        ResourceManager.loadImage(SpearManWalkingSouthWest);
        ResourceManager.loadImage(SpearManWalkingWest);
        ResourceManager.loadImage(SpearManWalkingNorthWest);

        ResourceManager.loadImage(SpearManDeath);
        game_sprites  = ResourceManager.getSpriteSheet(SPRITES, 64,64);
        LOADEDSPRITES = true;
    }

    //Kevin, a square, to be used to make collision shapes for entities, so we dont have float arrays everywhere when all we want is offset squares
    public static Shape sqr = new ConvexPolygon(new float[]{
            -16, 16,
            -16, -16,
            16, -16,
            16, 16
    });
    public static final Vector v0 = new Vector(0,0);

    public static enum DIRS {
        //Kevin, values number clockwise from NORTH
        NORTH(0),
        NORTHEAST(1),
        NORTHWEST(7),
        WEST(6),
        EAST(2),
        SOUTH(4),
        SOUTHWEST(5),
        SOUTHEAST(3);

        public final int val;

        DIRS(int value){
            this.val = value;
        }
    }

    //Kevin, convert an int into a dir, mainly used to convert a mouse direction into a dir, 0 and 8 are the same to deal with 360-0 boundry
    public static final DIRS[] angle_index_to_dir = new DIRS[]{DIRS.NORTHEAST,DIRS.EAST, DIRS.SOUTHEAST, DIRS.SOUTH,  DIRS.SOUTHWEST, DIRS.WEST, DIRS.NORTHWEST, DIRS.NORTH,  DIRS.NORTHEAST};

    // convert a dir enum to the corresponding directional unit vector, useful for directional movement as velocty's dir needs to be a unit vector
    public static Vector dir_enum_to_unit_vector(DIRS d){
        return (new Vector(1,0)).rotate(45 * d.val);
    }

    // convert a dir enum to a dir vector where the xy are always +-1 or 0, useful for grid offsets
    public static Vector dir_enum_to_dir_vector(DIRS d){
        var v= dir_enum_to_unit_vector(d);
        return new Vector(Math.signum(v.getX()), Math.signum(v.getY()));
    }

    // convert the angle between two points into the corresponding dir vector
    public static DIRS dir_from_point_to_point(Vector a, Vector b){
        double ang = (a.angleTo(b)+180 + 360 - 45)%360;
        int diridx = (int)Math.round((ang)/45);
        return lib.angle_index_to_dir[diridx];
    }

    //Kevin, Turn input into the direction ingame, assuming input is a bool array of "wasd" where idx==true if the key is down
    private static final HashMap<List<Boolean>,DIRS> input_to_dir = new HashMap<>(){{
        put(List.of(new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE}), DIRS.WEST);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE}), DIRS.SOUTH);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE}), DIRS.EAST);
        put(List.of(new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE}), DIRS.NORTH);
        put(List.of(new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE}), DIRS.NORTHWEST);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE}), DIRS.SOUTHWEST);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE}), DIRS.SOUTHEAST);
        put(List.of(new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE}), DIRS.NORTHEAST);
    }};

    //Kevin, abstraction on how wasd is converted into a dir
    public static DIRS wasd_to_dir(List<Boolean> wasd){
        return input_to_dir.get(wasd);
    }

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

