package top.imwonder.musicongenshin;

import java.util.HashMap;
import java.util.Map;

public enum Tone {
    HDO("+1", 'Q'), HRE("+2", 'W'), HMI("+3", 'E'), HFA("+4", 'R'), HSOL("+5", 'T'), HLA("+6", 'Y'), HSI("+7", 'U'),
    DO("1", 'A'), RE("2", 'S'), MI("3", 'D'), FA("4", 'F'), SOL("5", 'G'), LA("6", 'H'), SI("7", 'J'), LDO("-1", 'Z'),
    LRE("-2", 'X'), LMI("-3", 'C'), LFA("-4", 'V'), LSOL("-5", 'B'), LLA("-6", 'N'), LSI("-7", 'M'),STOP("0",'P');

    private String record;

    private char key;

    private static Map<String, Tone> tones;

    static {
        tones = new HashMap<>();
        for (Tone item : values()) {
            tones.put(item.record, item);
        }
    }

    private Tone(String record, char key) {
        this.record = record;
        this.key = key;
    }

    public static Tone getToneByRecord(String record) {
        return tones.get(record);
    }

    public char getKey() {
        return this.key;
    }

}
