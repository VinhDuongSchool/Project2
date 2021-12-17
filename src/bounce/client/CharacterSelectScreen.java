package bounce.client;

import bounce.common.entities.Archer;
import bounce.common.entities.Mage;
import bounce.common.entities.Rogue;
import bounce.common.entities.Warrior;
import bounce.common.level.TileMap;
import bounce.common.lib;
import jig.Vector;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This is used to select a character using the 1,2,3,4 keys
 */
public class CharacterSelectScreen extends BasicGameState {

    @Override
    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) {
        ExplorerGameClient egc = (ExplorerGameClient)game;
        if(!egc.is_connected) {
            egc.grid = new TileMap(100, 100);
            egc.enemies.clear();
            egc.projectiles.clear();
            egc.items.clear();
        }

    }

    @Override
    public void render(GameContainer container, StateBasedGame game,
                       Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;
        g.drawString("welcome to Dungeon Explorer game Choose your class",200, 170);
        g.drawString("Press 1 to choose warrior", 200, 200);
        g.drawString("Press 2 to choose archer", 200, 230);
        g.drawString("Press 3 to choose rogue", 200, 260);
        g.drawString("Press 4 to choose mage", 200, 290);


    }

    @Override
    public void update(GameContainer container, StateBasedGame game,
                       int delta) throws SlickException {
        Input input = container.getInput();
        ExplorerGameClient egc = (ExplorerGameClient) game;

        var startpos = new Vector(32*5, 32*5);

        if (input.isKeyPressed(Input.KEY_1)) { //Choose the character by selecting the information that is already in the character and load the appropiate image.
            egc.setCharacter(Warrior.class, startpos, lib.v0);
            egc.enterState(ExplorerGameClient.PLAYINGSTATE);
        } else if (input.isKeyPressed(Input.KEY_2)) {
            egc.setCharacter(Archer.class, startpos, lib.v0);
            egc.enterState(ExplorerGameClient.PLAYINGSTATE);
        } else if (input.isKeyPressed(Input.KEY_3)) {
            egc.setCharacter(Rogue.class, startpos, lib.v0);
            egc.enterState(ExplorerGameClient.PLAYINGSTATE);
        }else if (input.isKeyPressed(Input.KEY_4)) {
            egc.setCharacter(Mage.class, startpos, lib.v0);
            egc.enterState(ExplorerGameClient.PLAYINGSTATE);
        }
    }



    @Override
    public int getID() {
        return ExplorerGameClient.CHARACTERSELECTSTATE;
    }

}
