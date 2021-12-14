package bounce.common;

import jig.ConvexPolygon;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Warrior extends Character {
    Image temp_im;

    public Warrior(Vector gp, Vector v, Image img, long id) {
        super(gp,v,img,id);
        health = 100;
        defense = 50;
        stamina = 100;
        magic = 0;
        attack = 50;
        speed = 0.3f;
    }

    @Override
    public Optional<ArrayList<Projectile>> primary() {
        //0 and 8 map to the same value
        ArrayList<lib.DIRS> attack_dirs;
        if (lookingDirIdx == 0 || lookingDirIdx == 8){
            //Kevin, deal with edge case
            attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.DIRS.NORTHEAST, lib.DIRS.NORTH, lib.DIRS.EAST}));
        } else {
            //Kevin, otherwise get neighbors directly
            attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.angle_index_to_dir[lookingDirIdx-1], lib.angle_index_to_dir[lookingDirIdx], lib.angle_index_to_dir[lookingDirIdx+1]}));
        }

        //Kevin, if we already attacked cant attack again
        if (attack_timer > 0)
            return Optional.empty();

        temp_im = lib.game_sprites.getSprite(0,11);
        addImage(temp_im);
        countdown = 500;

        //Kevin, set attack timer, for each dir in the list create a new shape,
        //keep a reference to the shape so we can delete it later,
        //add the shape to the entity with the specific offset for the dir it should be in
        attack_timer = 1000;
        for(var d : attack_dirs){
            var s = new ConvexPolygon(lib.sqr.getPoints());
            attack_shapes.add(s);
            addShape(s,lib.dir_enum_to_dir_vector(d).scale(32));
        }
        return Optional.empty();

    }

    @Override
    public void update(int delta) { //To check the countdown timer.
        super.update(delta);
        if (countdown <= 0) {
            removeImage(temp_im);
        } else {
            countdown -= delta;
        }

    }
}
