package bounce.common.level;

import bounce.common.Enemy;
import bounce.common.ShadowArcher;
import bounce.common.Zombie;
import bounce.common.lib;
import jig.ConvexPolygon;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class Room {

    ArrayList<Door> doors;
    ArrayList<Spawner> spawners;
    final public Entity room_hitbox;
    final public int x, y, width, height;
    public boolean completed;

    public Room(int x, int y, int width, int height){
        doors = new ArrayList<>();
        spawners = new ArrayList<>();
        completed = false;

        x *= 32;
        y *= 32;
        width *= 32;
        height *= 32;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        //Kevin, make hitbox so we can check if players are in the room
        //collision box represents the the outline of the second square into the room so players should not get stuck in the door
        room_hitbox = new Entity(x,y);
        var s = new ConvexPolygon((float)width-128, (float)height-128);

        //Kevin, shift collision box into right position relative to the entity which is at the top left corner of the room (s.getmax())
        // shift box to second inner sqaure in room (+64)
        // shift collision box into game coordinates (-16)

        room_hitbox.addShape(s, new Vector(s.getMaxX()+64-16, s.getMaxY()+64-16), Color.transparent, Color.red);

    }

    public Room(Integer[] xywh){
        this(xywh[0], xywh[1],xywh[2],xywh[3]);
    }

    public void close(){
        doors.stream().forEach(Door::close);
    }

    public void open(){
        doors.stream().forEach(Door::open);

    }

    public Optional<ArrayList<Enemy>> update(int delta){
        if(spawners.stream().allMatch(Spawner::done)){

            completed = true;
        }

        ArrayList<Enemy> out = new ArrayList<>();

        spawners.stream()
                .filter(Predicate.not(Spawner::done))
                .map(s -> s.spawn(delta))
                .forEach(e -> e.ifPresent(out::add));

        if(out.isEmpty())
            return Optional.empty();
        return Optional.of(out);
    }

    public void addSpawner(Integer[] l, int x1, int y1, int x2, int y2){
        int count = l[1];
        int type = l[2];
        int xp = l[3]+x1;
        int yp = l[4]+y1;

        assert x1 < xp && xp <= x2;
        assert y1 < yp && yp <= y2;
        assert count > 0;
        var c = (type == 0) ? Zombie.class : ShadowArcher.class;
        spawners.add(new Spawner(c, new Vector(xp*32, yp*32), count));

    }

    public static class Spawner{

        private final Vector gamepos;
        private Constructor maker;
        private final Image im;
        private long timer = 1000;
        private final long time = 3000;
        private int count;

        public Spawner(Class<? extends Enemy> t, Vector gp, int c) {

            count = c;
            im = (t.getName().equals(Zombie.class.getName())) ? lib.game_sprites.getSprite(0, 9) :  lib.game_sprites.getSprite(3, 8);
            gamepos = gp;
            try {
                maker = t.getConstructor(Vector.class, Vector.class, Image.class);
            } catch (NoSuchMethodException e){
                e.printStackTrace();
            }

        }

        public boolean done(){
            return count <= 0;
        }

        public Optional<Enemy> spawn(int delta){
            if(timer < 0 && count > 0){
                count--;
                timer = time;
                try {
                    return Optional.of((Enemy) maker.newInstance(gamepos, lib.v0, im));
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            timer -= delta;
            return Optional.empty();
        }
    }
}
