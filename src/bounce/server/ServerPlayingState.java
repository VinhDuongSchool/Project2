package bounce.server;

import bounce.common.Character;
import bounce.common.*;
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
        ExplorerGameServer egs = (ExplorerGameServer)game;

        egs.grid.MakePath(Arrays.stream(egs.characters).map(Character::getGamepos).collect(Collectors.toCollection(ArrayList::new)));
        Enemy e;
        Message m;
        //TODO do better
//        e = new Zombie(new Vector(64,32), new Vector(0, 0), egs.game_sprites.getSprite(0, 9));
//        egs.enemies.add(e);
//        egs.out_messages.add(Message.builder(
//                Message.MSG_TYPE.ADD_ENTITY, e.id)
//                .setEtype(Message.ENTITY_TYPE.ZOMBIE)
//                .setGamepos(e.getGamepos())
//                .setVelocity(e.getVelocity())
//                .setData(new Object[]{0,9}));
//        e = new Enemy(32*3,32*5, 0, 0, egs.game_sprites.getSprite(0, 9));
//        egs.enemies.add(e);
//        egs.out_messages.add( Message.add_entity(e.gamepos, e.getVelocity(),0,9, e.id, Message.ENTITY_TYPE.ENEMY));
//        e = new Enemy(0,0, 0, 0, egs.game_sprites.getSprite(1, 8));
//        egs.enemies.add(e);
//        egs.out_messages.add( Message.add_entity(e.gamepos, e.getVelocity(),1,8, e.id, Message.ENTITY_TYPE.ENEMY));
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

        egs.grid.MakePath(Arrays.stream(egs.characters).map(Character::getGamepos).collect(Collectors.toCollection(ArrayList::new)));

        //(Kevin) read all the messages
        for(var m = egs.in_messages.poll(); m != null; m = egs.in_messages.poll()){
            egs.handle_message(m);
        }

        //(Kevin) update all the characters
        Arrays.stream(egs.characters).forEach((c) ->{
            var oldpos = c.getGamepos();
            c.update(delta);

            //Kevin, check collision with the 8 neighbor tiles of the character and undo their movement if there is a collision
            egs.grid.getNeighbors(c.getGamepos()).stream()
                    .filter(t -> t.type == TileMap.TYPE.WALL)
                    .map(c::collides) // stream of collisions that may be null
                    .filter(Objects::nonNull)
                    .findAny().ifPresent(collision -> { // the actual collision object isnt useful, the minpentration doesnt work at all
                        c.setVelocity(c.getVelocity().scale(-1));
                        c.update(delta);
                        c.setVelocity(c.getVelocity().scale(-1));
                    });

            if(!oldpos.equals(c.getGamepos()))
                egs.out_messages.add(new Message(Message.MSG_TYPE.NEW_POSITION, c.getGamepos(), c.client_id, Message.ENTITY_TYPE.CHARACTER));
        });


        //(Kevin) remove dead/hit/etc stuff
        var toremove =  egs.enemies.stream().filter(e -> e.getHealth() <=0).collect(Collectors.toList());
        for (Enemy e : toremove) {
            egs.enemies.remove(e);
            egs.out_messages.add(new Message(Message.MSG_TYPE.REMOVE_ENTITY, null,  e.id, Message.ENTITY_TYPE.ENEMY));
        }


        //Kevin, update enemies
        egs.enemies.stream().forEach(e -> {
            e.update(delta, egs.characters,
                    //Kevin, may be cleaned up eventually
                    e.getClass() == ShadowArcher.class ? egs.grid.getranged_dir(e.getGamepos()) : egs.grid.get_dir(e.getGamepos()));
            egs.out_messages.add(Message.builder(Message.MSG_TYPE.NEW_POSITION, e.id).setEtype(Message.ENTITY_TYPE.ENEMY).setGamepos(e.getGamepos()));
        });

        for (int i = egs.projectiles.size()-1; i >= 0; i--){
            var p = egs.projectiles.get(i);
            if (p.getHit()){
                egs.projectiles.remove(i);
                egs.out_messages.add(new Message(Message.MSG_TYPE.REMOVE_ENTITY, null, p.id, Message.ENTITY_TYPE.PROJECTILE));
            }
        }


        //(Kevin) update all other entities
        egs.projectiles.forEach(p -> {
            p.update(delta);
            egs.out_messages.add(new Message(Message.MSG_TYPE.NEW_POSITION,  p.getGamepos(), p.id, Message.ENTITY_TYPE.PROJECTILE));
        });

        //Kevin, check if projectiles collide with enemies
        for (Projectile p : egs.projectiles) {
            for (Enemy e : egs.enemies) {
                if (p.collides(e) != null) {
                    e.setHealth(e.getHealth() - p.damage);
                    egs.out_messages.add(Message.builder(Message.MSG_TYPE.SET_HP, e.id).setHP(e.getHealth()).setEtype(Message.ENTITY_TYPE.ENEMY));
                    p.setHit(true);
                    break; // each projectile should only collide with a single entity
                }
            }
        }

	}

	@Override
	public int getID() {
		return ExplorerGameServer.PLAYINGSTATE;
	}

}
