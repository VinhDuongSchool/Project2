package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Color;

import java.util.ArrayList;

public class Room {

    ArrayList<Door> doors;
    final public Entity room_hitbox;
    public int x, y, width, height;
    public Room(int x, int y, int width, int height){

        x *= 32;
        y *= 32;
        width *= 32;
        height *= 32;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        room_hitbox = new Entity(x,y);
        var s = new ConvexPolygon((float)width-128, (float)height-128);

        room_hitbox.addShape(s, new Vector(s.getMaxX()+64, s.getMaxY()+64), Color.transparent, Color.red);


        doors = new ArrayList<>();
    }
    public Room(Integer[] xywh){
        this(xywh[0], xywh[1],xywh[2],xywh[3]);
    }

    public void close(){
        System.out.println("room closed");
        doors.stream().forEach(Door::close);
    }

    public void open(){
        doors.stream().forEach(Door::open);

    }
}
