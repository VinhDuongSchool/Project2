package bounce.common;


import jig.Vector;

public class lib {

    // returns a new vector that turns the world coordinates to screen coordinates
    static Vector to_screen(float wx, float wy, Vector screen_orgin){
        return screen_orgin.transform(new float[]{1,0, 0, 1, (wx + wy),  wy/2 - wx/2});

    }
    static Vector to_screen(float wx, float wy, Vector screen_orgin, float h){
        return screen_orgin.transform(new float[]{1,0, 0, 1, (wx + wy),  -h + (wy - wx)/2});

    }
    static Vector to_screen(float wx, float wy){
        return new Vector((wy + wx), (wy - wx)/2);
    }
    static Vector to_screen(Vector wxy){
        float wx = wxy.getX();
        float wy = wxy.getY();
        return new Vector((wy + wx), (wy - wx)/2);
    }
    static Vector to_screen(Vector wxy, Vector screen_orgin){
        float wx = wxy.getX();
        float wy = wxy.getY();
        return screen_orgin.transform(new float[]{1,0, 0, 1, (wx + wy),  wy/2 - wx/2});
    }

    static Vector screen_to_game(float wx, float wy){
        return new Vector( 1/(wx + wy),  2/(wy -wx));
    }

    static Vector screen_to_game(Vector sxy){
        float sx = sxy.getX();
        float sy = sxy.getY();
        return new Vector( 1/(sx + sy),  2/(sy -sx));
    }
}
