package org.via.enums;

public enum eRoomType {
    Standard(1),
    Seminaire(2);

    private final int value;

    private eRoomType(int val) {
        this.value = val;
    }


    public static eRoomType GetRoomType(int val) {
        switch (val) {
        case 1:
            return Standard;
        case 2:
            return Seminaire;
        default:
            return Standard;
        }
    }


    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        switch (value) {
        case 1:
            return "Standard";
        case 2:
            return "Seminaire";
        default:
            return "Standard";
        }
    }

}
