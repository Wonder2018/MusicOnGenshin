package top.imwonder.musicongenshin.pojo;

import java.util.HashMap;
import java.util.Map;

public enum LyreToneEnum {
    HDO("+1", 'Q'), HRE("+2", 'W'), HMI("+3", 'E'), HFA("+4", 'R'), HSOL("+5", 'T'), HLA("+6", 'Y'), HSI("+7", 'U'),
    DO("1", 'A'), RE("2", 'S'), MI("3", 'D'), FA("4", 'F'), SOL("5", 'G'), LA("6", 'H'), SI("7", 'J'), LDO("-1", 'Z'),
    LRE("-2", 'X'), LMI("-3", 'C'), LFA("-4", 'V'), LSOL("-5", 'B'), LLA("-6", 'N'), LSI("-7", 'M'), STOP("0", 'P');

    private String record;

    private char key;

    private static final Map<String, LyreToneEnum> TONES_RCD;
    private static final Map<Character, LyreToneEnum> TONES_KEY;

    static {
        TONES_RCD = new HashMap<>();
        TONES_KEY = new HashMap<>();
        for (LyreToneEnum item : values()) {
            TONES_RCD.put(item.record, item);
            TONES_KEY.put(item.key, item);
        }
    }

    private LyreToneEnum(String record, char key) {
        this.record = record;
        this.key = key;
    }

    public static LyreToneEnum getToneByRecord(String record) {
        return TONES_RCD.get(record);
    }

    public static LyreToneEnum getToneByKey(char key) {
        return TONES_KEY.get(Character.toUpperCase(key));
    }

    public char getKey() {
        return this.key;
    }

}
