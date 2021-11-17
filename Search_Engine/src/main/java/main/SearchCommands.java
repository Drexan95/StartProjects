package main;

import main.model.Page;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
@Component
public class SearchCommands {
    @Autowired
    PageResults pageResults;
    /**
     * Method return pages matching search query
     * @param query words to search
     * @param site search in particular site
     * @param limit page results
     * @param offset skip first n results
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws JSONException
     */
    public ResponseEntity<?> search(@RequestParam(name = "query") String query, @Nullable String site, @Nullable Integer limit, @Nullable Integer offset) throws SQLException, IOException, JSONException
    {

        SearchRequest request = new SearchRequestBuilder().setText(query).setSiteUrl(site).setLimit(limit).setOffset(offset).build();
        List<Page> results = pageResults.getResults(request);
//        results.forEach(System.out::println);
        if(query.equals("")){
            JSONObject response = new JSONObject();
            response.put("result","false");
            response.put("error","задан пустой поисковой запрос");
            return new ResponseEntity<>( response.toString(), HttpStatus.BAD_REQUEST);
        }
        if(results.size()>0){
            return new ResponseEntity<>(results,HttpStatus.OK);
        }
        else {
            JSONObject response = new JSONObject();
            response.put("result","false");
            response.put("error","Совпадения не найдены");
            return new ResponseEntity<>(response.toString(),HttpStatus.OK);
        }
    }
}
