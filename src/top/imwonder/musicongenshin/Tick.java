package top.imwonder.musicongenshin;

import static top.imwonder.musicongenshin.MusicOnGenshin.transShortToBytes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tick {

    public static final byte ZERO = 0;

    private List<Tone> tones;

    private short delay;

    public Tick(int delay) {
        this.delay = (short) delay;
        this.tones = new ArrayList<>();
    };

    public void addTone(Tone tone) {
        tones.add(tone);
    }

    public void write(OutputStream out) throws IOException {
        Iterator<Tone> itone = tones.iterator();
        while (itone.hasNext()) {
            Tone item = itone.next();
            out.write(transShortToBytes((short) item.getKey()));
            if (itone.hasNext()) {
                out.write(transShortToBytes(ZERO));
            } else {
                out.write(transShortToBytes(delay));
            }
        }
    }
}
