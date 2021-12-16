package bounce.common;

import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Warrior extends Character {

    SpriteSheet IdleSpriteSheet;

    SpriteSheet attackNorthSpriteSheet;
    SpriteSheet attackNorthWestSpriteSheet;
    SpriteSheet attackWestSpriteSheet;
    SpriteSheet attackSouthWestSpriteSheet;
    SpriteSheet attackSouthSpriteSheet;
    SpriteSheet attackSouthEastSpriteSheet;
    SpriteSheet attackEastSpriteSheet;
    SpriteSheet attackNorthEastSpriteSheet;

    SpriteSheet WalkingNorthSpriteSheet;
    SpriteSheet WalkingNorthWestSpriteSheet;
    SpriteSheet WalkingWestSpriteSheet;
    SpriteSheet WalkingSouthWestSpriteSheet;
    SpriteSheet WalkingSouthSpriteSheet;
    SpriteSheet WalkingSouthEastSpriteSheet;
    SpriteSheet WalkingEastSpriteSheet;
    SpriteSheet WalkingNorthEastSpriteSheet;

    SpriteSheet DeathSpriteSheet;

    Animation curanim;
    lib.DIRS animdir;

    public Warrior(Vector gp, Vector v, long id) {
        super(gp,v,id);
        health = 100;
        defense = 50;
        stamina = 100;
        magic = 0;
        attack = 50;
        speed = 0.3f;
        // load all animaions

        //this spritesheet will not be used as an animation
        IdleSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManIdle), 183,138);
        //default starting img added
        // all of these spritesheets will be used for animations
        attackNorthSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManAttackNorth).getScaledCopy(.5f),72,79);//vector offset needs to be -5 -1

        attackNorthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManAttackNorthEast).getScaledCopy(.5f),85,72); //vector offset unchanged
        attackEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManAttackEast).getScaledCopy(.5f),117,65); // vector offset -10 -1
        attackSouthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManAttackSouthEast).getScaledCopy(.5f),104,68); // vector offset -20
        attackSouthSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManAttackSouth).getScaledCopy(.5f),79,86); // vector offset unchanged
        attackSouthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManAttackSouthWest).getScaledCopy(.5f),86,78);//vector unchanged
        attackWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManAttackWest).getScaledCopy(.5f),115,69);// vector offset -15 -1
        attackNorthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManAttackNorthWest).getScaledCopy(.5f),99,66);// vector offset -5 f-1

        //same with these
        WalkingNorthSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManWalkingNorth).getScaledCopy(.5f),49,79); // vector offset -10 -1
        WalkingNorthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManWalkingNorthEast).getScaledCopy(.5f),75,69);// vector unchanged
        WalkingEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManWalkingEast).getScaledCopy(.5f),95,61);// vector unchanged
        WalkingSouthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManWalkingSouthEast).getScaledCopy(.5f),75,66); // vector unchanged
        WalkingSouthSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManWalkingSouth).getScaledCopy(.5f),52,67); // vector unchanged
        WalkingSouthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManWalkingSouthWest).getScaledCopy(.5f),79,66);// vector offset -15 -1
        WalkingWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManWalkingWest).getScaledCopy(.5f),101,61);// vector unchanged
        WalkingNorthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManWalkingNorthWest).getScaledCopy(.5f),82,71);// vector unchanged

        // same with this one
        DeathSpriteSheet = new SpriteSheet(ResourceManager.getImage(lib.SpearManDeath).getScaledCopy(.5f),77,79); // vector offset -10 -1

        curanim = new Animation(WalkingNorthSpriteSheet, 50);
        addAnimation(curanim);
    }


    public void doAnim(){
        if(attack_timer > 0 && !doingAttackAnim) {
            doingAttackAnim = true;
            removeAnimation(curanim);
            switch (lib.angle_index_to_dir[lookingDirIdx]) {
                // GAME NORTH IS SCREEN NORTHWEST
                case NORTHEAST:
                    curanim = new Animation(attackEastSpriteSheet, 50);
                    break;
                case NORTH:
                    curanim = new Animation(attackNorthEastSpriteSheet, 50);
                    break;
                case EAST:
                    curanim = new Animation(attackSouthEastSpriteSheet, 50);
                    break;
                case SOUTHEAST:
                    curanim = new Animation(attackSouthSpriteSheet, 50);
                    break;
                case SOUTH:
                    curanim = new Animation(attackSouthWestSpriteSheet, 50);
                    break;
                case SOUTHWEST:
                    curanim = new Animation(attackWestSpriteSheet, 50);
                    break;
                case WEST:
                    curanim = new Animation(attackNorthWestSpriteSheet, 50);
                    break;
                case NORTHWEST:
                    curanim = new Animation(attackNorthSpriteSheet, 50);
                    break;
            }
            addAnimation(curanim);
            curanim.setLooping(false);
        }

        if(curdir == null && null != animdir){
            curanim.stop();
            animdir = curdir;
        }

        if(curdir != animdir){
            removeAnimation(curanim);
            switch (curdir){
                // GAME NORTH IS SCREEN NORTHWEST
                case NORTHEAST:
                    curanim = new Animation(WalkingEastSpriteSheet, 50);
                    break;
                case NORTH:
                    curanim = new Animation(WalkingNorthEastSpriteSheet, 50);
                    break;
                case EAST:
                    curanim = new Animation(WalkingSouthEastSpriteSheet, 50);
                    break;
                case SOUTHEAST:
                    curanim = new Animation(WalkingSouthSpriteSheet, 50);
                    break;
                case SOUTH:
                    curanim = new Animation(WalkingSouthWestSpriteSheet, 50);
                    break;
                case SOUTHWEST:
                    curanim = new Animation(WalkingWestSpriteSheet, 50);
                    break;
                case WEST:
                    curanim = new Animation(WalkingNorthWestSpriteSheet, 50);
                    break;
                case NORTHWEST:
                    curanim = new Animation(WalkingNorthSpriteSheet, 50);
                    break;
            }
            animdir = curdir;
            addAnimation(curanim);
        }
    }

    @Override
    public Optional<ArrayList<Projectile>> primary() {

        //Kevin, if we already attacked cant attack again
        if (attack_timer > 0)
            return Optional.empty();

        //0 and 8 map to the same value
        ArrayList<lib.DIRS> attack_dirs;
        if (lookingDirIdx == 0 || lookingDirIdx == 8){
            //Kevin, deal with edge case
            attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.DIRS.NORTHEAST, lib.DIRS.NORTH, lib.DIRS.EAST}));
        } else {
            //Kevin, otherwise get neighbors directly
            attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.angle_index_to_dir[lookingDirIdx-1], lib.angle_index_to_dir[lookingDirIdx], lib.angle_index_to_dir[lookingDirIdx+1]}));
        }


        //Kevin, set attack timer, for each dir in the list create a new shape,
        //keep a reference to the shape so we can delete it later,
        //add the shape to the entity with the specific offset for the dir it should be in
        attack_timer = 500;
        for(var d : attack_dirs){
            var s = new ConvexPolygon(lib.sqr.getPoints());
            attack_shapes.add(s);
            addShape(s,lib.dir_enum_to_dir_vector(d).scale(32));
        }

        doAnim();
        return Optional.empty();
    }

    @Override
    public void update(final int delta) { //To check the countdown timer.
        super.update(delta);

        doAnim();

        if(attack_timer > 0)
            return;

        // if we reach here we are done doing an attack
        if (attack_shapes.size() > 0){
            doingAttackAnim = false;
            attack_shapes.stream().forEach(this::removeShape);
            attack_shapes.clear();
        }
    }
}
