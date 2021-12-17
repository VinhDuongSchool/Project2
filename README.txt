Controls: W,A,S,D controls to move the characters. Can hold more than 2 buttons to move diagonally. F or Left click mouse button to do primary attack

Cheat codes: (single player only)
Press K to kill the player manually without waiting for their health to get to zero.
Press L to revive player and give them their max health
Press O to clear the current room
Press P to close all rooms (doesnt reset spawners)
Press T to reload level (resets spawners) (untested and probably breaks stuff)

Low bar goals

Enemy Pathfinding - complete
    Ranged enemies path find different than melee enemies
Player movement - complete
Combat - partially complete
    Basic ranged and melee attack, characters only have a primary attack
Multiplayer - complete
Death system - partially complete
    characters will die when they run out of health. they can revive them selves after n seconds.
Enemies - partially complete
    only ranged and melee enemies
Status effects - not complete
Victory condition - not complete
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

https://opengameart.org/content/64x64-isometric-roguelike-tiles
 "Part of (or All) the graphic tiles used in this program is the public domain roguelike tileset 'RLTiles'.
