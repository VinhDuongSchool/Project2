package bounce.common.entities;

import bounce.common.lib;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Optional;


public class Archer extends Character {
    Image temp_im;
    int attack_timer = 0;

    public Archer(Vector gp, Vector v, long id) {
        super(gp,v,id);
        addImage(lib.game_sprites.getSprite(1,10));
        health = 75;
        defense = 25;
        stamina = 100;
        magic = 0;
        attack = 75;
        speed = 0.3f;
        maxHealth = 75;
    }

    public void doAnim() {

    }

    @Override
    public Optional<ArrayList<Projectile>> primary() { //Primary attack
//                egc.projectiles.add(new Projectile(egc.character.getGamepos(), egc.character.getVelocity(), 0, lib.angle_index_to_dir[diridx])); //Set the initial location to the player.

        if (attack_timer <= 0) {
            //Kevin, make a new projectile with the proper stats and return it
            var d = lib.angle_index_to_dir[lookingDirIdx];
            var p = new Projectile(gamepos, lib.dir_enum_to_unit_vector(d).scale(0.4f), d, this);
            var ar = new ArrayList<Projectile>();
            ar.add(p);
            attack_timer = 500;
            return Optional.of(ar);
        }
        return Optional.empty();

    }

    @Override
    public void update(final int delta) { //To end the timer.

        super.update(delta);
        if (attack_timer > 0) {
            attack_timer -= delta;
        }
    }

}
