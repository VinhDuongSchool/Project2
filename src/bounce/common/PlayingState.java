package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.Vector;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


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
public class PlayingState extends BasicGameState {


	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
        ExplorerGameClient egc = (ExplorerGameClient)game;

        egc.enemies.add(new Enemy(0,32, 0, 0, egc.game_sprites.getSprite(0, 9))); //Add the enemies
        egc.enemies.add(new Enemy(32*3,32*5, 0, 0, egc.game_sprites.getSprite(0, 9)));
        egc.enemies.add(new Enemy(0,0, 0, 0, egc.game_sprites.getSprite(1, 8)));
	    container.setSoundOn(true);

	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;


        var pos = lib.to_screen(egc.character.gamepos, egc.screen_center);
        egc.screenox = pos.getX();
        egc.screenoy = pos.getY();

        var s1 = egc.game_sprites.getSprite(10, 4);
        var s2 = egc.game_sprites.getSprite(2, 4);
        var s3 = egc.game_sprites.getSprite(0, 2);


        // for sprites drawn directly from image they are drawn from the top left corner
        // for entities the image is drawn form their center
        // because the entity case will be mor common for testing these tiles subtract 32 from the render step
        Vector cords;
        for (float x = 0; x<10; x++){
            for (float y = 0; y<5; y++){
                cords =  lib.to_screen(x*32,y*32, new Vector(egc.screenox, egc.screenoy), -16);
                g.drawImage(s1,cords.getX()-32, cords.getY()-32);
            }
        }

        // testing stuff
//        g.drawLine(0,0, egc.screenox, egc.screenoy);
//        g.drawRect(egc.screenox, egc.screenoy, 64, 64);

        cords =  lib.to_screen(0,0, new Vector(0, 100 ));
        g.drawImage(s3, cords.getX(), cords.getY());

        cords =  lib.to_screen(0,32, new Vector(egc.screenox, egc.screenoy));
        g.drawImage(s2, cords.getX(), cords.getY());

        for (Enemy e : egc.enemies) {//Render all the enemies.
            e.setPosition(lib.to_screen(e.gamepos, new Vector(egc.screenox, egc.screenoy)));
            e.render(g);
        }


        egc.character.render(g); //Render the character onto the screen.
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
        Input input = container.getInput();
        ExplorerGameClient egc = (ExplorerGameClient) game;

        if (egc.is_connected){

        } else {
            // will need to change movement stuff to make it easier to do different sprites for different directions
            Vector v = new Vector(0,0);
            var UP_V = new Vector(0.2f,-0.2f);
            var LEFT_V = new Vector(-0.1f,-0.1f);
            if (input.isKeyDown(Input.KEY_W)){ //Move the player in the direction of the key pressed.
                v = v.add(UP_V);
            }
            if (input.isKeyDown(Input.KEY_A)){
                v = v.add( LEFT_V);
            }
            if (input.isKeyDown(Input.KEY_S)){
                v = v.add( UP_V.scale(-1));
            }
            if (input.isKeyDown(Input.KEY_D)){
               v = v.add( LEFT_V.scale(-1));
            }

            // invert velocity again (not sure why tbh)
            egc.character.setVelocity(v.scale(-1));
            egc.character.update(delta); //Update the position of the player

        }

	}

	@Override
	public int getID() {
		return ExplorerGameClient.PLAYINGSTATE;
	}

}
