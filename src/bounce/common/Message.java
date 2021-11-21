package bounce.common;


import jig.Vector;

import java.io.Serializable;

public class Message implements Serializable {
    public enum MSG_TYPE {
        SET_VELOCITY,
        NEW_POSITION,
        INIT_CHARACTER,
        ADD_ENTITY,
        REMOVE_ENTITY,
        FIRE_PROJECTILE
    }

    public enum ENTITY_TYPE{
        CHARACTER,
        ENEMY,
        PROJECTILE
    }
    public MSG_TYPE type;
    public ENTITY_TYPE etype;
    public Object data;
    public long id;
    public Vector gamepos;
    public Vector velocity;

    public Message(MSG_TYPE t, Object d, long _id) {
        type = t;
        data = d;
        id = _id;
    }



    public Message(MSG_TYPE t, Object d, long _id, ENTITY_TYPE et) {
        type = t;
        data = d;
        etype = et;
        id = _id;
    }
    public Message(MSG_TYPE t, Vector d, long _id, ENTITY_TYPE et) {
        type = t;
        if (t == MSG_TYPE.SET_VELOCITY)
            velocity = d;
        if (t == MSG_TYPE.NEW_POSITION)
            gamepos = d;

        assert velocity == null ^ gamepos == null;
        etype = et;
        id = _id;
    }


    //(Kevin) create a message that will add an entity with the specified stats
    public static Message add_entity(Vector pos, Vector vel, int spritex, int spritey, long _id, ENTITY_TYPE et){
        Object[] dat = new Object[]{
                spritex,
                spritey
        };
        var m =  new Message(MSG_TYPE.ADD_ENTITY, dat, -1,  et);
        m.gamepos = pos;
        m.velocity = vel;
        m.id = _id;
        return m;
    }
}



