import java.util.*;
import java.io.*;

// this class is used to save and load files
// for this project its used for saving and loading the shadow password file
public class SaveLoadController {

    private static FileOutputStream fileOut;
    private static ObjectOutputStream out;
    private static FileInputStream fileIn;
    private static ObjectInputStream in;



    public static void saveToShadowFile(String fileName, List<User> users) {

            try {

                fileOut = new FileOutputStream(fileName);
                out = new ObjectOutputStream(fileOut);
                out.writeObject(users);
                out.close();
                fileOut.close();
                System.out.println("Serial data is saved in: " + fileName);
            }
            catch(IOException i) {
                i.printStackTrace();
            }
        }

    public static ArrayList<User> loadFromShadowFile(String fileName) {
        ArrayList<User> newUsers = new ArrayList<User>();

        try {

            fileIn = new FileInputStream(fileName);
            in = new ObjectInputStream(fileIn);
            newUsers = (ArrayList<User>) in.readObject();
            in.close();
            fileIn.close();
        }

        catch(IOException i) {
            i.printStackTrace();
        }

        catch(ClassNotFoundException c) {
            System.out.println("Class Not Found");
            c.printStackTrace();
        }

        return newUsers;
    }
}