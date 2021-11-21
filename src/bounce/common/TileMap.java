package bounce.common;

import jig.Vector;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

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

    private final int maxx;
    private final int maxy;

    PriorityQueue<int[]> Q = new PriorityQueue<>(100, (a,b) -> Integer.compare(a[2],b[2])); //Priority queue for the dijkstra algorithm.

    public TileMap(int tilesx, int tilesy, SpriteSheet ss) {
        maxx = tilesx;
        maxy = tilesy;

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
                    tileType = "Wall";
                    t = TYPE.WALL;
                } else {
                    i = ss.getSprite(10, 4);
                    tileType = "Floor";
                    t= TYPE.FLOOR;
                }
                tiles[x][y] = new Tile(0,0, new Vector(x*32, y*32),i,tileType);
                tiles[x][y].type = t;
            }
        }
        S = new int[tiles.length][tiles[0].length];
        d = new int[tiles.length][tiles[0].length];
        pi = new int[tiles.length][tiles[0].length];
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
            }
        }

    }

    public void MakePath(ArrayList<Vector> goals){
        for(var arr : costs){
            Arrays.fill(arr, Integer.MAX_VALUE);
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

        while (!tocheck.isEmpty()){
            var cur = tocheck.poll();
            var curcost = costs[cur[0]][cur[1]];
            getNeighbors(cur)
                    .stream()
                    .forEach(t -> {
                        // (Kevin) ignore walls and set cost to +1
                        int cost = (tiles[t[0]][t[1]].type == TYPE.WALL) ? Integer.MAX_VALUE : costs[t[0]][t[1]];
                        if (cost != Integer.MAX_VALUE && curcost + 1 < cost){
                            costs[t[0]][t[1]] = curcost + 1;
                            DirToNext[t[0]][t[1]] = new Vector(cur[0] - t[0], cur[1] - t[1]);
                            tocheck.add(t);
                        }
                    });
        }
    }

    private ArrayList<int[]> getNeighbors(int[] t) {
        var neighbors = new ArrayList<int[]>();
        //(Kevin) go in all 8 dirs
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int gpx = t[0] + x;
                int gpy = t[1] + y;

                //(Kevin) make sure neighbor exists
                if(0 <= gpy && gpy < maxy){
                    if(0 <= gpx && gpx < maxy){
                        neighbors.add(new int[]{gpx, gpy});
                    }
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
                    if (tiles[u[0] + 1][u[1]].tileType == "Floor") { //If to the right is a floor tile.
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                    if (tiles[u[0]][u[1] + 1].tileType == "Floor") { //If to the down is a floor tile
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                    if (tiles[u[0] + 1][u[1] + 1].tileType == "Floor") { //If to the down right
                        v[0] = u[0] + 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "RD");
                    }

                } else if (u[0] == tiles.length - 1 && u[1] == 0) { //If the top right corner
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].tileType == "Floor") { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0]][u[1] + 1].tileType == "Floor") { //If to the down is a floor tile
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                    if (tiles[u[0] - 1][u[1] + 1].tileType == "Floor") { //If to the down left
                        v[0] = u[0] - 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "LD");
                    }
                } else if (u[0] == 0 && u[1] == tiles[0].length - 1) { //If at bottom left
                    int[] v = new int[2];
                    if (tiles[u[0] + 1][u[1]].tileType == "Floor") { //If to the right is a floor tile.
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                    if (tiles[u[0]][u[1] - 1].tileType == "Floor") { //If to the up is a floor tile
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] + 1][u[1] - 1].tileType == "Floor") { //If to the up right
                        v[0] = u[0] + 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "RU");
                    }
                } else if (u[0] == tiles.length - 1 && u[1] == tiles[0].length - 1) { // If at bottom right
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].tileType == "Floor") { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0]][u[1] - 1].tileType == "Floor") { //If to the up is a floor tile
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] - 1][u[1] - 1].tileType == "Floor") { //If to the up left
                        v[0] = u[0] - 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "LU");
                    }
                } else if (u[0] == 0) { //Left of the maze
                    int[] v = new int[2];
                    if (tiles[u[0]][u[1] - 1].tileType == "Floor") { //If to the up is a floor tile.
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] + 1][u[1] - 1].tileType == "Floor") { //If to the up right is a floor tile
                        v[0] = u[0] + 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "RU");
                    }
                    if (tiles[u[0] + 1][u[1]].tileType == "Floor") { //If to the right
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                    if (tiles[u[0] + 1][u[1] + 1].tileType == "Floor") { //If to the down right
                        v[0] = u[0] + 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "RD");
                    }
                    if (tiles[u[0]][u[1] + 1].tileType == "Floor") { //If to the down
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                } else if (u[0] == tiles.length - 1) { //If on the right.
                    int[] v = new int[2];
                    if (tiles[u[0]][u[1] - 1].tileType == "Floor") { //If to the up is a floor tile.
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] - 1][u[1] - 1].tileType == "Floor") { //If to the up left is a floor tile
                        v[0] = u[0] - 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "LU");
                    }
                    if (tiles[u[0] - 1][u[1]].tileType == "Floor") { //If to the left
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0] - 1][u[1] + 1].tileType == "Floor") { //If to the down left
                        v[0] = u[0] - 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "LD");
                    }
                    if (tiles[u[0]][u[1] + 1].tileType == "Floor") { //If to the down
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                } else if (u[1] == 0) { //If on the top of the maze
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].tileType == "Floor") { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0] - 1][u[1] + 1].tileType == "Floor") { //If to the up left down
                        v[0] = u[0] - 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "LD");
                    }
                    if (tiles[u[0]][u[1] + 1].tileType == "Floor") { //If to the Down
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                    if (tiles[u[0] + 1][u[1] + 1].tileType == "Floor") { //If to the down right
                        v[0] = u[0] + 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "RD");
                    }
                    if (tiles[u[0] + 1][u[1]].tileType == "Floor") { //If to the right
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                } else if (u[1] == tiles[0].length - 1) { //If on the bottom
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].tileType == "Floor") { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0] - 1][u[1] - 1].tileType == "Floor") { //If to the up left up
                        v[0] = u[0] - 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "LU");
                    }
                    if (tiles[u[0]][u[1] - 1].tileType == "Floor") { //If to the up
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] + 1][u[1] - 1].tileType == "Floor") { //If to the down up
                        v[0] = u[0] + 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "RU");
                    }
                    if (tiles[u[0] + 1][u[1]].tileType == "Floor") { //If to the right
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                } else { //Else any other coordinates
                    int[] v = new int[2];
                    if (tiles[u[0] - 1][u[1]].tileType == "Floor") { //If to the left is a floor tile.
                        v[0] = u[0] - 1;
                        v[1] = u[1];
                        relax(u, v, "L");
                    }
                    if (tiles[u[0] - 1][u[1] - 1].tileType == "Floor") { //If to the up left up
                        v[0] = u[0] - 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "LU");
                    }
                    if (tiles[u[0]][u[1] - 1].tileType == "Floor") { //If to the up
                        v[0] = u[0];
                        v[1] = u[1] - 1;
                        relax(u, v, "U");
                    }
                    if (tiles[u[0] + 1][u[1] - 1].tileType == "Floor") { //If to the down up
                        v[0] = u[0] + 1;
                        v[1] = u[1] - 1;
                        relax(u, v, "RU");
                    }
                    if (tiles[u[0] + 1][u[1]].tileType == "Floor") { //If to the right
                        v[0] = u[0] + 1;
                        v[1] = u[1];
                        relax(u, v, "R");
                    }
                    if (tiles[u[0] + 1][u[1] + 1].tileType == "Floor") { //If to the right down
                        v[0] = u[0] + 1;
                        v[1] = u[1] + 1;
                        relax(u, v, "RD");
                    }
                    if (tiles[u[0]][u[1] + 1].tileType == "Floor") { //If to the down
                        v[0] = u[0];
                        v[1] = u[1] + 1;
                        relax(u, v, "D");
                    }
                    if (tiles[u[0] - 1][u[1] + 1].tileType == "Floor") { //If to the down left
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

    public int[][] getPiArray() {
        return pi;
    }
}
