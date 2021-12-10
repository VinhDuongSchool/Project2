package bounce.common;

import jig.ConvexPolygon;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TileMap {
    public final Tile[][] tiles;

    private int[][] S; //2d arrays used by the dijkstra algorithm.
    private int[][] d;
    private int[][] pi;

    private int[][] costs;
    private Vector[][] DirToNext;


    public enum TYPE{
        FLOOR,
        WALL,
        DOOR
    }

    public ArrayList<Room> rooms;

    private final int maxx;
    private final int maxy;

    PriorityQueue<int[]> Q = new PriorityQueue<>(100, (a,b) -> Integer.compare(a[2],b[2])); //Priority queue for the dijkstra algorithm.

    public TileMap(int tilesx, int tilesy, SpriteSheet ss) {
        maxx = tilesx;
        maxy = tilesy;

        rooms = new ArrayList<>();
        tiles = new Tile[tilesx][tilesy];
        costs = new int[tilesx][tilesy];
        DirToNext = new Vector[tilesx][tilesy];

        for (int x = 0; x < tiles.length; x++){
            for (int y = 0; y < tiles[0].length; y++){
                Image i;
                String tileType;
                TYPE t;
                if(y == 0 || x == 0){
                    i = ss.getSprite(0,2);
                    t = TYPE.WALL;
                } else {
                    i = ss.getSprite(10, 4);
                    t= TYPE.FLOOR;
                }
                tiles[x][y] = new Tile(x*32,y*32, new Vector(x*32, y*32),i);
                tiles[x][y].type = t;
            }
        }
        //(Kevin) test tile
//        t = new Tile(0,0, new Vector(11*32, 10*32), ss.getSprite(0,2), "WALL");
//        t.addShape(new ConvexPolygon(new float[]{16,-16,16,16,-16,16,-16,-16}));

        S = new int[tiles.length][tiles[0].length];
        d = new int[tiles.length][tiles[0].length];
        pi = new int[tiles.length][tiles[0].length];

        addRoom("room1", ss);

        int gpx = 6;
        int gpy = 6;
        var t = new Tile(320,320, new Vector(10*32, 10*32), ss.getSprite(0,2));
//        t.addShape(new ConvexPolygon(new float[]{16,-16,16,16,-16,16,-16,-16}));
        t.type = TYPE.WALL;
        tiles[10][10] = t;

        t.addShape(new ConvexPolygon(lib.sqr.getPoints()), Color.transparent, Color.blue);

    }

    public void update(Character[] carr){

        var r = rooms.get(0);

//        System.out.println(r.room_hitbox.collides(carr[0]));
        boolean all_in_room = Arrays.stream(carr).map(r.room_hitbox::collides).allMatch(Objects::nonNull);
        if(all_in_room){
            r.close();
        }
    }

    public Tile getTile(Vector gamexy){
        int x = (int)Math.floor( gamexy.getX()/32.0f);
        int y = (int)Math.floor( gamexy.getY()/32.0f);
        return tiles[x][y];
    }

    public Tile getTile(float gamex, float gamey){
        int x = (int)Math.floor(gamex/32.0f);
        int y = (int)Math.floor(gamey/32.0f);
        return tiles[x][y];
    }

    public Vector getdir(Vector gamexy){
        int x = (int)Math.floor( gamexy.getX()/32.0f);
        int y = (int)Math.floor( gamexy.getY()/32.0f);
        return DirToNext[x][y];
    }

    public void render(Graphics g, Vector screen_offset, Vector character_pos){

        //(Kevin) render a square around the player, proper render order is enforced in the for loops
        int midx = (int)Math.floor(character_pos.getX()/32.0f);
        int midy = (int)Math.floor(character_pos.getY()/32.0f);
        int dist = 18;
        for (int y = Math.max(0, midy - dist); y < Math.min( tiles[0].length, midy + dist); y++){
            for (int x = Math.min( tiles.length-1, midx +dist); x >= Math.max(0, midx-dist);  x--){
                Tile t = tiles[x][y];
                t.setPosition(lib.to_screen(t.gamepos, screen_offset, -16));
                t.render(g);
                t.setPosition(t.gamepos);
            }
        }

    }


    public void MakePath(ArrayList<Vector> goals){
        for(var arr : costs){
            Arrays.fill(arr, Integer.MAX_VALUE - 100000);
        }
        PriorityQueue<int[]> tocheck = new PriorityQueue<>(maxx, Comparator.comparingInt(v -> costs[v[0]][v[1]]));
        //(Kevin) assuming goals are some game position
        goals.stream()
                .filter(g -> // filter positions outside of the map (should be turned into an assertion later)
                        (0 <= g.getX() && g.getX() < maxx*32) &&
                        (0 <= g.getY() && g.getY() < maxy*32))
                .forEach(g -> { // initialize goal tiles to 0 and add them to queue
                        int x = (int)Math.floor(g.getX()/32.0f);
                        int y = (int)Math.floor(g.getY()/32.0f);
                        costs[x][y] = 0;
                        tocheck.add(new int[]{x,y});
                });

        //Kevin, set refernce for character tiles to themselves so when enemy reaches goal they dont error
        tocheck.stream().forEach(t -> tiles[t[0]][t[1]].next = tiles[t[0]][t[1]]);
        while (!tocheck.isEmpty()){
            var cur = tocheck.poll();
            var curt = tiles[cur[0]][cur[1]];
            var curcost = costs[cur[0]][cur[1]];
            getNeighbors(cur)
                    .stream()
                    .forEach(t -> {
                        // (Kevin) ignore walls and set cost to +1
//                        int cost = (tiles[t[0]][t[1]].type == TYPE.WALL) ? Integer.MAX_VALUE-10 : costs[t[0]][t[1]];
                        int cost = costs[t[0]][t[1]];
                        if (tiles[t[0]][t[1]].type != TYPE.WALL && curcost + 1 < cost){
                            costs[t[0]][t[1]] = curcost + 1;
                            DirToNext[t[0]][t[1]] = new Vector(cur[0] - t[0], cur[1] - t[1]);
                            tiles[t[0]][t[1]].next = curt;
                            tocheck.add(t);
                        }
                    });
        }
    }

    public ArrayList<int[]> getNeighbors(int[] t) {
        var neighbors = new ArrayList<int[]>();
        //(Kevin) go in all 8 dirs
        boolean wall_neighbor = false;
        outer:
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int gpx = t[0] + x;
                int gpy = t[1] + y;

                //(Kevin) make sure neighbor exists and is not a self reference
                if(
                    (0 <= gpy && gpy < maxy ) &&
                    (0 <= gpx && gpx < maxx) &&
                    !(x == 0 && y == 0)
                ){
                    //Kevin, would love to use a goto to do it all with 1 set of loops but no goto :/
                    //Kevin, check if wall is an edge
                    if ((x == 0) != (y == 0) && tiles[gpx][gpy].type == TYPE.WALL){
                        wall_neighbor = true;
                        break outer;
                    }
                    neighbors.add(new int[]{gpx, gpy});
                }
            }
        }
        if(!wall_neighbor){
            return neighbors;
        }

        //Kevin, if wall in one of 4 edges dont add corners to neighbors because diagnal movement would collide with wall
        neighbors.clear();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if ((x == 0) == (y == 0) ){
                    continue;
                }
                int gpx = t[0] + x;
                int gpy = t[1] + y;

                //(Kevin) make sure neighbor exists and is not a self reference
                if(
                    (0 <= gpy && gpy < maxy) &&
                    (0 <= gpx && gpx < maxx)
                ){
                    neighbors.add(new int[]{gpx, gpy});
                }
            }
        }
        return neighbors;
    }


    public ArrayList<Tile> getNeighbors(Vector agamepos) {
        int[] t = new int[]{
                (int)Math.floor( agamepos.getX()/32.0f),
                (int)Math.floor( agamepos.getY()/32.0f)};
        var neighbors = new ArrayList<Tile>();
        //(Kevin) go in all 8 dirs
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int gpx = t[0] + x;
                int gpy = t[1] + y;

                //(Kevin) make sure neighbor exists, needs self reference for proper collsion (x,y) == (0,0)
                if(
                    (0 <= gpy && gpy < maxy ) &&
                    (0 <= gpx && gpx < maxx)
                ){
                    neighbors.add(tiles[gpx][gpy]);
                }
            }
        }
        return neighbors;
    }

    public void DijkstraAlgorithm(int[] startingPoint) { //Logic for the
        initializeSingleSource(startingPoint);
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++){
                S[x][y] = 0;
            }
        }

        int[] temp = { startingPoint[0], startingPoint[1], 0};
        Q.add(temp);
        int[] u;

        while (Q.size() != 0) {
            u = Q.remove();
            if (S[u[0]][u[1]] != 1) { // Has not been seen
                S[u[0]][u[1]] = 1; //Means this has been seen
                if (u[0] == 0 && u[1] == 0) { //If the top left corner
                    int[] v = new int[2];
                    if (tiles[u[0] + 1][u[1]].type == TYPE.FLOOR) { //If to the right is a floor tile.
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                    if (tiles[u[0]][u[1] + 1].type == TYPE.FLOOR) { //If to the down is a floor tile
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                    if (tiles[u[0] + 1][u[1] + 1].type == TYPE.FLOOR) { //If to the down right
                        v[0] = u[0] + 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "RD");
                    }

                } else if (u[0] == tiles.length - 1 && u[1] == 0) { //If the top right corner
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].type == TYPE.FLOOR) { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0]][u[1] + 1].type == TYPE.FLOOR) { //If to the down is a floor tile
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                    if (tiles[u[0] - 1][u[1] + 1].type == TYPE.FLOOR) { //If to the down left
                        v[0] = u[0] - 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "LD");
                    }
                } else if (u[0] == 0 && u[1] == tiles[0].length - 1) { //If at bottom left
                    int[] v = new int[2];
                    if (tiles[u[0] + 1][u[1]].type == TYPE.FLOOR) { //If to the right is a floor tile.
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                    if (tiles[u[0]][u[1] - 1].type == TYPE.FLOOR) { //If to the up is a floor tile
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] + 1][u[1] - 1].type == TYPE.FLOOR) { //If to the up right
                        v[0] = u[0] + 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "RU");
                    }
                } else if (u[0] == tiles.length - 1 && u[1] == tiles[0].length - 1) { // If at bottom right
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].type == TYPE.FLOOR) { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0]][u[1] - 1].type == TYPE.FLOOR) { //If to the up is a floor tile
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] - 1][u[1] - 1].type == TYPE.FLOOR) { //If to the up left
                        v[0] = u[0] - 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "LU");
                    }
                } else if (u[0] == 0) { //Left of the maze
                    int[] v = new int[2];
                    if (tiles[u[0]][u[1] - 1].type == TYPE.FLOOR) { //If to the up is a floor tile.
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] + 1][u[1] - 1].type == TYPE.FLOOR) { //If to the up right is a floor tile
                        v[0] = u[0] + 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "RU");
                    }
                    if (tiles[u[0] + 1][u[1]].type == TYPE.FLOOR) { //If to the right
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                    if (tiles[u[0] + 1][u[1] + 1].type == TYPE.FLOOR) { //If to the down right
                        v[0] = u[0] + 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "RD");
                    }
                    if (tiles[u[0]][u[1] + 1].type == TYPE.FLOOR) { //If to the down
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                } else if (u[0] == tiles.length - 1) { //If on the right.
                    int[] v = new int[2];
                    if (tiles[u[0]][u[1] - 1].type == TYPE.FLOOR) { //If to the up is a floor tile.
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] - 1][u[1] - 1].type == TYPE.FLOOR) { //If to the up left is a floor tile
                        v[0] = u[0] - 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "LU");
                    }
                    if (tiles[u[0] - 1][u[1]].type == TYPE.FLOOR) { //If to the left
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0] - 1][u[1] + 1].type == TYPE.FLOOR) { //If to the down left
                        v[0] = u[0] - 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "LD");
                    }
                    if (tiles[u[0]][u[1] + 1].type == TYPE.FLOOR) { //If to the down
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                } else if (u[1] == 0) { //If on the top of the maze
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].type == TYPE.FLOOR) { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0] - 1][u[1] + 1].type == TYPE.FLOOR) { //If to the up left down
                        v[0] = u[0] - 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "LD");
                    }
                    if (tiles[u[0]][u[1] + 1].type == TYPE.FLOOR) { //If to the Down
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                    if (tiles[u[0] + 1][u[1] + 1].type == TYPE.FLOOR) { //If to the down right
                        v[0] = u[0] + 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "RD");
                    }
                    if (tiles[u[0] + 1][u[1]].type == TYPE.FLOOR) { //If to the right
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                } else if (u[1] == tiles[0].length - 1) { //If on the bottom
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].type == TYPE.FLOOR) { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0] - 1][u[1] - 1].type == TYPE.FLOOR) { //If to the up left up
                        v[0] = u[0] - 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "LU");
                    }
                    if (tiles[u[0]][u[1] - 1].type == TYPE.FLOOR) { //If to the up
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] + 1][u[1] - 1].type == TYPE.FLOOR) { //If to the down up
                        v[0] = u[0] + 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "RU");
                    }
                    if (tiles[u[0] + 1][u[1]].type == TYPE.FLOOR) { //If to the right
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                } else { //Else any other coordinates
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].type == TYPE.FLOOR) { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0] - 1][u[1] - 1].type == TYPE.FLOOR) { //If to the up left up
                        v[0] = u[0] - 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "LU");
                    }
                    if (tiles[u[0]][u[1] - 1].type == TYPE.FLOOR) { //If to the up
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] + 1][u[1] - 1].type == TYPE.FLOOR) { //If to the down up
                        v[0] = u[0] + 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "RU");
                    }
                    if (tiles[u[0] + 1][u[1]].type == TYPE.FLOOR) { //If to the right
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                    if (tiles[u[0] + 1][u[1] + 1].type == TYPE.FLOOR) { //If to the right down
                        v[0] = u[0] + 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "RD");
                    }
                    if (tiles[u[0]][u[1] + 1].type == TYPE.FLOOR) { //If to the down
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                    if (tiles[u[0] - 1][u[1] + 1].type == TYPE.FLOOR) { //If to the down left
                        v[0] = u[0] - 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "LD");
                    }
                }
            }
        }
    }
    public void initializeSingleSource(int[] startingPoint) { //Initialize the values for the Dijkstra algorithm.
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                d[x][y] = 100000;
                pi[x][y] = -1;
            }
        }
        d[startingPoint[0]][startingPoint[1]] = 0;
    }

    public void relax(int[] u, int[] v, String direction) { //Relax methods
        if (d[v[0]][v[1]] > d[u[0]][u[1]] + 1) { //If the new coordinate d value is more than the old d coordinate + 1 (the weight)
            d[v[0]][v[1]] = d[u[0]][u[1]] + 1; //set new d value
            if (direction == "U") { //Assign value to pi based off the direction
                pi[v[0]][v[1]] = 1;
            } else if (direction.equals("RU")) {
                pi[v[0]][v[1]] = 2;
            } else if (direction.equals("R")) {
                pi[v[0]][v[1]] = 3;
            } else if (direction.equals("RD")) {
                pi[v[0]][v[1]] = 4;
            } else if (direction.equals("D")) {
                pi[v[0]][v[1]] = 5;
            } else if (direction.equals("LD")) {
                pi[v[0]][v[1]] = 6;
            } else if (direction.equals("L")) {
                pi[v[0]][v[1]] = 7;
            } else if (direction.equals("LU")) {
                pi[v[0]][v[1]] = 8;
            } else {
                System.out.println("Error occurred in Dijkstra");
            }
            int[] temp = new int[3]; //Make a new array to add to the priority Queue.
            temp[0] = v[0];
            temp[1] = v[1];
            temp[2] = d[u[0]][u[1]] + 1;
            Q.add(temp);
        }
    }

    private void addRoom(String room, SpriteSheet ss){

        //Kevin, add proper path to levels
        room = System.getProperty("user.dir") + "\\Project2\\src\\bounce\\resource\\"+room;


        /*
        Kevin, parsing a level file:

        first line = room definition in rectangle x y w h form
        second line = door positions, they are defined W S E N, -1 means that side does not have a door
        all other lines define inner stuff:
        first number: type 1 = wall, 99 = temp door case
        second: number of tiles to insert
        third: direction to insert tiles, y = 1, x = 0
        fourth: start tile, it must be within the walls of the room
         */

        try {
            var ft = new BufferedReader(new FileReader(room));

            //Kevin, convert lines to int arrays instead of array list
            //because indexing is cleaner than .get() on an array list
            var lines  = ft.lines()
                    .map(s -> Arrays.stream(s.split(" "))
                            .map(Integer::parseInt)
                            .toArray(Integer[]::new))
                    .collect(Collectors.toCollection(ArrayList::new));

            var cords = lines.get(0);
            var r = new Room(cords);
            System.out.println(Arrays.toString(cords));

            final int x1 = cords[0];
            final int y1 = cords[1];
            cords[2] += cords[0];
            cords[3] += cords[1];
            final int x2 = cords[2];
            final int y2 = cords[3];



            for(int x = x1; x < x2; x++){
                for(int y = y1; y < y2; y++){
                    //Kevin, add room edges
                    if(x == x1 || x == x2-1 || y == y1 || y == y2-1){
                        tiles[x][y] = new Tile(x*32,y*32, ss.getSprite(0,2), TYPE.WALL, r);
                    } else {
                        tiles[x][y] = new Tile(x*32, y*32, ss.getSprite(10, 4), TYPE.FLOOR, r);
                    }
                }
            }

            var doors =  lines.get(1);

            //Kevin, insert doors by walking though defined cords (x1 y1 x2 y2)
            for(int i = 0; i < 4; i ++){
                int edgeBound = cords[i] - ((i > 1) ? 1 : 0);
                int dir = doors[i];

                if(dir == -1)
                    continue;

                //Kevin, get correct rotation of door based on if y cords or x cords
                var img = ss.getSprite(12 - i % 2 ,4);
                var t = TYPE.DOOR;

                //Kevin, add door to the right edge and add the door to the room list
                Function<Boolean, BiConsumer<Integer, Integer>> set_v = (v) -> { // EHHHHH it works
                    final boolean vt = v;
                    return (Integer a, Integer b) -> {
                        var tl =  new Door(a*32, b*32, img, t, r, vt);
                        r.doors.add(tl);
                        tiles[a][b] = tl;
                    };
                };
                BiConsumer<Integer, Integer> addDoor;
                if(i % 2 == 0){
                    addDoor = set_v.apply(false);
                    addDoor.accept(edgeBound, dir);
                    dir += 1;
                    addDoor.accept(edgeBound, dir);
                } else {
                    addDoor = set_v.apply(true);
                    addDoor.accept(dir, edgeBound);
                    dir += 1;
                    addDoor.accept(dir, edgeBound);
                }
            }

            lines.stream().skip(2).filter(l -> l.length >= 1).forEach(l -> {
                System.out.println(Arrays.toString(l));
                int type = l[0];
                int count = l[1];
                int dir = l[2];
                int xp = l[3];
                int yp = l[4];

                //Kevin, 0 or 1 depending on dir
                int xdir = 1 & ~(0 ^ dir);
                int ydir = 1 & ~(1 ^ dir);

                TYPE t;
                Image img;
                switch (type){
                    case 1:
                       t = TYPE.WALL;
                       img = ss.getSprite(0,2);
                       break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }

                //Kevin, assert tile are within the room bounds
                assert x1 <= xp && (xp + count * xdir) < x2;
                assert y1 <= yp && (yp + count * ydir) < y2;

                //Kevin, insert the tile line into the map
                for(int i = 0; i < count; i++){
                    tiles[xp][yp] = new Tile(xp*32, yp*32, img, t, r);
                    xp += xdir;
                    yp += ydir;
                }
            });

            rooms.add(r);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int[][] getPiArray() {
        return pi;
    }
}

