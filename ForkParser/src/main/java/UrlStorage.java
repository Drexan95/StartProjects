import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

import java.util.concurrent.RecursiveTask;

public class UrlStorage extends RecursiveTask<List<String>> {
    private String path ;
    private  String url;
    private volatile List<String> urls;
    private List<UrlStorage> tasks;
    private  Set<String> visitedInternalLink = Collections.synchronizedSet(new HashSet<>());
    private volatile Vector<String> mapLinks;

    private static final List<String> UNSUPPORTED_TYPES = Arrays.asList("jpg", "pdf", "png", "gif", "zip",
            "tar", "jar", "gz", "svg", "ppt", "pptx");

    public UrlStorage(String path){
        this.path = path;
    }

    public UrlStorage(String path,Set<String> visitedInternalLink){
        this.path = path;
        this.visitedInternalLink = visitedInternalLink;
    }

    @Override
    protected Vector<String> compute() {
         urls  = new ArrayList<>();//для задач
         tasks = new ArrayList<>();//для посещенных
         mapLinks = new Vector<>();//карта сайта

        try {
            Document doc = Jsoup.connect(path).get();
            Thread.sleep(1500);
            Elements elements = doc.select("a[abs:href^=" + path + "]");
            for (Element element : elements) {
                url = element.absUrl("href");

                if (visitedInternalLink.contains(url)||skip(url) || !isInternalLink(url) ) {
                    continue;
                }
                urls.add(url);
                int LEVEL_COUNTER = 3;
                mapLinks.add("\t".repeat((int) (getLinkLevel(url)- LEVEL_COUNTER))+url+"\n");
                visitedInternalLink.add(url);

            }

            for (String url : urls) {
                UrlStorage task = new UrlStorage(url,visitedInternalLink);
                task.fork();
                tasks.add(task);
            }

        } catch (IOException | InterruptedException |NullPointerException exception) {
            exception.printStackTrace();
        }
        joinTasks();
        //=======================//

        return mapLinks;
    }

    private void joinTasks(){
        for(UrlStorage task: tasks){
                mapLinks.addAll(task.join());
        }

    }

    private  boolean skip(String linkUrl){
        boolean skip = false;
        if(linkUrl.contains("#")||linkUrl.contains("?")){
            skip = true;
        }
        //Пустая ссылка
        if(linkUrl.trim().length() == 0){
            skip = true;
        }
        //Ссылка на главную
        if(url.equalsIgnoreCase(path)){
            skip = true;
        }
        //Ссылка на файлы
        if(!isSupportedType(url)){
            skip = true;
        }
        return  skip;
    }

    private boolean isSupportedType(String url) {

        if (url == null) {
            return false;
        }
        if (url.isEmpty()) {
            return true;
        }
        String ext = url.substring(url.lastIndexOf(".")+1);
        if (ext == null) {
            return true;
        }
       return (!UNSUPPORTED_TYPES.contains(ext));
    }

    private  boolean isInternalLink(String  ulr){
        return ulr.startsWith(path);
    }


    public Long getLinkLevel(String link) {
        long level = link.chars().filter(ch -> ch == '/').count();
        return level;
    }
}
