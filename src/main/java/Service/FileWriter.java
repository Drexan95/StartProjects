package Service;

import org.example.App;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class FileWriter {
  static int stringsCount;
  static int minString;
   static int maxString;

  static  long maxInt;
  static long minInt;
  static long sumInt;
  static double avgInt;

  static float maxFloat;
  static float minFloat;
  static float sumFloat;
  static float avgFloat;

    /**
     * Метод записывает слова и собирает статистку по ним, если они существуют
     * @param lines
     * @throws IOException
     */
    public static void writeString(List<String> lines) throws IOException {
        stringsCount = lines.size();
        String o = App.o;
        String p = App.p;
        Path path = Paths.get(o+ p +"strings.txt");
        if(lines.size()>0) {
            checkExistense(path);
            for (String line : lines) {
                Files.writeString(path,line + "\n", StandardOpenOption.APPEND);
            }
        }else {
            System.out.println("Слова отсутствуют");
            return;
        }
        try {
            maxString = lines.stream().max(Comparator.comparing(String::length)).get().length();
            minString = lines.stream().min(Comparator.comparing(String::length)).get().length();
           Statistics.smallStringStat();
           Statistics.fullStringStat();

        }catch (NoSuchElementException e){
            System.out.println("Строки отсутствуют");

        }
    }

    /**
     * Метод записывает целые числа и собирает статистку по ним, если они существуют
     * @param lines
     * @throws IOException
     */
public static void writeInts(List<String> lines) throws IOException {
    HashMap<String,Long> integers = new HashMap<>();
    String o = App.o;
    String p = App.p;
    Path path = Paths.get(o+ p +"integers.txt");

    if(lines.size()>0) {
        checkExistense(path);
        for (String line : lines) {
            Files.writeString(path,line + "\n", StandardOpenOption.APPEND);
            integers.put(line,Long.valueOf(line));
        }
    }
    else {
        System.out.println("Целые числа отсутствуют");
        return;
    }
    maxInt=  integers.values().stream().max(Comparator.comparing(Long::valueOf)).get();
    minInt= integers.values().stream().min(Comparator.comparing(Long::valueOf)).get();
    sumInt = integers.values().stream().collect(Collectors.summarizingLong(Long::valueOf)).getSum();
    avgInt = integers.values().stream().mapToLong(Long::valueOf).average().getAsDouble();
   Statistics.smallIntStat(lines);
 Statistics.fullIntStat();
}
    /**
     * Метод записывает дробные числа и собирает статистку по ним, если они существуют
     * @param lines
     * @throws IOException
     */
public static void writeFloats(List<String> lines) throws IOException {
        HashMap<String,Float> floats = new HashMap<>();
    String o = App.o;
    String p = App.p;
    Path path = Paths.get(o+ p +"floats.txt");

    if(lines.size()>0) {
        checkExistense(path);
        for (String line : lines) {
            Files.writeString(path,line + "\n", StandardOpenOption.APPEND);
            floats.put(line,Float.valueOf(line));
        }
    }
    else {
        System.out.println("Дробные числа отсутвуют");
        return;
    }
    maxFloat = floats.values().stream().max(Comparator.comparing(Float::valueOf)).get();
    minFloat = floats.values().stream().min(Comparator.comparing(Float::valueOf)).get();
    sumFloat = (float) floats.values().stream().mapToDouble(Float::valueOf).sum();
    avgFloat = (float) floats.values().stream().mapToDouble(Float::valueOf).average().getAsDouble();
   Statistics.smallFloatStat(lines);
   Statistics.fullFloatStat();
}

    /**
     * Метод проверяет существует ли уже файл для перезаписи
     * @param path
     * @throws IOException
     */
    private static void checkExistense(Path path) throws IOException {
        try {
            File file = new File(path.toUri());
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException exception){
            System.out.println();
        }
}


}
