package Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileReader {
    public static List<String> words = new ArrayList<>();
    public static List<String> ints = new ArrayList<>();
    public static List<String> floats = new ArrayList<>();

    /**
     * Метод считывает строку и определяет к какому типу она относится
     * @param lines
     */
    public static void readLine(List<String> lines)  {

        for (String string : lines) {
            if (isNumeric(string)) {
                boolean b = isInteger(string) ? ints.add(string) : floats.add(string);
            } else {
                words.add(string);
            }
        }
    }

    /**
     * Метод сообщает записыать обработанные данные в файл
     * @throws IOException
     */
    public static void writeLines() throws IOException {
    FileWriter.writeString(words);
    FileWriter.writeFloats(floats);
    FileWriter.writeInts(ints);

    }


    public static boolean isNumeric(String string){
    Pattern pattern = Pattern.compile("-?\\d+?(\\.\\d+)?[eE]?[+-]?\\d+");
  return pattern.matcher(string).matches();
}
public static boolean isInteger(String string){
    Pattern pattern = Pattern.compile("\\d+");
    return pattern.matcher(string).matches();
 }
}
