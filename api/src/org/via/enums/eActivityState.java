package org.via.enums;

public enum eActivityState {
    Activer(1),
    Supprimer(2),
    Desactiver(3),
    Archiver(4);


    private final int value;

    private eActivityState(int val) {
        this.value = val;
    }


    public static eActivityState GetActivityState(int val) {
        switch (val) {
        case 1:
            return Activer;
        case 2:
            return Supprimer;
        case 3:
            return Desactiver;
        case 4:
            return Archiver;
        default:
            return Activer;
        }
    }


    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        switch (value) {
        case 1:
            return "Activer";
        case 2:
            return "Supprimer";
        case 3:
            return "Desactiver";
        case 4:
            return "Archiver";
        default:
            return "Activer";
        }
    }
}
