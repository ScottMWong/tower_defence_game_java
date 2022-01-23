import bagel.*;
import bagel.map.TiledMap;
import bagel.util.Point;


import java.io.File;
import java.io.FileNotFoundException;

/**
 * Main class for the game.
 * <p>
 * Controls rendering for the entire window and flow of game logic. Contains a static instance of
 * WaveControl for holding entities (towers, slicers, and projectiles), as well as the panels.
 * <p>
 * Please note that the tower logic is set to target CLOSEST enemy, and as such the game is
 * quite hard with the values given in the specification. Recommendations for testing would be to
 * give yourself arbitrary amount of money and/or disable the restart level code.
 * <p>
 * Finally, two gameplay oddities of note.
 * <p>
 * Since the tower projectiles are allowed to do their damage and delete themselves
 * as soon as their center collides with their target, in higher timescales the
 * projectiles may be nearly impossible to see as they only appear for a few frames
 * due to the targeting algorithm prioritising closer enemies.
 * <p>
 * In addition, the specifications given state to spawn the first enemy of an event
 * as soon as the previous event has finished. This means that whenever two consecutive
 * events spawn the same enemy type, the last enemy of the first event and the first
 * enemy of the second event appear in exactly the same location. Testing has shown that
 * there are indeed two identical enemies stacked on top of each other, and they will
 * move to and be rendered in exactly the same locations until one is destroyed.
 */
public class ShadowDefend extends AbstractGame {
    //Defining variables
    //For window
    private String currentMapName;
    private File testExist;
    private TiledMap mapset;
    private static final int mapwidth = 32;
    private static final int mapheight = 24;
    private static final int tilelength = 32;
    private static final int windowheight = 768;
    private static final int windowwidth = 1024;
    //For gameplay
    private static int level;
    protected static WaveControl waveControl;
    private boolean waveActive;
    private static int lives;
    private static int money;
    private StatusPanel statusPanel;
    private BuyPanel buyPanel;
    //For timescale
    private static int timescale;
    private static final double msperframe = 1000/60;
    private ClickStatus clickStatus;
    private Status status;
    private Image tank;
    private Image supertank;
    private Image aircraft;
    private boolean gameover;

    /**
     * Method to start the game.
     * @param args  is inherited from the base Bagel function, as input arguements do not actually
     *             affect the game
     * @throws FileNotFoundException if any of the required files could not be found.
     */
    public static void main(String[] args) throws FileNotFoundException {
        // Create new instance of game and run it
        new ShadowDefend().run();
    }

    /**
     * Constructor for the game.
     * @throws FileNotFoundException if any of the required files could not be found.
     */
    public ShadowDefend() throws FileNotFoundException {
        level = 0;
        timescale = 1;
        mapset = new TiledMap("res/levels/1.tmx");
        waveControl = new WaveControl(mapset);
        buyPanel = new BuyPanel();
        statusPanel = new StatusPanel();
        levelUpdate();
        status = Status.Waiting;
        tank = new Image("res/images/tank.png");
        supertank = new Image("res/images/supertank.png");
        aircraft = new Image("res/images/airsupport.png");
        gameover = false;
    }

    private void levelUpdate(){
        lives = 25;
        money = 500;
        level = level + 1;
        currentMapName = "res/levels/" + level + ".tmx";
        testExist = new File(currentMapName);
        if (testExist.exists()) {
            mapset = new TiledMap(currentMapName);
            waveActive = false;
        }
        else {
            gameover = true;
        }
        waveControl.WaveControlReset(mapset);
        clickStatus = clickStatus.none;
        Aircraft.resetAircraftOrientation();
    }

    /**
     * Setter for losing lives when an enemy escapes.
     * @param livesLost  is the amount of lives the escaping enemy penalizes
     */
    protected static void penalty (int livesLost) {
        lives -= livesLost;
    }

    /**
     * Setter for gaining money when a enemy dies
     * @param moneyAdded  is the amount of money the dead enemy grants
     */
    protected static void reward (int moneyAdded) {
        money += moneyAdded;
    }

    private void restartmap() {
        waveControl.WaveControlReset(mapset);
        lives = 25;
        money = 500;
    }

    private void setStatus() {
        if (waveControl.levelFinished) {
            status = Status.Winner;
        }
        else if (clickStatus != ClickStatus.none) {
            status = Status.Placing;
        }
        else if (!waveControl.waveFinished) {
            status = Status.WaveStarted;
        }
        else {
            status = Status.Waiting;
        }
    }
    /**
     * Updates the game state approximately 60 times a second, potentially reading from input.
     * @param input  The input instance which provides access to keyboard/mouse state information.
     * <p>
     * Firstly, checks if the map needs to be restarted or changed.
     * Then checks key and mouse input responses such as starting waves, changing timescale,
     * selecting towers and placing towers.
     * Next, calls on the waveUpdate class to update the status of all entities in the game.
     * Finally, renders on the screen all entities, both panels, and, if appropriate,
     * a preview of tower placement.
     *
     */
    protected void update(Input input) {
        if (lives <= 0) {
            restartmap();
        }
        //This will trigger next level
        if (waveActive && waveControl.levelFinished && !gameover) {
            levelUpdate();
            return;
        }
        //Start wave
        if(input.wasPressed(Keys.S)) {
            if (waveControl.waveFinished) {
                waveControl.waveStart();
                waveControl.waveFinished = false;
            }
        }
        //Timescale up
        if(input.wasPressed(Keys.L)) {
            if (timescale < 5) {
                timescale += 1;
            }
        }
        //Timescale down
        if(input.wasPressed(Keys.K)) {
            if (timescale > 1) {
                timescale -= 1;
            }
        }
        //Mouse inputs
        Point mousepos = input.getMousePosition();
        if(input.wasPressed(MouseButtons.LEFT)) {
            ClickStatus BuyPanelStatus = buyPanel.BuyPanelClick(mousepos,money);
            if (BuyPanelStatus != ClickStatus.none) {
                clickStatus = BuyPanelStatus;
            }
            else if (!waveControl.checkTowerCursorCollisions(mousepos,mapset)){
                if (clickStatus == ClickStatus.tank && money >= 250) {
                    money -= 250;
                    ShadowDefend.waveControl.addTower(mousepos,clickStatus);
                    clickStatus = ClickStatus.none;
                }
                else if (clickStatus == ClickStatus.supertank && money >= 600) {
                    money -= 600;
                    ShadowDefend.waveControl.addTower(mousepos,clickStatus);
                    clickStatus = ClickStatus.none;
                }
                else if (clickStatus == ClickStatus.aircraft && money >= 500) {
                    money -= 500;
                    ShadowDefend.waveControl.addTower(mousepos,clickStatus);
                    clickStatus = ClickStatus.none;
                }
            }
        }

        if (input.wasPressed(MouseButtons.RIGHT)) {
            clickStatus = ClickStatus.none;
        }

        //Always draw map, panels, towers and enemies!
        setStatus();
        mapset.draw(0,0,0,0,windowwidth,windowheight);
        waveControl.waveUpdate(timescale);
        waveControl.renderEntities();
        buyPanel.BuyPanelRender(money);
        statusPanel.StatusPanelRender(waveControl.waveNumber, timescale, status, lives);
        //render preview of tower position
        if (!waveControl.checkTowerCursorCollisions(mousepos,mapset) && clickStatus != ClickStatus.none) {
            if (clickStatus == ClickStatus.tank) {
                tank.draw(mousepos.x, mousepos.y);
            }
            if (clickStatus == ClickStatus.supertank) {
                supertank.draw(mousepos.x, mousepos.y);
            }
            if (clickStatus == ClickStatus.aircraft) {
                aircraft.draw(mousepos.x, mousepos.y);
            }
        }
    }
}
