package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/**
 * A repository for Capers
 *
 * @author Edward
 * The structure of a Capers Repository is as follows:
 * <p>
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 * - dogs/ -- folder containing all of the persistent data for dogs
 * - story -- file containing the current story
 * <p>
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /**
     * Current Working Directory.
     */
    static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Main metadata folder.
     */
    static final File CAPERS_FOLDER = join(CWD, ".capers");

    // TODO Hint: look at the `join`
    //      function in Utils

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     * <p>
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     * - dogs/ -- folder containing all of the persistent data for dogs
     * - story -- file containing the current story
     */
    public static void setupPersistence() {
        // TODO
        File f = CAPERS_FOLDER;
        if (!f.exists()) {
            f.mkdir();
        }
        f = join(CAPERS_FOLDER, "story");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ignore) {
            }
        }
        f = Dog.DOG_FOLDER;
        if (!f.exists()) {
            f.mkdir();
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     *
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO
        File f = join(CAPERS_FOLDER, "story");
        String s = readContentsAsString(f) + text + "\n";
        writeContents(f, s);
        System.out.print(s);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO
        File f = join(Dog.DOG_FOLDER, name);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ignore) {

            }
        }
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
        System.out.println(dog);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     *
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // TODO
        File f = join(Dog.DOG_FOLDER, name);
        Dog dog = readObject(f, Dog.class);
        dog.haveBirthday();
        dog.saveDog();
    }
}
