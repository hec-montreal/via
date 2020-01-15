package org.via.enums;

public enum eParticipantType {
    Participant(1),
    Presentateur(2),
    Animator(3);

    private final int value;

    private eParticipantType(int val) {
        this.value = val;
    }


    public static eParticipantType GetParticipantType(int val) {
        switch (val) {
        case 1:
            return Participant;
        case 2:
            return Presentateur;
        case 3:
            return Animator;
        default:
            return Participant;
        }
    }


    public int getValue() {
        return this.value;
    }

    public static String participantTypeString(int type) {
        switch (type) {
        case 1:
            return "Participant";
        case 2:
            return "Presentateur";
        case 3:
            return "Animateur";
        default:
            return "Participant";
        }
    }

    @Override
    public String toString() {
        switch (value) {
        case 1:
            return "Participant";
        case 2:
            return "Presentateur";
        case 3:
            return "Animateur";
        default:
            return "Participant";
        }
    }
}
