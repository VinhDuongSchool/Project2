Controls: W,A,S,D controls to move the characters. Can hold more than 2 buttons to move diagonally. F or Left click mouse button to do primary attack

using controller: left joycon uses the dpads as movement and right joycon uses a for attack. the last way you walked will be the attack direction
setting up controllers: this game is setup as using two joycons and you must configure them differently depending on your machines connected inputs for me, I
had 4 inputs from my computers keyboard and other inputs. the controller input is changed by selecting a different index at lines 189 - 191. also to make it to actually use the joycons as input you have to put true for the controller input in the startup arguments
ex (false/true) true (false/true is for if it's using a server or not).


Cheat codes:
Press 1 to go to gameover state
Press 2 to go to win state
(single player only)
Press K to kill the player manually without waiting for their health to get to zero.
Press L to revive player and give them their max health
Press O to clear and complete the current room
Press P to close all rooms (doesnt reset spawners)
Press T to reload level (resets spawners) (untested and probably breaks stuff)

you can rename room files to remove rooms. if you rename room2 > room10 the game will only load the first room

Low bar goals

Enemy Pathfinding - complete
    Ranged enemies path find different than melee enemies
Player movement - complete
Combat - partially complete
    Basic ranged and melee attack, characters only have a primary attack
Multiplayer - complete
Death system - partially complete
    characters will die when they run out of health and go to a defeated screen.
    They can then go back to the character select screen and restart (in single player the map resets on the server it doesnt).

Enemies - partially complete
    only ranged and melee enemies
Status effects - not complete
Victory condition - complete
    When all rooms are completed you go to a victory screen
Point/Gold system - partially complete


Other goals completed:
Room system
    Rooms are loaded from a room file, format is described in Tilemap.addRoom()
    Room features (all described in the file):
        room dimensions as x,y,w,h
        wall obstacles placed in the middle of rooms
        enemy spawners
    until a room is cleared of enemies its doors are closed. when the room is cleared its doors open
    a room will not close until all players enter the room
items:
    Potions to heal
    Gold piles
Multiple character select


Licensing terms:
Creative Commons

https://opengameart.org/content/spearman-bleeds-game-art //free to share and adapt
https://opengameart.org/content/64x64-isometric-roguelike-tiles
 "Part of (or All) the graphic tiles used in this program is the public domain roguelike tileset 'RLTiles'.
