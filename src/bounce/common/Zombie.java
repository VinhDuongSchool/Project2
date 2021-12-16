package bounce.common;

import jig.ConvexPolygon;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Zombie extends Enemy{
    Image temp_im;

    public Zombie(Vector pos, Vector vel, Image img){
        super(pos, vel, img);
    }
    public Zombie(Vector pos, Vector vel, Image img, long _id){
        super(pos, vel, img, _id);
    }

    @Override
    public Optional<ArrayList<Projectile>> attack(Character c){
        attack_timer = 3000; //Set a timer.

        //Kevin, attack in the direction of the player
        //not using lib.dir_from_point_to_point incase we need to add multiple shapes depending on direction

        double ang = (c.getGamepos().angleTo(gamepos)+180 + 360 - 45)%360;
        int diridx = (int)Math.round((ang)/45);
        var offsetdir = lib.dir_enum_to_dir_vector(lib.angle_index_to_dir[diridx]);
        var s = new ConvexPolygon(lib.sqr.getPoints()); //Set a box to point right
        attack_shapes.add(s);
        addShape(s, offsetdir.scale(32.0f));

        return Optional.empty();
    }

    @Override
    public Optional<ArrayList<Projectile>> update(final int delta, Character[] characters, lib.DIRS td){
        //Kevin, if stunned by attack or player is on top of us
        if(td == null || attack_timer > 0) {
            attack_timer -= delta;
            return Optional.empty();
        }

        //Kevin, if we are done attacking remove shapes
        if(attack_shapes.size() > 0){
            attack_shapes.stream().forEach(this::removeShape);
            attack_shapes.clear();
        }

        //Kevin, get character with minimum distance to enemy
        var c = Arrays.stream(characters)
                .min((a,b)-> (int)(gamepos.distance(a.getGamepos()) - gamepos.distance(b.getGamepos()))).get();
        float distance = gamepos.distance(c.getGamepos());

        curdir = td;
        if(distance <= 40){
            attack(c);
            attacking = true;
            temp_im = lib.game_sprites.getSprite(0,11); //Add an explosion image when attacking.
            addImage(temp_im);
        } else {
            velocity = lib.dir_enum_to_unit_vector(curdir).scale(0.04f);
            gamepos = gamepos.add(velocity.scale(delta));
            attacking = false;
            removeImage(temp_im); //Remove the image when it can move again.
        }

        setPosition(gamepos);
        return Optional.empty();
    }
}
