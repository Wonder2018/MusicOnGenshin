package top.imwonder.musicongenshin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MusicOnGenshin {

    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        config.load(new FileReader("config.properties"));
        File scriptIn = new File(config.getProperty("input"));
        File binOut = new File(config.getProperty("output"));
        System.out.println("Output path: " + binOut.getAbsolutePath());
        cook(scriptIn, binOut);
    }

    public static void cook(File script, File bin) throws IOException {
        try (OutputStream out = new FileOutputStream(bin); Reader reader = new FileReader(script)) {
            List<Tick> ticks = readScript(reader);
            writeBin(out, ticks);
        }
    }

    public static List<Tick> readScript(Reader reader) throws IOException {
        List<Tick> ticks = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);
        String speed[] = br.readLine().split(",");
        int bits = Integer.parseInt(speed[0]);
        int rate = Integer.parseInt(speed[1]);
        int sdly = 60000 / bits / rate;
        String line = null;
        int count = 0;
        while ((line = br.readLine()) != null) {
            String oTick[] = line.split(",");
            int bit = Integer.parseInt(oTick[1]);
            Tick tick = new Tick(bit * sdly);
            anlzTone(tick, oTick[0].toCharArray());
            ticks.add(tick);
            count += bit;
        }
        System.out.println("Count: " + (count / rate));
        return ticks;
    }

    public static Tick anlzTone(Tick tick, char[] tones) {
        int len = tones.length;
        for (int i = 0; i < len; i++) {
            switch (tones[i]) {
            case '+':
                i++;
                tick.addTone(Tone.getToneByRecord("+" + tones[i]));
                break;
            case '-':
                i++;
                tick.addTone(Tone.getToneByRecord("-" + tones[i]));
                break;
            default:
                tick.addTone(Tone.getToneByRecord("" + tones[i]));
                break;
            }
        }
        return tick;
    }

    public static void writeBin(OutputStream out, List<Tick> ticks) throws IOException {
        for (Tick tick : ticks) {
            tick.write(out);
        }
    }

    public static byte[] transShortToBytes(short value) {
        byte b[] = new byte[] { (byte) (value >> 8), (byte) value };
        ;
        return b;
    }
}
