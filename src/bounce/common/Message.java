package bounce.common;


import java.io.Serializable;

public class Message implements Serializable {
    public static enum MSG_TYPE {
        SET_VELOCITY,
        NEW_POSITION,
        INIT_CHARACTER,
    }

    public MSG_TYPE type;
    public Object data;
    public int id;

    public Message(MSG_TYPE t, Object d, int _id) {
        type = t;
        data = d;
        id = _id;
    }

}
