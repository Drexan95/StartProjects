package UrlService;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    private static final List<String> UNSUPPORTED_TYPES = Arrays.asList("jpg", "pdf", "png", "gif", "zip",
            "tar", "jar", "gz", "svg", "ppt", "pptx");

    public static String findText(String text) {
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile("[а-яёА-ЯЁ]+");
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            result.append(" ").append(m.group());
        }

        return result.toString();
    }
    public  static  boolean skip(String url,String path,String root) {
        boolean skip = false;
        if (url.contains("#") || url.contains("?")) {
            skip = true;
        }

        if (url.trim().length() == 0) {
            skip = true;
        }

        if (url.equalsIgnoreCase(root)) {
            skip = true;
        }

        if (!isSupportedType(url)) {
            skip = true;
        }
        return skip;
    }
    public static boolean isInternalLink(String ulr,String path) {
        return ulr.startsWith(path);
    }

    public static float calculateFieldWeight(Element element){
        String fieldTag = element.tagName();
        float weight;
        switch (fieldTag){
            case("head") :
                weight = 1;
            break;
            case ("body"): weight = 0.8f;
            break;
            case ("div"): weight = 0.6f;
            break;
            default:weight = 0.4f;
            break;
        }
        return weight;
    }

    public static boolean isSupportedType(String url) {

        if (url == null) {
            return false;
        }
        if (url.isEmpty()) {
            return true;
        }
        String ext = url.substring(url.lastIndexOf(".") + 1);
        if (ext == null) {
            return true;
        }
        return (!UNSUPPORTED_TYPES.contains(ext));
    }
   public static boolean elementIsRedundant(Element element,String text) {

        if (text == null || text.equals("") ){
            return true;
        }

        if (element.hasAttr("*")) {
            return false;
        }
        if (element.hasText()) {
            return false;
        }
        if (element.childNodeSize() == 0 || element.childNodes().isEmpty()) {
            return true;
        }
        List<org.jsoup.nodes.Node> children = element.childNodes();
        for (Node child : children) {
            if (!child.toString().isEmpty() && !child.toString().matches("\\s*")) {
                return false;
            }
        }

        return true;
   }

    public static JSONObject jsonStartIndexing(boolean status) throws JSONException
    {
        JSONObject response = new JSONObject();
        if (!status) {
            response.put("result", "true");
            return response;
        } else
            response.put("result", "false");
        response.put("error", "индексация уже запущена");
        return response;

    }
    public static JSONObject jsonStopIndexing(boolean status) throws JSONException
    {
        JSONObject response = new JSONObject();
        if(!status){
            response.put("result","true");
            return response;
        }
        else response.put("result","false");
        response.put("error","индексация не запущена");
        return response;
    }
    public static  String slashAtEnd(String siteUrl){
        if(!siteUrl.endsWith("/")){
            siteUrl = siteUrl+"/";
        }
        return siteUrl;
    }
}
