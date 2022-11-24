package byow.Core;

import byow.Core.Character.Characters;
import edu.princeton.cs.introcs.StdDraw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File saveFile = join(CWD, "savefiles.txt");

    /**
     * Assorted utilities.
     * <p>
     * Give this file a good read as it provides several useful utility functions
     * to save you some time.
     *
     * @author P. N. Hilfinger
     */
    public static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    public static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    /**
     * Write the result of concatenating the bytes in CONTENTS to FILE,
     * creating or overwriting it as needed.  Each object in CONTENTS may be
     * either a String or a byte array.  Throws IllegalArgumentException
     * in case of problems.
     */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     * Throws IllegalArgumentException in case of problems.
     */
    static <T extends Serializable> T readObject(File file, Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* SERIALIZATION UTILITIES */

    /**
     * Returns a byte array containing the serialized contents of OBJ.
     */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw new RuntimeException("Internal error serializing");
        }
    }

    /**
     * Write OBJ to FILE.
     */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* ENGINE UTILS */

    /**
     * Assorted utilities.
     * <p>
     * Give this file a good read as it provides several useful utility functions
     * to save you some time.
     *
     * @author Edward Tsang
     */
    public static String fixInputString(Engine engine, String input) {
        StringBuilder split = new StringBuilder();
        boolean loadFlag = false;
        boolean startFlag = false;
        boolean moveFlag = false;
        char[] in = input.toCharArray();
        for (int i = 0; i < input.length(); i++) {
            switch (in[i]) {
                case 'l', 'L' -> {
                    loadFlag = true;
                    split.append(in[i]);
                    moveFlag = true;
                }
                case 'n', 'N' -> {
                    if (!loadFlag) {
                        startFlag = true;
                        split.append(in[i]);
                    }
                }
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    if (startFlag && !loadFlag) {
                        split.append(in[i]);
                    }
                }
                case 's', 'S' -> {
                    if (startFlag && !loadFlag
                            && (input.length() - input.lastIndexOf('n') > 1
                            || input.length() - input.lastIndexOf('N') > 1)) {
                        split.append(in[i]);
                        startFlag = false;
                        moveFlag = true;
                    } else if (moveFlag || engine.start) {
                        split.append(in[i]);
                    }
                }
                case 'w', 'W', 'a', 'A', 'd', 'D' -> {
                    if (moveFlag || engine.start) {
                        split.append(in[i]);
                    }
                }
                case ':' -> {
                    if (in[i + 1] == 'q' || in[i + 1] == 'Q') {
                        split.append(':');
                        split.append('Q');
                        return split.toString().toUpperCase();
                    }
                }
            }
        }
        return split.toString().toUpperCase();
    }

    /**
     * @param input N#S, # means the seed number
     */
    public static void generateWorld(Variables v, String input) {
        long seed = Long.parseLong(input, 1, input.length() - 1, 10);
        //long seed = LocalTime.now().toNanoOfDay();
        v.world.initializeWorld(seed);
        v.tempWorld = v.world.clone();
        v.characters = new Characters(v.tempWorld);
        v.characters.setCharacters(v.tempWorld, "");
    }

    public static void getStarted(StringBuilder input) {
        boolean firstN = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                if (!firstN) {
                    if ((ch == 'N' || ch == 'n')) {
                        input.append(ch);
                        firstN = true;
                    } else if (ch == 'l' || ch == 'L') {
                        input.append(ch);
                        return;
                    }
                }
                if (firstN) {
                    if (ch >= '0' && ch <= '9') {
                        input.append(ch);
                    }
                    if (ch == 's' || ch == 'S') {
                        input.append(ch);
                        break;
                    }
                }
            }
        }
    }

    public static void inputCommands(StringBuilder input) {
        boolean quit = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                switch (ch) {
                    case 'w', 'W', 's', 'S', 'a', 'A', 'd', 'D' -> {
                        if (quit) {
                            input.deleteCharAt(0);
                        }
                        input.append(ch);
                        return;
                    }
                    case ':' -> {
                        input.append(ch);
                        quit = true;
                    }
                    case 'q', 'Q' -> {
                        if (quit) {
                            input.append(ch);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static Variables load() {
        return readObject(saveFile, Variables.class);
    }

    public static void save(Variables v) {
        writeObject(saveFile, v);
    }

    public static void quit(Variables v) {
        save(v);
    }

    public static void move(Variables v, String command) {
        for (char s : command.toCharArray()) {
            v.tempWorld = v.world.clone();
            v.characters.setCharacters(v.tempWorld, String.valueOf(s));
        }
    }
}
