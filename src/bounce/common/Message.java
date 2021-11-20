package bounce.common;


import jig.Vector;

import java.io.Serializable;

public class Message implements Serializable {
    public enum MSG_TYPE {
        SET_VELOCITY,
        NEW_POSITION,
        INIT_CHARACTER,
        ADD_ENTITY,
        REMOVE_ENTITY
    }

    public enum ENTITY_TYPE{
        CHARACTER,
        ENEMY,
        PROJECTILE
    }
    public MSG_TYPE type;
    public ENTITY_TYPE etype;
    public Object data;
    public int id;
    public Vector gamepos;
    public Vector velocity;

    public Message(MSG_TYPE t, Object d, int _id) {
        type = t;
        data = d;
        id = _id;
    }


    public Message(Vector pos, int id){
        type = MSG_TYPE.NEW_POSITION;
        gamepos = pos;
        etype = ENTITY_TYPE.CHARACTER;
    }
    //(Kevin) message type for setting an entitys position
    public Message(Vector pos,  ENTITY_TYPE et) {
        type = MSG_TYPE.NEW_POSITION;
        gamepos = pos;
        etype = et;
    }
    public Message(MSG_TYPE t, Object d, ENTITY_TYPE et) {
        type = t;
        data = d;
        etype = et;
    }
    //(Kevin) create a message that will add an entity with the specified stats
    public static Message add_entity(float x, float y, float vx, float vy, int spritex, int spritey,ENTITY_TYPE et){
        Object[] dat = new Object[]{
                spritex,
                spritey
        };
        var m =  new Message(MSG_TYPE.ADD_ENTITY, dat,  et);
        m.gamepos = new Vector(x,y);
        m.velocity = new Vector(vx,vy);
        return m;
    }
}



