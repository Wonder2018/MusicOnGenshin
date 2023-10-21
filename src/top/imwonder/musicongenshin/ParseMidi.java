package top.imwonder.musicongenshin;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import top.imwonder.musicongenshin.pojo.LyreToneEnum;
import top.imwonder.musicongenshin.pojo.Note;
import top.imwonder.musicongenshin.pojo.PianoToneEnum;
import top.imwonder.musicongenshin.pojo.Tick;

public class ParseMidi {

    private static final byte[] KEY_MAP = new byte[] { 0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6 };

    private static final byte[] BLKS = new byte[] { 1, 3, 6, 8, 10, 13, 15, 18, 20, 22, 25, 27, 30, 32, 34 };

    private static final int DEFAULT_SPEED = 160; // 拍每分钟

    private static float speed = 0; // ms/tick
    private static byte maxNote = Byte.MIN_VALUE;
    private static byte minNote = Byte.MAX_VALUE;

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidMidiDataException {
        String fin = "wonderTmp/xxsj.mid";
        String fon = "wonderTmp/xxsj";
        // Properties config = new Properties();
        // config.load(new FileReader("config.properties"));
        // fin = args.length > 0 ? args[0] : config.getProperty("input");
        // fon = args.length > 1 ? args[1] : config.getProperty("output");
        File midiIn = new File(fin);
        File binOut = new File(fon);
        out.println("Output path: " + binOut.getAbsolutePath());
        cook(midiIn, binOut);
    }

    private static void cook(File midiIn, File bin) throws IOException, InvalidMidiDataException {
        try (OutputStream out = new FileOutputStream(bin)) {
            List<Note> notes = readForNotes(midiIn);
            notes = toLyreNotes(notes);
            List<Tick> ticks = parseToTicks(notes);
            MusicOnGenshin.writeBin(out, ticks);
        }
    }

    private static List<Note> readForNotes(File midiIn) throws InvalidMidiDataException, IOException {
        Sequence midi = MidiSystem.getSequence(midiIn);
        List<Note> noteList = new ArrayList<>();
        int trackSize = 0;
        MidiEvent event;
        MidiMessage message;
        ShortMessage shortMessage;
        MetaMessage metaMessage;
        int metaType;
        Note note;

        for (Track track : midi.getTracks()) {
            trackSize = track.size();
            for (int i = 0; i < trackSize; i++) {
                event = track.get(i);
                message = event.getMessage();
                byte[] msgData = event.getMessage().getMessage();
                if (message instanceof ShortMessage) {
                    shortMessage = (ShortMessage) message;
                    if (shortMessage.getCommand() == ShortMessage.NOTE_ON && msgData[2] != 0) {
                        if (speed == 0) {
                            out.println("Warn: midi文件未设置速度！将使用默认速度 " + DEFAULT_SPEED);
                            speed = 60000f / DEFAULT_SPEED / midi.getResolution();
                        }
                        note = new Note(event.getTick(), (byte) (shortMessage.getData1()), speed);
                        noteList.add(note);
                        maxNote = maxNote > note.getNote() ? maxNote : note.getNote();
                        minNote = minNote < note.getNote() ? minNote : note.getNote();
                    }
                } else if (message instanceof MetaMessage) {
                    metaMessage = (MetaMessage) message;
                    metaType = metaMessage.getType();
                    switch (metaType) {
                        case 0x51:
                            speed = ((float) readSpeed(metaMessage.getData())) / ((float) (midi.getResolution()));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        Collections.sort(noteList);
        out.printf("当前乐谱最高音：%s\n", PianoToneEnum.getToneByMidiCode(maxNote));
        out.printf("当前乐谱最低音：%s\n", PianoToneEnum.getToneByMidiCode(minNote));
        return noteList;
    }

    private static List<Note> toLyreNotes(List<Note> notes) throws IOException {
        checkRange(notes);
        List<Note> lyreNotes = new ArrayList<>();
        int countBlk = 0;
        byte noteNum;
        for (Note note : notes) {
            noteNum = to36Key(note.getNote(), minNote);
            if (noteNum < 0) {
                continue;
            }
            if (Arrays.binarySearch(BLKS, noteNum) > -1) {
                countBlk++;
            }
            lyreNotes.add(new Note(note.getTick(), toLyreKey(noteNum), note.getSpeed()));

        }
        out.printf("当前范围内的黑键有： %d (%.2f%%)", countBlk, countBlk * 100.0 / lyreNotes.size());
        return lyreNotes;
    }

    private static List<Tick> parseToTicks(List<Note> notes) {
        List<Tick> ticks = new ArrayList<>();
        long seqTick = 0;// 序列位置
        long notTick = 0;// 当前音符位置
        Tick tick = new Tick(0);
        for (Note note : notes) {
            notTick = note.getTick();
            if (notTick != seqTick) {
                if (speed == 0) {
                    speed = note.getSpeed();
                }
                tick.addDelay((short) ((notTick - seqTick) * speed));
                ticks.add(tick);
                tick = new Tick(0);
                seqTick = notTick;
            }
            tick.addTone(LyreToneEnum.values()[note.getNote()]);
            speed = note.getSpeed();
        }
        ticks.add(tick);
        return ticks;
    }

    private static byte to36Key(byte noteNum, byte move) {
        byte rst = (byte) (noteNum - move);
        if (rst < 36) {
            return rst;
        }
        return -1;
    }

    private static byte toLyreKey(byte noteNum) {
        int level = noteNum / 12;
        int index = noteNum % 12;
        return (byte) (level * 7 + KEY_MAP[index]);
    }

    private static void checkRange(List<Note> notes) throws IOException {
        if (maxNote - minNote > 35) {
            narrowRange();
        }
        if (maxNote - maxNote < 35) {
            minNote = selectRange(notes);
        }
    }

    private static void narrowRange() throws IOException {
        PianoToneEnum dftMaxTone = PianoToneEnum.getToneByMidiCode(maxNote);
        PianoToneEnum dftMinTone = PianoToneEnum.getToneByMidiCode(minNote);
        PianoToneEnum maxTone;
        PianoToneEnum minTone;
        int aria = Integer.MAX_VALUE;
        while (maxNote - minNote > 35) {
            out.println("Midi 音域超出原神键盘范围，请选择保留范围！\n注意：钢琴黑键统一使用降号表示，如：D3b");
            out.printf("目前最高音：%s\n", dftMaxTone);
            out.printf("目前最低音：%s\n", dftMinTone);
            out.printf("音域范围（需不大于36）：%d (%s - %s)\n", maxNote - minNote + 1, dftMinTone, dftMaxTone);
            maxTone = getValidTone(String.format("请输入要保留的最*高*音(%s)：", dftMaxTone), dftMaxTone);
            if (maxTone.getMidiCode() > 56) {
                minTone = PianoToneEnum.getToneByMidiCode(maxTone.getMidiCode() - 35);
            } else {
                minTone = dftMinTone;
            }
            minTone = getValidTone(String.format("请输入要保留的最*低*音(%s)：", minTone), minTone);
            aria = maxTone.getMidiCode() - minTone.getMidiCode();
            if (aria > 35) {
                out.println("您选择的音域仍然超出了原神键盘范围，");
                out.printf("乐谱音域是：%s - %s (%d)，\n", dftMinTone, dftMaxTone, maxNote - minNote + 1);
                out.printf("选择的音域是：%s - %s (%d)。\n", minTone, maxTone, aria + 1);
                out.println("请重新输入，并保证所选范围不大于36。");
            } else {
                maxNote = maxTone.getMidiCode();
                minNote = minTone.getMidiCode();
            }
        }
    }

    private static PianoToneEnum getValidTone(String tip, PianoToneEnum defaultTone) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PianoToneEnum tone = null;
        while (true) {
            out.print(tip);
            tone = PianoToneEnum.getToneByKeyName(reader.readLine());
            if (tone != null) {
                return tone;
            } else if (defaultTone != null) {
                return defaultTone;
            }
            out.println("输入无效，请重新输入！");
        }
    }

    private static int getValidNum(String tip, Integer defaultNum, Integer... nums) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<Integer> choseList = new ArrayList<Integer>(Arrays.asList(nums));
        int num = 0;
        while (true) {
            out.print(tip);
            try {
                num = Integer.parseInt(reader.readLine());
                if (choseList.size() == 0) {
                    return num;
                } else if (choseList.indexOf(num) > -1) {
                    return num;
                }
            } catch (NumberFormatException e) {
                if (defaultNum != null) {
                    return defaultNum;
                }
            }
            out.println("输入无效，请重新输入！");
        }
    }

    private static byte selectRange(List<Note> notes) throws IOException {
        if (maxNote - minNote == 35) {
            return minNote;
        }
        out.println("当前乐谱音域不是 36 将自动匹配黑键最少的方案！");
        byte nowMin = (byte) (maxNote - 35);
        int minCountBlk = Integer.MAX_VALUE;
        int countBlk = 0;
        List<Byte> minList = new ArrayList<>();
        for (byte i = nowMin; i <= minNote; i++) {
            for (Note note : notes) {
                if (Arrays.binarySearch(BLKS, to36Key(note.getNote(), i)) > -1) {
                    countBlk++;
                }
            }
            if (minCountBlk >= countBlk) {
                nowMin = i;
                minCountBlk = countBlk;
                if (minCountBlk == 0) {
                    minList.add(i);
                }
            }
            countBlk = 0;
        }
        int size = minList.size();
        if (size == 0) {
            return nowMin;
        } else if (size == 1) {
            return minList.get(0);
        } else {
            out.println("匹配到多个黑键数量为 0 的方案：");
            int ind = 1;
            List<Integer> indList = new ArrayList<>();
            for (Byte minCase : minList) {
                LyreToneEnum lyreKey = LyreToneEnum.values()[toLyreKey(to36Key(minNote, minCase))];
                out.printf("\t %d. %s -> %s(%s)\n", ind, PianoToneEnum.getToneByMidiCode(minNote),
                        PianoToneEnum.getToneByMidiCode(minCase),
                        lyreKey.name());
                indList.add(ind++);
            }
            return minList.get(
                    getValidNum(String.format("请输入要选择的方案(%d)：", size / 2), size / 2, indList.toArray(new Integer[0]))
                            - 1);
        }
    }

    private static int readSpeed(byte[] tempo) {
        return readInt(tempo) / 1000;
    }

    private static int readInt(byte[] data) {
        int rst = 0;
        for (byte b : data) {
            rst <<= 8;
            rst += b & 0xff;
        }
        return rst;
    }
}
