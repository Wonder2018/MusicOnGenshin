package top.imwonder.musicongenshin.pojo;

import static top.imwonder.musicongenshin.MusicOnGenshin.transShortToBytes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tick {

    public static final byte ZERO = 0;

    private List<LyreToneEnum> tones;

    private short delay;

    public Tick(int delay) {
        this.delay = (short) delay;
        this.tones = new ArrayList<>();
    };

    public void addDelay(Tick tick) {
        this.delay += tick.delay;
    }

    public void addDelay(short tick) {
        this.delay += tick;
    }

    public void addTone(LyreToneEnum tone) {
        tones.add(tone);
    }

    public boolean isPause() {
        return tones.indexOf(LyreToneEnum.STOP) > -1;
    }

    public void write(OutputStream out) throws IOException {
        Iterator<LyreToneEnum> itone = tones.iterator();
        while (itone.hasNext()) {
            LyreToneEnum item = itone.next();
            out.write(transShortToBytes((short) item.getKey()));
            if (itone.hasNext()) {
                out.write(transShortToBytes(ZERO));
            } else {
                out.write(transShortToBytes(delay));
            }
        }
    }
}
