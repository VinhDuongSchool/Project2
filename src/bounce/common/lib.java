package bounce.common;


import jig.Vector;

public class lib {

    // returns a new vector that turns the world coordinates to screen coordinates
    static Vector to_screen(float wx, float wy, Vector screen_orgin){
        return screen_orgin.transform(new float[]{1,0, 0, 1, (wx + wy),  wy/2 - wx/2});
    }
}
