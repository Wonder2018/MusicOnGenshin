package top.imwonder.musicongenshin.pojo;

import java.util.HashMap;
import java.util.Map;

public enum PianoToneEnum{
    A0("A0", 21), B0_B("B0b", 22), B0("B0", 23),
    C1("C1", 24), D1_B("D1b", 25), D1("D1", 26), E1_B("E1b", 27), E1("E1", 28), F1("F1", 29), G1_B("G1b", 30),
    G1("G1", 31), A1_B("A1b", 32), A1("A1", 33), B1_B("B1b", 34), B1("B1", 35),
    C2("C2", 36), D2_B("D2b", 37), D2("D2", 38), E2_B("E2b", 39), E2("E2", 40), F2("F2", 41), G2_B("G2b", 42),
    G2("G2", 43), A2_B("A2b", 44), A2("A2", 45), B2_B("B2b", 46), B2("B2", 47),
    C3("C3", 48), D3_B("D3b", 49), D3("D3", 50), E3_B("E3b", 51), E3("E3", 52), F3("F3", 53), G3_B("G3b", 54),
    G3("G3", 55), A3_B("A3b", 56), A3("A3", 57), B3_B("B3b", 58), B3("B3", 59),
    C4("C4", 60), D4_B("D4b", 61), D4("D4", 62), E4_B("E4b", 63), E4("E4", 64), F4("F4", 65), G4_B("G4b", 66),
    G4("G4", 67), A4_B("A4b", 68), A4("A4", 69), B4_B("B4b", 70), B4("B4", 71),
    C5("C5", 72), D5_B("D5b", 73), D5("D5", 74), E5_B("E5b", 75), E5("E5", 76), F5("F5", 77), G5_B("G5b", 78),
    G5("G5", 79), A5_B("A5b", 80), A5("A5", 81), B5_B("B5b", 82), B5("B5", 83),
    C6("C6", 84), D6_B("D6b", 85), D6("D6", 86), E6_B("E6b", 87), E6("E6", 88), F6("F6", 89), G6_B("G6b", 90),
    G6("G6", 91), A6_B("A6b", 92), A6("A6", 93), B6_B("B6b", 94), B6("B6", 95),
    C7("C7", 96), D7_B("D7b", 97), D7("D7", 98), E7_B("E7b", 99), E7("E7", 100), F7("F7", 101), G7_B("G7b", 102),
    G7("G7", 103), A7_B("A7b", 104), A7("A7", 105), B7_B("B7b", 106), B7("B7", 107),
    C8("C8", 108);

    private String keyName;

    private byte midiCode;

    private static final Map<String, PianoToneEnum> NAME_SET;
    private static final Map<Byte, PianoToneEnum> CODE_SET;

    static {

        NAME_SET = new HashMap<>();
        CODE_SET = new HashMap<>();
        for (PianoToneEnum item : values()) {
            NAME_SET.put(item.keyName, item);
            CODE_SET.put(item.midiCode, item);
        }
    }

    PianoToneEnum(String keyName, int midiCode) {
        this.keyName = keyName;
        this.midiCode = (byte)midiCode;
    }

    public String getKeyName() {
        return keyName;
    }

    public byte getMidiCode() {
        return midiCode;
    }

    @Override
    public String toString() {
        return keyName;
    }

    public static PianoToneEnum getToneByKeyName(String keyName) {
        if (keyName == null || keyName.isEmpty()) {
            return null;
        }
        return NAME_SET.get(keyName.trim());
    }

    public static PianoToneEnum getToneByMidiCode(int midiCode) {
        return CODE_SET.get((byte)midiCode);
    }
}
