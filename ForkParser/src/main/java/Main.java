
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;


public class Main {
    private static String urls = "C:\\Users\\admin\\IdeaProjects\\java_basics\\11_Multithreading\\ForkParser\\data\\links.txt";
    private  static  String skillboxLinks = "C:\\Users\\admin\\IdeaProjects\\java_basics\\11_Multithreading\\ForkParser\\data\\sklbx.txt";


    public static void main(String[] args) throws IOException {

        UrlStorage storage = new UrlStorage("https://skillbox.ru/");
        ForkJoinPool pool =  new ForkJoinPool(50);
        pool.execute(storage);
        while (!storage.isDone()) {
            System.out.println("Кол-во задач: " + pool.getQueuedTaskCount());
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        List<String> results = storage.join();
        pool.shutdown();
        System.out.println("Кол-во ссылок: " + results.size());
        FileWriter writer = new FileWriter(skillboxLinks);
        for (String link : results) {
            writer.write(link + System.lineSeparator());
        }
        writer.close();
    }
}

