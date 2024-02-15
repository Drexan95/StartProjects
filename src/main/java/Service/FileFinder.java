package Service;

import org.example.App;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class FileFinder {
    /**
     * Метод ищёт источники переданные в командной строке
     * @param args
     * @param path
     * @throws IOException
     */
    public static void getFiles(String[] args, String path) throws IOException {
            if (args.length == 0) {
                System.out.println("Ошибка! Введите исходные данные!!!");
                return;
            }
        for (int i = args.length - 1; i >= 0; i--) {
            if (args.length<0 || !args[i].endsWith(".txt")) {
                break;
            }
            File file = new File(path + args[i]);
            App.files.add(file);
        }

    }

    /**
     * Метод читает строки и сохраняет их для обработки
     * @param files
     * @return
     * @throws IOException
     */
    public static List<String> readFiles(List<File> files) throws IOException {

        List<String> strings= new ArrayList<>();
        for (File file : files){
            strings.addAll(Files.readAllLines(Paths.get(file.getPath()))) ;
        }
     return strings;
    }

    /**
     * Метод проверяет налачие папки, по необходимости создаёт их
     */
    public static void checkDirectory(){
        if(!App.o.endsWith("\\\\")){
            App.o=App.o+"\\\\";
        }
        File directory = new File(App.o);
        if (!(directory.exists())){
            directory.mkdirs();
        }

    }
}
