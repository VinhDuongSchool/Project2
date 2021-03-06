package bounce.common;


import jig.Entity;
import jig.Vector;

import java.io.Serializable;

public class Message implements Serializable {
    public enum MSG_TYPE {
        SET_VELOCITY,
        NEW_POSITION,
        INIT_CHARACTER,
        ADD_ENTITY,
        REMOVE_ENTITY,
        MOUSE_IDX,
        SET_DIR,
        INIT_GRID,
        SET_HP,
        PRIMARY,
        SET_ATTACK_TIMER,
        COMPLETE_ROOM,
        DEAD,
        CLOSE_ROOM,
        ADD_GOLD,
        REVIVE
    }

    public enum ENTITY_TYPE{
        CHARACTER,
        WARRIOR,
        ARCHER,
        ENEMY,
        PROJECTILE,
        SHADOWARCHER,
        ZOMBIE,
        GOLDPILE,
        POTION
    }
    public final MSG_TYPE type;
    public ENTITY_TYPE etype;
    public Object data;
    public long id;
    public Vector gamepos;
    public Vector velocity;
    public lib.DIRS dir;
    public int HP;
    public int intData;
    public Class<? extends Entity> ctype;

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

    //Kevin, message builder stuff
    private Message(MSG_TYPE t, long _id){
        id = _id;
        type = t;
    }

    public static Message builder(MSG_TYPE t, long id){
        return new Message(t, id);
    }

    public Message setData(Object data) {
        this.data = data;
        return this;
    }

    public Message setGamepos(Vector gamepos) {
        this.gamepos = gamepos;
        return this;
    }

    public Message setId(long id) {
        this.id = id;
        return this;
    }

    public Message setVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }

    public Message setDir(lib.DIRS dir) {
        this.dir = dir;
        return this;
    }

    public Message setEtype(ENTITY_TYPE etype) {
        this.etype = etype;
        return this;
    }

    public Message setHP(int hp) {
        this.HP = hp;
        return this;
    }

    public Message setIntData(int d) {
        this.intData = d;
        return this;
    }

    public Message setCType(Class<? extends Entity> c) {
        this.ctype = c;
        return this;
    }
}
