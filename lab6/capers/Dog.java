package capers;

import java.io.File;
import java.io.Serializable;

import static capers.Utils.*;

/**
 * Represents a dog that can be serialized.
 *
 * @author Edward
 */
public class Dog implements Serializable {

    /**
     * Folder that dogs live in.
     */
    static final File DOG_FOLDER = join(CapersRepository.CAPERS_FOLDER, "dogs");
    /**
     * Age of dog.
     */
    private int age;
    /**
     * Breed of dog.
     */
    private final String breed;
    /**
     * Name of dog.
     */
    private final String name;

    /**
     * Creates a dog object with the specified parameters.
     *
     * @param name  Name of dog
     * @param breed Breed of dog
     * @param age   Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        File f = join(DOG_FOLDER, name);
        return readObject(f, Dog.class);
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(this);
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        File f = join(DOG_FOLDER, this.name);
        writeObject(f, this);
    }

    @Override
    public String toString() {
        return String.format(
                "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
                name, breed, age);
    }

}
