Note: The BAGEL packages this project was built on was not developed by me.
Documentation for these packages can be found here: https://people.eng.unimelb.edu.au/mcmurtrye/bagel-doc/

Assignment for Object Oriented Software Design (SWEN20003), University of Melbourne, Semester 1 2020
Created June 2020
Relevant documentation below:

Shadow Defend is a tower defence game where the player must attempt to defend the map from enemies using towers.  
As such, there are two main components of the game:slicers(the enemy),and towers.
Towers are manually purchased by the player and can be placed on a non-blocked area of the map.
There are different types of towers, each with different characteristics, but the overall goal of a tower is to prevent slicers from exiting the map.
Slicers are the main antagonists of the game.  They spawn at varying points during a wave. Like towers, there are different types of slicers, 
each with different characteristics, but the overall goalof a slicer is to reach the ‘exit’ of the map.
When the game begins, the first level is loaded and rendered, the buy panel and status panels are rendered, and $500 starting cash is awarded to the player.
The player can set up their first defensive tower (if they want) and then when they’re ready, they can press ‘S’ to initiate the first wave of enemies.

A wave consists of a number of wave events.  The waves for every map is the same, and they are specified inside waves.txt:
Each line of the file specifies a wave event. There are two types of wave events:
Spawn Event: A spawn event comes in the form:<wave number>,spawn,<number to spawn>,<enemy type>,<spawn delay in milliseconds>
For example:1,spawn,20,slicer,1000 represents a spawn event for wave 1 that spawns 20 enemy slicers with a delay of 1000ms between each consecutive spawn.
In total, this wave eventshould  take 19000ms to complete (since the first slicer is spawned as soon as the wave event is started). 
The next wave event begins immediately after the previous wave event is completed.
Delay Event: A delay event comes in the form:<wave number>,delay,<delay in milliseconds>
For example:1,delay,1234 represents a delay event for wave 1 that simply waits (delays) for 1234ms before moving onto the next wave event.

Wave LogicA wave begins when the ‘S’ key is pressed.  The wave events are processed in order until there are no wave events left to process. 
When there are no events to process for the current wave, and there are no enemies left on the map, the wave is considered finished 
and the player is to be awarded $150 +wave×$100 where waveis the wave number that just finished.  
For example, if wave 6 just ended, the player would be granted $750.  If there is a wave in progress when the ‘S’ key is pressed- nothing should happen.
There will be no missing waves (i.e. a gap between two consecutive wave numbers). There is no limit on the number of waves that may be in a map. 
For a given wave, there may be zero or more spawn and delay events, but there will always be at least one of either.

There are two main panels in the game: the buy panel and the status panel.
The buy panel contain  the tower  available for purchase (purchase items), a list of key binds,and the money the player currently has. 
The background for the buy panel is provided for you in the images folder.  The first purchase item should be offset 64px horizontally (from the left of the window). 
There should be a 120px gap between the center of each purchase item, and the center of the purchase items should be drawn 10px above the center of the buy panel. 
The topleft coordinate of the money indicator should be rendered 200px to the left of the right edge of thewindow, and 65px from the top of the window. 
The buy panel will always be rendered at the top of the screen. The cost of the purchase item should be rendered below its image. 
If the player has enough funds to buy the purchase item, it should be rendered in green - otherwise in red.

When a panel item is left clicked (and the player has the funds to purchase it), 
a copy of the panel item image should be rendered at the user’s cursor so that they can have a visual indicator of where the tower is to be placed. 
If the tower cannot be placed where the user’s cursor is, then the visual indicator should not be rendered when hovering over that spot.
A tower cannot be placed on a coordinate if the center of the tower to place intersects with a panel, the bounding box of another tower, or with a blocked tile.  
Clicking again while a panel item is selected(and the cursor is over a valid place on the map) should create a new tower at the chosen location and deduct the cost of the tower from the player’s money.
Clicking while the cursor is over an invalid place on the map should not do anything, and it should leave the purchase item as selected.
Right clicking while a purchase item is selected should de-select the tower.

In the middle of the buy panel, a list of key binds should be rendered. This is just an informational feature that will help the player understand their capabilities.  

The status panel shows the state of the game at any given time. The state of the game can be described by:
the current wave (either the wave currently in progress or the wave we are waiting to start), the current time scale, current “status”, and the number of lives left.  
The player initially starts with 25 lives. The current status can be be one of:•Winner! •Placing •Wave In Progress •Awaiting Start
These statuses are provided in order of priority.  For example, if the player is placing a tower during a wave in progress, the status would be “placing”.

When the game is run, the map should be rendered to the screen.The map files for this project will be supplied in the TMX format. 
Bagel has rudimentary functionality to parse and render a tiled map, so you do not need to worry about the specifics regarding the map format.  
The map contains two main pieces of metadata:•Blocked tiles•Polylines
A polyline is a connected sequence of line segments that are described by a list of Points.  
The corners are not smooth curves, but a number of small line segments connected to give the impression of one.

The delays and movement rates in this project specification assume a timescale of 1. In our complete tower defense game, 
we often will want to speed things up so that we don’t have to sit through waves that will take a long time to complete.

When the ‘L’ key  is pressed,  the  timescale  should  increase  by  1  (if  possible).  
When the K key is pressed, the timescale should decrease by 1 (if possible). The timescale should not go below 1 or above 5.
If we have some delay in our game with a specified base value of 5 seconds and a movement rate of some object that is 1px/frame and the ‘L’ key is pressed,
the delay should be decreased to 2.5 seconds, and the movement rate of the object should be increased to 2px/frame.
If it is pressed again the delay should be decreased to 1.67 seconds and the movement rate increased to 3px/frame.
The effect of a change in the timescale should be reflected immediately within the game.

Slicers are the base enemy of the game. The goal of  the  slicer is simple: navigate through the enemy territory and get to the other end.  
When a slicer is spawned, it is spawned at the start ofthe polyline described by the current map. A slicer moves through the enemy territory by traversin gthe polyline.  
When a slicer successfully exits the map without being eliminated, the player loses lives equivalent to the penalty associated with the slicer. 
When the number of lives a player has reaches zero (or lower), the game should exit.

A slicer starts off with a specific amount ofhealth, aspeed, a rewardand a penalty.  
When the health of a slicer reaches 0 (or lower), the slicer is considered “eliminated” and should be removed from the game, and the value of its reward added to the player’s money.
Any extra damage inflicted upon a sliceris not carried over to its child slicers (if any).

A regular slicer moves at a rate of 2px/frame, has a health of 1 unit, a reward of $2, and a penalty of 1 life.

A super slicer moves at 3/4 the rate of a regular slicer, has the same health as a regular slicer, has a reward of $15, and has a penalty equivalent to the sum of its child slicers’ penalties.
When a super slicer is eliminated, it spawns two regular slicers at the location it was eliminated.

A mega slicer moves at the same rate as a super slicer, has double the health of a super slicer, has a reward of $10, and has a penalty equivalent to the sum of its child slicers’ penalties.
When a mega slicer is eliminated, it spawns two super slicers at the location it was eliminated.

An apex slicer moves at the half the rate of a mega slicer, has 25 times the health of a regular slicer, has a reward of $150, and a penalty equivalent to the sum of its child slicers’ penalties.
When an apex slicer is eliminated, it spawns four mega slicers at the location it was eliminated.

Towers allow the player to defend the map from enemies. There are two main types of towers, active towers and passive towers.
An active tower can deal damage to an enemy within its radius.It typically does this by facing the enemy and then launching a projectile at them.
When this projectile intersects with the enemy,damage is applied to the enemy through the reduction of the enemy’s health and the projectile is removed from the game. 
Each active tower has a projectile cooldown associated with it.  Once a projectile is fired, the tower enters a ‘cooldown’ period where it cannot attack any enemies. 
After the cooldown period has elapsed, the tower is allowed to launch another projectile.  
If there are multiple enemies within the tower’s area of effect, you are free to choose an enemy to target however you want.
Although we will specify the area of effect of active towers in terms of its radius (which implies a circular region), you are allowed to consider this areaa rectangular region to make things simpler.

A tank is an active tower that has an effect radius of 100px, does 1 unit of damage per projectile,and has  a  projectile cooldown of 1000ms.
A super tank is an active tower that has an effect radius of 150px, does three times as much damage as a regular tank, and has a projectile cooldown of 500ms.

The airplane is a passive tower. When placed, the airplane will spawn outside of the map and will fly at a  rate of 3px/frame either horizontally or vertically across the map,
dropping an explosive on the map at the plane’s current coordinate every 1 to 2 seconds.  The drop time should be chosen randomly from 1 to 2 seconds (inclusive).  
The drop time is chosen again after each drop. The explosive has an effect radius of 200px and detonates after 2 seconds of it being dropped. 
When an explosive detonates, it deals 500 damage to every enemy inits effect radius and is subsequently removed from the game.
The direction  (horizontally  or  vertically)  that  the  plane  will  travel  when  placed  will  alternate.
The first plane that is placed will travel horizontally, the second will travel vertically, the third will travel horzontally, etc.

If the airplane is to travel horizontally, it will be spawned outside the left edge of the window with the same y coordinate as where it was placed. 
The plane will then move straight to the right until it leaves the right edge of the window.
If the airplane is to travel vertically,it will be spawned outside the top edge of the window with the same x coordinate as where it was placed.  
The plane will then move straight downwards until it leaves the bottom edge of the window.

The airplane is only removed from the game once all its dropped explosives have detonated and it has left the window. 
The plane cannot drop any explosives when it is outside the map.

Every projectile starts at the center of the attacker, and moves at a constant speedof 10px/frame towards a target enemy.  
If the projectile is destined for a particular enemy, the enemy may be in motion — so the projectile should always move towards the updated location of the enemy.
A projectile ‘hits’ an enemy if the bounding box of the projectile intersects with the bounding box of the target.

The game should be able to support multiple levels. For the purposes of assessment, you can assume there will only be 2 levels in the game,
however you should design your solution in a way that can easily support increasing this limit.  You will be provided with two map files (1.tmx and 2.tmx).  
When a level is complete (i.e.  all waves have finished) - the game state is completely reset and the next level is loaded. 
This does not mean the game closes and reopens - the window must stay open during the reset of the game state and loading of the next level.  
When all levels are completed, the game must show the status ”Winner!”. 
The first level is specified by 1.tmx, second by 2.tmx, etc.The game begins on level 1.

You will be given a package res.zip, which contains all of the graphics and other files you need to build the game. 