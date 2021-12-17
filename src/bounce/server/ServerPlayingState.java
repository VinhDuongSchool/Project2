package bounce.server;

import bounce.common.Message;
import bounce.common.entities.Character;
import bounce.common.entities.*;
import bounce.common.level.Door;
import bounce.common.level.TileMap;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 *
 * Transitions From StartUpState
 *
 * Transitions To GameOverState
 */
public class ServerPlayingState extends BasicGameState {

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

    }

    @Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameServer egs = (ExplorerGameServer) game;
        g.drawString("SERVER", egs.ScreenWidth/2, egs.ScreenHeight/2);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

        ExplorerGameServer egs = (ExplorerGameServer) game;

        egs.grid.MakePath(Arrays.stream(egs.characters).filter(c -> !c.dead).map(Character::getGamepos).collect(Collectors.toCollection(ArrayList::new)));

        //(Kevin) read all the messages
        for (var m = egs.in_messages.poll(); m != null; m = egs.in_messages.poll()) {
            egs.handle_message(m);
        }

        //(Kevin) update all the characters
        Arrays.stream(egs.characters).filter(c -> !c.dead).forEach((c) -> {
            var oldpos = c.getGamepos();
            var oldAT = c.attack_timer;
            c.update(delta);

            //Kevin, check collision with the 8 neighbor tiles of the character and undo their movement if there is a collision
            egs.grid.getNeighbors(c.getGamepos()).stream()
                    .filter(t -> t.type == TileMap.TYPE.WALL || t.type == TileMap.TYPE.DOOR && !((Door) t).is_open)
                    .map(c::collides) // stream of collisions that may be null
                    .filter(Objects::nonNull)
                    .findAny().ifPresent(collision -> { // the actual collision object isnt useful, the minpentration doesnt work at all
                        if (c.attack_timer <= 0) // when attacking we dont move
                            c.setGamepos(c.getGamepos().add(c.getVelocity().scale(-1).scale(delta)));
                    });

            if (!oldpos.equals(c.getGamepos()))
                egs.out_messages.add(new Message(Message.MSG_TYPE.NEW_POSITION, c.getGamepos(), c.client_id, Message.ENTITY_TYPE.CHARACTER));
            if (oldAT != c.attack_timer)
                egs.out_messages.add(Message.builder(Message.MSG_TYPE.SET_ATTACK_TIMER, c.client_id).setIntData(c.attack_timer));
            if(c.health <= 0){
                c.dead = true;
                egs.out_messages.add(Message.builder(Message.MSG_TYPE.DEAD, c.client_id));
            }
        });


        { //rewrite TileMap update method but do it in the server's context
            var tm = egs.grid;
            if(tm.curRoom != null && tm.curRoom.completed && egs.enemies.isEmpty()){
                System.out.println("room completed");
                tm.curRoom.open();
                egs.out_messages.add(Message.builder(Message.MSG_TYPE.COMPLETE_ROOM, tm.rooms.indexOf(tm.curRoom)));
                tm.curRoom = null;
            }

            if(tm.curRoom == null){
                tm.rooms.stream().filter(r -> !r.completed).forEach(r -> {
                    if (Arrays.stream(egs.characters).filter(c -> !c.dead).map(r.room_hitbox::collides).allMatch(Objects::nonNull)){
                        tm.curRoom = r;
                        r.close();
                        egs.out_messages.add(Message.builder(Message.MSG_TYPE.CLOSE_ROOM, tm.rooms.indexOf(tm.curRoom)));
                    }
                });
            } else {
                tm.curRoom.update(delta).ifPresent(enems ->
                        enems.stream().forEach(e -> {
                            egs.enemies.add(e);
                            egs.out_messages.add(Message.builder(
                                            Message.MSG_TYPE.ADD_ENTITY, e.id)
                                    .setEtype(e.getClass().getName().equals(Zombie.class.getName()) ? Message.ENTITY_TYPE.ZOMBIE : Message.ENTITY_TYPE.SHADOWARCHER)
                                    .setGamepos(e.getGamepos())
                                    .setVelocity(e.getVelocity())
                                    .setData(new Object[]{0, 9}));
                        }));
            }
        }

        //(Kevin) remove dead/hit/etc stuff
        var toremove = egs.enemies.stream().filter(e -> e.getHealth() <= 0).collect(Collectors.toList());
        for (Enemy e : toremove) {
            egs.enemies.remove(e);
            egs.out_messages.add(new Message(Message.MSG_TYPE.REMOVE_ENTITY, null, e.id, Message.ENTITY_TYPE.ENEMY));
        }

        //Kevin, update enemies
        egs.enemies.stream().forEach(e -> {
            e.update(delta, egs.characters,
                    //Kevin, may be cleaned up eventually
                    e.getClass() == ShadowArcher.class ? egs.grid.getranged_dir(e.getGamepos()) : egs.grid.get_dir(e.getGamepos())
                    ).ifPresent(pr -> pr.stream().forEach(p -> {
                        egs.projectiles.add(p);
                        egs.out_messages.add(Message.builder(Message.MSG_TYPE.ADD_ENTITY, p.id)
                                .setEtype(Message.ENTITY_TYPE.PROJECTILE)
                                .setGamepos(p.getGamepos())
                                .setDir(p.curdir)
                                .setVelocity(p.getVelocity()));
                    }));
            egs.out_messages.add(Message.builder(Message.MSG_TYPE.NEW_POSITION, e.id).setEtype(Message.ENTITY_TYPE.ENEMY).setGamepos(e.getGamepos()));
        });

        //check player attacks, must be after both player and character are updated
        Arrays.stream(egs.characters).filter(c -> !c.hit_in_this_attack && c.attack_timer > 0).forEach(c -> {
            for(var e : egs.enemies){
                if(c.collides(e) != null){
                    e.setHealth(e.getHealth() - c.attack);
                    //player attack freeze is 500ms
                    e.attack_timer += 450;
                    c.hit_in_this_attack = true;
                    break;
                }
            }
        });

        egs.enemies.stream().filter(e -> e.getClass() == Zombie.class && e.attacking)
                .forEach(e -> {
                    for(var c : egs.characters){
                        if (e.collides(c) != null) {
                            c.health -= e.damage;
                            e.attacking = false;
                            egs.out_messages.add(Message.builder(Message.MSG_TYPE.SET_HP, c.client_id)
                                    .setHP(c.health)
                                    .setEtype(Message.ENTITY_TYPE.CHARACTER));
                        }
                    }
        });

        for (int i = egs.projectiles.size() - 1; i >= 0; i--) {
            var p = egs.projectiles.get(i);
            if (p.getHit()) {
                egs.projectiles.remove(i);
                egs.out_messages.add(new Message(Message.MSG_TYPE.REMOVE_ENTITY, null, p.id, Message.ENTITY_TYPE.PROJECTILE));
            }
        }

        //(Kevin) update all other entities
        egs.projectiles.forEach(p -> {
            p.update(delta);
            egs.out_messages.add(new Message(Message.MSG_TYPE.NEW_POSITION, p.getGamepos(), p.id, Message.ENTITY_TYPE.PROJECTILE));
        });

        //Kevin, check if projectiles collide with enemies
        for (Projectile p : egs.projectiles) {
            //Kevin, if projectile isnt sent by archer dont hit enemies
            if(p.sender.getClass() == Archer.class) {
                for (Enemy e : egs.enemies) {
                    if (p.collides(e) != null) {
                        e.setHealth(e.getHealth() - p.damage);
                        p.setHit(true);
                        break; // each projectile should only collide with a single entity
                    }
                }
            } else { // must be an enemy projectile
                for(var c : egs.characters){
                    if(c.collides(p) != null) {
                        c.health -= p.damage;
                        egs.out_messages.add(Message.builder(Message.MSG_TYPE.SET_HP, c.client_id)
                                .setHP(c.health)
                                .setEtype(Message.ENTITY_TYPE.CHARACTER));
                        p.setHit(true);
                        break;
                    }
                }
            }
            var currentProjectileTile = egs.grid.getTile(p.getGamepos()); //Get the tile the projectile is at.
            if (currentProjectileTile.type == TileMap.TYPE.WALL && p.collides(currentProjectileTile) != null)  //If the tile is a wallh
                p.setHit(true);
        }
    }

    @Override
	public int getID() {
		return ExplorerGameServer.PLAYINGSTATE;
	}

}
