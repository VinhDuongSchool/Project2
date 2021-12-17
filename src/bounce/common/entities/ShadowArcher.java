package bounce.common.entities;

import bounce.common.lib;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ShadowArcher extends Enemy{

    public ShadowArcher(Vector pos, Vector vel, Image img){
        super(pos, vel, img);
    }
    public ShadowArcher(Vector pos, Vector vel, Image img, long _id){
        super(pos, vel, img, _id);
    }

    @Override
    public Optional<ArrayList<Projectile>> attack(Character character){
        //Kevin, make a new projectile with the proper stats and return it
        attack_timer = 3000;
        var d = lib.dir_from_point_to_point(character.getGamepos(), gamepos);
        curdir = d;
        var p = new Projectile(super.getGamepos(), lib.dir_enum_to_unit_vector(d).scale(0.05f), d, this);
        var ar = new ArrayList<Projectile>();
        ar.add(p);

        return Optional.of(ar);
    }

    @Override
    public Optional<ArrayList<Projectile>> update(final int delta, Character[] characters, lib.DIRS td){
        setPosition(gamepos);

        //Kevin, if stunned by attack or player is on top of us
        if(attack_timer > 0) {
            attack_timer -= delta;
            return Optional.empty();
        }

        if(td != null)
            curdir = td;

        //Kevin, get character with minimum distance to enemy
        var c = Arrays.stream(characters).min((a,b) ->
                (int)(gamepos.distance(a.getGamepos()) - gamepos.distance(b.getGamepos()))
        ).get();

        //Kevin, fire at the player if we are on line with the player
        if(td == null){

            return attack(c);
        } else {
            velocity = lib.dir_enum_to_unit_vector(td).scale(0.03f);
            gamepos = gamepos.add(velocity.scale(delta));
        }

        setPosition(gamepos);
        return Optional.empty();
    }
}
