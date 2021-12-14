package bounce.client;

import bounce.common.*;
import jig.Vector;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


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
public class ClientPlayingState extends BasicGameState {

    Vector mp = new Vector(0,0);
    int lastMouseIdx = 0;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
        ExplorerGameClient egc = (ExplorerGameClient)game;

        if(egc.is_connected)
            return;

        container.setSoundOn(true);


//        egc.enemies.add(new Enemy(64,32, 0, 0, egc.game_sprites.getSprite(0, 9))); //Add the enemies
        egc.enemies.add(new Zombie(
                new Vector(64,32),
                new Vector(0, 0),
                ExplorerGameClient.game_sprites.getSprite(0, 9))); //Add the enemies
        egc.enemies.add(new ShadowArcher(
                new Vector(64,32),
                new Vector(0, 0),
                ExplorerGameClient.game_sprites.getSprite(3, 8))); //Add the enemies
//        egc.enemies.add(new Enemy(32*3,32*5, 0, 0, egc.game_sprites.getSprite(0, 9)));
//        egc.enemies.add(new Enemy(32,32, 0, 0, egc.game_sprites.getSprite(1, 8)));


	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;

        if(egc.character == null)
            throw new IllegalStateException("character not initialized");

        var screen_offset = lib.to_screen(egc.character.getGamepos().scale(-1), egc.screen_center);
        egc.character.setPosition(egc.screen_center);
        egc.screenox = screen_offset.getX();
        egc.screenoy = screen_offset.getY();

        egc.grid.render(g,screen_offset, egc.character.getGamepos());



        // testing stuff
//        var v = lib.to_screen(egc.character.gamepos, new Vector(egc.screenox, egc.screenoy));
//        g.drawLine(0,0, v.getX(), v.getY());
//        draw game pos on screen
//        g.setColor(Color.blue);
//        g.drawRect(egc.character.getGamepos().getX(), egc.character.getGamepos().getY(), 32, 32);
//        g.drawRect(egc.grid.tiles[10][10].gamepos.getX(),egc.grid.tiles[10][10].gamepos.getY(), 32,32);
//        for (var e : egc.enemies){
//            g.drawRect(e.getGamepos().getX(), e.getGamepos().getY(),32,32);
//        }
//        g.drawLine(mp.getX(), mp.getY(), egc.screen_center.getX(),  egc.screen_center.getY());
//        g.setColor(Color.gray);
//        System.out.print(egc.character.gamepos + " ");
//        System.out.println(Math.floor(egc.character.gamepos.getX() / 32.0f));
//        var p = new Polygon();
//        var v = new Vector(0,0);
//        var v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(32,32);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(0,32);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(32,0);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        g.draw(p);
//        egc.grid.rooms.get(0).room_hitbox.render(g);
//        var r = egc.grid.rooms.get(0);
//        g.drawRect(r.x, r.y, r.width, r.height);
//        g.drawRect(egc.character.getGamepos().getX(), egc.character.getGamepos().getY(), 32,32);

        for (Enemy e : egc.enemies) {//Render all the enemies.
            e.setPosition(lib.to_screen(e.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
            e.render(g);
        }
        egc.character.render(g); //Render the character onto the screen.

        egc.projectiles.stream().forEach(p -> {
            p.setPosition(lib.to_screen(p.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
            p.render(g);
        });

        if(egc.is_connected){
            for (var c : egc.allies.values()){
                if (c.client_id != egc.ID){
                    c.setPosition(lib.to_screen(c.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
                    c.render(g);
                }
            }
        }
        //System.out.println(egc.character.gamepos);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
        Input input = container.getInput();
        ExplorerGameClient egc = (ExplorerGameClient) game;

        if(egc.character == null)
            throw new IllegalStateException("character not initialized");

        if(!egc.is_connected)
            egc.grid.MakePath(new ArrayList<Vector>( List.of(egc.character.getGamepos())));

        //(Kevin) deal with user input
        var inp = List.of( new Boolean[]{input.isKeyDown(Input.KEY_W), input.isKeyDown(Input.KEY_A), input.isKeyDown(Input.KEY_S), input.isKeyDown(Input.KEY_D)});
        var characterDir = lib.wasd_to_dir(inp);

        //Kevin, m is mouse cords on screen, character is always in the sceen center,
        //angleto gives the angle in degrees rotated by 180 for some reason,
        //divide by 45 to convert into 8 directions, then round to get the angle index,
        var mousePos = new Vector(input.getMouseX(), input.getMouseY());
        int diridx = (int)Math.round((mousePos.angleTo(egc.screen_center)+180)/45);

//        Kevin, commented out until its used for something
//        for(Enemy e : egc.enemies){
//            if(egc.character.collides(e)!= null){
//                System.out.println("character collided with an enemy");
//            }
//        }

        if(egc.is_connected){ //Kevin, run with a server
            var messages = new ArrayList<Message>();

            if(egc.character.getCurdir() != characterDir){
                messages.add(Message.builder(Message.MSG_TYPE.SET_DIR, egc.ID).setEtype(Message.ENTITY_TYPE.CHARACTER).setDir(characterDir));
                egc.character.setCurdir(characterDir);
            }


            if(input.isKeyPressed(Input.KEY_F))
                messages.add(Message.builder(Message.MSG_TYPE.PRIMARY, egc.ID));

            if(diridx != lastMouseIdx){
                messages.add(Message.builder(Message.MSG_TYPE.MOUSE_IDX, egc.ID).setIntData(diridx));
                lastMouseIdx = diridx;
            }

            messages.stream().forEach(m -> {
                try {
                    egc.out_stream.writeObject(m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            for(var m = egc.in_messages.poll(); m != null; m = egc.in_messages.poll()){
                egc.handle_message(m);
            }

        } else {
            //(Kevin) handle stuff when client isnt connected

            egc.character.lookingDirIdx = diridx;
            egc.character.setCurdir(characterDir);
            egc.character.update(delta); //Update the position of the player


            //Kevin, check collision with the 8 neighbor tiles of the character and undo their movement if there is a collision
            egc.grid.getNeighbors(egc.character.getGamepos()).stream()
                    .filter(t -> t.type == TileMap.TYPE.WALL || (t.type == TileMap.TYPE.DOOR && !((Door)t).is_open)) //hmmmmmmmmmmmmmm
                    .map(egc.character::collides) // stream of collisions that may be null
                    .filter(Objects::nonNull)
                    .findAny().ifPresent(c -> { // the actual collision object isnt useful, the minpentration doesnt work at all
                        egc.character.setVelocity(egc.character.getVelocity().scale(-1));
                        egc.character.update(delta);
                        egc.character.setVelocity(egc.character.getVelocity().scale(-1));
                    });

            if(input.isKeyPressed(Input.KEY_F))
                egc.character.primary().ifPresent(egc.projectiles::addAll);


            //(Kevin) update all other entities
            egc.projectiles.stream().forEach(p -> p.update(delta));

            //Kevin, make  an array of characters because thats what the server would give to the method
            egc.enemies.stream().map(e -> e.update(delta, new bounce.common.Character[] {egc.character},
                    //Kevin, may be cleaned up eventually
                    e.getClass() == ShadowArcher.class ? egc.grid.getranged_dir(e.getGamepos()) : egc.grid.get_dir(e.getGamepos())))

                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(egc.projectiles::addAll);

            //Kevin, check if projectiles collide with enemies
            for (Projectile p : egc.projectiles){
                //Kevin, if projectile isnt sent by archer dont hit enemies
                if(p.sender.getClass() != Archer.class)
                    continue;

                for (Enemy e : egc.enemies) {
                    if (p.collides(e) != null){
                        e.setHealth(e.getHealth() - p.damage);
                        p.setHit(true);
                        break; // each projectile should only collide with a single entity
                    }
                }
            }

//            egc.grid.update(new Character[]{egc.character});

            //Kevin, temp room open/close keys
            if(input.isKeyPressed(Input.KEY_O))
                egc.grid.rooms.forEach(Room::open);
            if(input.isKeyPressed(Input.KEY_P))
                egc.grid.rooms.forEach(Room::close);


            //(Kevin) remove dead/hit/etc stuff
            egc.enemies.removeIf(e -> e.getHealth() <=0);
            egc.projectiles.removeIf(Projectile::getHit);
        }
    }



    @Override
    public int getID() {
        return ExplorerGameClient.PLAYINGSTATE;
	}

}
