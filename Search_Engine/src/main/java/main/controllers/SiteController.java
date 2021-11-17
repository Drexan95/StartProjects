package main.controllers;

import main.ManagementCommands;
import main.model.Statistics;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

@RequestMapping
@RestController
public class SiteController {
    @Autowired
    private Statistics statistics;

    @Autowired
    private ManagementCommands managementCommands;


    @GetMapping(value = "/startIndexing", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> startIndexing() throws SQLException, IOException, ParseException, JSONException {
        return managementCommands.startIndexing();
    }


    @GetMapping("/stopIndexing")
    public ResponseEntity<String> stopIndexing() throws JSONException {
        return managementCommands.stopIndexing();
    }


    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return managementCommands.getStatistics();

    }

}