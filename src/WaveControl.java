import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * The controller for wave events, and the spawning and maintenance of all entities.
 * <p>
 * Contains ArrayList members to store towers, projectiles, slicers,
 * wave events and map points, as well as the methods to interact meaningfully
 * with these lists.
 */
public class WaveControl {
    //Wave variables
    protected boolean waveFinished;
    protected boolean levelFinished;
    protected int waveNumber;
    //For Towers
    protected ArrayList<Tower> towerList;
    protected ArrayList<Aircraft> aircraftList;
    private static Rectangle playfield = new Rectangle (0,100, 1024,643);
    //For projectiles
    protected ArrayList<Projectile> projectileList;
    //For Slicers
    protected ArrayList<Point> pointList;
    protected ArrayList<Slicer> slicerList;
    //For Spawning Slicers
    private int waveOfNextEvent;
    private String typeOfNextEvent;
    private int countToSpawn;
    private int countSpawned;
    private int spawnCooldownms;
    private ArrayList<String> spawnEvents;
    private File eventFile;
    private String tempEventParser;

    private int EventNum;
    private static int msElasped;
    private static final double msperframe = 1000/60;
    private Scanner eventReader;
    private SpawnType typeSpawning;
    private int spawnIDassignment;
    enum SpawnType {
        slicer,
        superslicer,
        megaslicer,
        apexslicer,
        none
    }

    /**
     * The constructor for the WaveControl class.
     * @param mapset  is the map to load the route for
     * @throws FileNotFoundException if any of the required files cannot be found.
     */
    protected WaveControl(TiledMap mapset) throws FileNotFoundException {
        spawnEvents = new ArrayList<String>();
        takeWaveEvents();
        WaveControlReset(mapset);
    }

    /**
     * Resets the WaveControl instance.
     * Used to allow the use of the same WaveControl instance for multiple levels
     * or plays of the same level.
     * @param mapset  is the map to load the route for
     */
    protected void WaveControlReset(TiledMap mapset) {
        waveOfNextEvent = 0;
        msElasped = 0;
        EventNum = 0;
        countToSpawn = 0;
        countSpawned = 0;
        spawnCooldownms = 0;
        waveNumber = 0;
        spawnIDassignment = 0;
        waveFinished = true;
        pointList = new ArrayList<Point>();
        slicerList = new ArrayList<Slicer>();
        projectileList = new ArrayList<Projectile>();
        towerList = new ArrayList<Tower>();
        aircraftList = new ArrayList<Aircraft>();
        for (Point point : mapset.getAllPolylines().get(0)) {
            pointList.add(point);
        }
        newWaveEvent();
    }
    //When loading game take events once
    private void takeWaveEvents() throws FileNotFoundException {
        eventFile = new File ("res/levels/waves.txt");
        eventReader = new Scanner(eventFile);
        while (eventReader.hasNextLine()) {
            spawnEvents.add(eventReader.nextLine());
        }
    }

    /**
     * Starts the next wave when called.
     * Increments the wave number, and if the first wave event is a spawn,
     * spawns the first enemy.
     */
    protected void waveStart() {
        waveNumber += 1;
        waveFinished = false;
        if (typeOfNextEvent.equals("spawn")) {
            spawnControl();
        }
    }

    /**
     * Updates the state of all the entities.
     * Firstly, calls the update method for each entity in turn.
     * Then this method searches the lists of entities and deletes as appropriate.
     * Finally, if the current wave is not finished, the current wave event is progressed.
     * @param timescale  is the current timescale, given by the ShadowDefend instance
     */
    protected void waveUpdate(int timescale) {
        for (Slicer slicer: slicerList) {
            slicer.updatePos(timescale);
        }
        for (Projectile projectile : projectileList){
            projectile.ProjectileUpdate(timescale);
        }
        for (Tower tower: towerList) {
            tower.towerUpdate(timescale);
        }
        for (Aircraft aircraft: aircraftList) {
            aircraft.towerUpdate(timescale);
        }
        projectileDeleteSearch();
        aircraftDeleteSearch();
        slicerDeleteSearch();

        if (!waveFinished) {
            msElasped += timescale * msperframe;
            if (msElasped >= spawnCooldownms) {
                if (countToSpawn != 1) {
                    spawnControl();
                }
                msElasped -= spawnCooldownms;
                if (countSpawned >= countToSpawn) {
                    if (EventNum == spawnEvents.size()) {
                        levelFinished = true;
                        waveFinished = true;
                    }
                    else {
                        newWaveEvent();
                        if (waveOfNextEvent > waveNumber) {
                            waveFinished = true;
                        }
                        else if (typeOfNextEvent.equals("spawn")) {
                            spawnControl();
                        }
                    }
                }
            }
        }
    }

    private void newWaveEvent () {
        tempEventParser = spawnEvents.get(EventNum);
        EventNum += 1;
        countSpawned = 0;
        waveOfNextEvent = parseInt(tempEventParser.substring(0,tempEventParser.indexOf(',')));
        tempEventParser = tempEventParser.substring(tempEventParser.indexOf(',') + 1);
        typeOfNextEvent = tempEventParser.substring(0, tempEventParser.indexOf(','));
        tempEventParser = tempEventParser.substring(tempEventParser.indexOf(',') + 1);
        if (typeOfNextEvent.equals("spawn")) {
            countToSpawn = parseInt(tempEventParser.substring(0,tempEventParser.indexOf(',')));
            tempEventParser = tempEventParser.substring(tempEventParser.indexOf(',') + 1);
            typeSpawning = SpawnType.valueOf(tempEventParser.substring(0, tempEventParser.indexOf(',')));
            tempEventParser = tempEventParser.substring(tempEventParser.indexOf(',') + 1);
            spawnCooldownms = parseInt(tempEventParser);
        }
        else {
            typeSpawning = SpawnType.none;
            spawnCooldownms = parseInt(tempEventParser);
        }
    }

    private void spawnControl () {
        countSpawned += 1;
        switch (typeSpawning) {
            case slicer:
                slicerList.add(new RegularSlicer(spawnIDassignment, pointList));
                spawnIDassignment += 1;
                break;
            case superslicer:
                slicerList.add(new SuperSlicer(spawnIDassignment, pointList));
                spawnIDassignment += 1;
                break;
            case megaslicer:
                slicerList.add(new MegaSlicer(spawnIDassignment, pointList));
                spawnIDassignment += 1;
                break;
            case apexslicer:
                slicerList.add(new ApexSlicer(spawnIDassignment, pointList));
                spawnIDassignment += 1;
                break;
        }
    }

    /**
     * Renders all entities on screen.
     * Render the slicers and towers before projectiles.
     * This allows projectiles to always be visible, even when overlapping other entities.
     */
    protected void renderEntities() {
        slicerRender();
        towerRender();
        projectileRender();

    }

    private void slicerRender() {
        for (Slicer slicer : slicerList) {
            slicer.renderSlicer();
        }
    }

    private void projectileRender() {
        for (Projectile projectile : projectileList) {
            projectile.ProjectileRender();
        }
    }

    private void towerRender() {
        for (Tower tower: towerList) {
            tower.towerRender();
        }
        for (Aircraft aircraft: aircraftList) {
            aircraft.towerRender();
        }
    }

    /**
     * Adds four Mega Slicers to the game when a Apex Slicer dies.
     * @param nextpointnum  is the number of the point the Apex Slicer was trying to reach
     * @param x  is the x position of Apex Slicer on death
     * @param y  is the y position of Apex Slicer on death
     */
    protected void ApexSlicerDeath(int nextpointnum, double x, double y) {
        slicerList.add(new MegaSlicer(spawnIDassignment, pointList, nextpointnum, x, y));
        spawnIDassignment += 1;
        slicerList.add(new MegaSlicer(spawnIDassignment, pointList, nextpointnum, x, y));
        spawnIDassignment += 1;
        slicerList.add(new MegaSlicer(spawnIDassignment, pointList, nextpointnum, x, y));
        spawnIDassignment += 1;
        slicerList.add(new MegaSlicer(spawnIDassignment, pointList, nextpointnum, x, y));
        spawnIDassignment += 1;

    }
    /**
     * Adds two Super Slicers to the game when a Mega Slicer dies.
     * @param nextpointnum  is the number of the point the Mega Slicer was trying to reach
     * @param x  is the x position of Mega Slicer on death
     * @param y  is the y position of Mega Slicer on death
     */
    protected void MegaSlicerDeath(int nextpointnum, double x, double y) {
        slicerList.add(new SuperSlicer(spawnIDassignment, pointList, nextpointnum, x, y));
        spawnIDassignment += 1;
        slicerList.add(new SuperSlicer(spawnIDassignment, pointList, nextpointnum, x, y));
        spawnIDassignment += 1;
    }
    /**
     * Adds two Regular Slicers to the game when a Super Slicer dies.
     * @param nextpointnum  is the number of the point the Super Slicer was trying to reach
     * @param x  is the x position of Super Slicer on death
     * @param y  is the y position of Super Slicer on death
     */
    protected void SuperSlicerDeath(int nextpointnum, double x, double y) {
        slicerList.add(new RegularSlicer(spawnIDassignment, pointList, nextpointnum, x, y));
        spawnIDassignment += 1;
        slicerList.add(new RegularSlicer(spawnIDassignment, pointList, nextpointnum, x, y));
        spawnIDassignment += 1;
    }

    /**
     * Finds where the slicer with the given ID is located.
     * @param slicerID  is the generated ID of the slicer to find
     * @return returns where the slicer is located, or null if it is not in the list.
     */
    protected Point findSlicerPoint(int slicerID) {
        for (Slicer slicer : slicerList) {
            if (slicer.getSpawnID() == slicerID) {
                return slicer.returnPoint();
            }
        }
        return null;
    }

    /**
     * Deals damage to the slicer with the given ID.
     * @param damage  is the amount of damage to deal to the slicer
     * @param targetID  is the generated ID of the slicer to find
     */
    protected void ProjectileDamage(int damage, int targetID) {
        for (Slicer slicer : slicerList) {
            if (slicer.getSpawnID() == targetID) {
                slicer.takeDamage(damage);
                break;
            }
        }
    }
    /**
     * Deals damage to each slicer inside the given rectangle.
     * @param damage  is the amount of damage to deal to each slicer
     * @param AoE  is the rectangle to search.
     */
    protected void ExplosiveDamage(int damage, Rectangle AoE) {
        ArrayList<Integer> targetlist = new ArrayList<Integer>();
        for (Slicer slicer : slicerList) {
            if (AoE.intersects(slicer.returnPoint())) {
                targetlist.add(slicer.getSpawnID());
            }
        }
        while (targetlist.size() > 0) {
            int targetID = targetlist.get(0);
            for (Slicer slicer : slicerList) {
                if (slicer.getSpawnID() == targetID) {
                    slicer.takeDamage(damage);
                    targetlist.remove(0);
                    break;
                }
            }
        }
    }

    private void projectileDeleteSearch() {
        projectileList.removeIf(projectile -> projectile.getDelete());
    }

    private void aircraftDeleteSearch() {
        aircraftList.removeIf(aircraft-> aircraft.getDelete());
    }

    private void slicerDeleteSearch() {
        slicerList.removeIf(slicer-> slicer.getDelete());
    }
    /**
     * Finds the closest slicer to a point.
     * This is the targeting function for Tanks and Super Tanks.
     * @param range  is the range of the targeting tower
     * @param point  is is the position of the targeting tower
     * @return returns the generated ID of the closest slicer, or -1 if no slicer is in range.
     */
    protected int findTargetID(int range, Point point) {
        int TargetID = -1;
        double TargetRange = range;
        Point possiblepoint;
        for (Slicer slicer : slicerList) {
            possiblepoint = slicer.returnPoint();
            Vector2 distance = new Vector2(possiblepoint.x - point.x, possiblepoint.y - point.y);
            if (distance.length() < TargetRange) {
                TargetID = slicer.getSpawnID();

            }TargetRange = distance.length();
        }
        return TargetID;
    }

    /**
     * Checks if the point given is a valid placement for a tower
     * @param point  is the point to check placement for
     * @param mapset  is the current map
     * @return returns false is the given point is a valid tower placement, and true otherwise
     */
    protected boolean checkTowerCursorCollisions(Point point, TiledMap mapset) {
        for (Tower tower: towerList) {
            if (tower.towerCollision(point)) {
                return true;
            }
        }
        if (!playfield.intersects(point)) {
            return true;
        }
        if (mapset.getPropertyBoolean((int) point.x, (int) point.y, "blocked", false)) {
            return true;
        }
        return false;
    }

    /**
     * Adds a tower to the list of towers.
     * @param point  is the point at which to add the tower
     * @param clickStatus  is the type of tower to add
     */
    protected void addTower(Point point, ClickStatus clickStatus) {
        if (clickStatus == ClickStatus.tank) {
            towerList.add(new Tank(point));
        }
        else if (clickStatus == ClickStatus.supertank) {
            towerList.add(new SuperTank(point));
        }
        else if (clickStatus == ClickStatus.aircraft) {
            aircraftList.add(new Aircraft(point));
        }
    }
}
