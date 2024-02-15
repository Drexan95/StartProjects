package Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Класс который стирает файлы для перезаписи
 */
public class Eraser {
    public static void clearFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()){
            try (PrintWriter pw  = new PrintWriter(new File(path))){
            } catch (FileNotFoundException ex){
                System.out.println("");
            }
        }

    }
}
