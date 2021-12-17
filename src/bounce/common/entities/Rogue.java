package bounce.common.entities;

import bounce.common.lib;
import jig.Vector;

import java.util.ArrayList;
import java.util.Optional;


public class Rogue extends Character {
    public Rogue(Vector gp, Vector v, long id) {
        super(gp,v,id);
        addImage(lib.game_sprites.getSprite(2,10));
        health = 15;
        defense = 25;
        stamina = 100;
        magic = 0;
        attack = 2;
        speed = 0.3f;
        maxHealth = 15;
    }

    public void doAnim() {

    }

    @Override
    public Optional<ArrayList<Projectile>> primary() { //Primary attack

        //Kevin, if we already attacked cant attack again
        if (attack_timer > 0)
            return Optional.empty();

        //Kevin, make a new projectile with the proper stats and return it
        var d = lib.angle_index_to_dir[lookingDirIdx];
        var p = new Projectile(gamepos, lib.dir_enum_to_unit_vector(d).scale(0.2f), d,this, attack);
        var ar = new ArrayList<Projectile>();
        ar.add(p);
        attack_timer = 1500;
        return Optional.of(ar);
    }

    //Override update when its needed

}
