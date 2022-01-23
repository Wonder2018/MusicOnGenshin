package top.imwonder.musicongenshin.pojo;

public class Note implements Comparable<Note> {
    // private static final byte[] BLK_KEY = { 22, 25, 27, 30, 32, 34, 37, 39, 42,
    // 44, 46, 49, 51, 54, 56, 58, 58, 61, 63,
    // 66, 68, 70, 73, 75, 78, 80, 82, 85, 87, 90, 92, 94, 97, 99, 102, 104, 106 };

    private final long tick;
    private final byte note;
    private final float speed;

    public Note(long tick, byte note, float speed) {
        this.tick = tick;
        this.note = note;
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Note [note=" + note + ", tick=" + tick + "]";
    }

    public long getTick() {
        return tick;
    }

    public byte getNote() {
        return note;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public int compareTo(Note note) {
        return (int) (this.tick - note.getTick());
    }
}
