package fyp.acronym;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static fyp.acronym.Acronym.*;
import static fyp.acronym.multithread_subsequence.generateSubsequences;
import static fyp.acronym.multithread_subsequence.search;


/**
 * @author vanting
 */
@Controller
public class HtmlController {

    // demo the use of the parameters: path and method
    // http://localhost/spring/web/welcome1
    @RequestMapping(path = "/welcome1", method = RequestMethod.GET)
    public String welcomeAsHtmlTemplate() {
        return "welcome";       // treat as the view name by default
    }

    // demo to pass multiple arguments
    // http://localhost/spring/web/welcome2
    @RequestMapping(value = {"/welcome2", "/welcome3"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody               // ask the view resolver to treat the returned text as response body
    public String welcomeAsHtmlText() {

        // """ (three double quote marks): text block syntax is introduced in JDK 15
        return """
                <!DOCTYPE HTML>
                <html>
                     <head>
                         <title>Welcome Page</title>
                         <meta charset="UTF-8">
                         <meta name="viewport" content="width=device-width, initial-scale=1.0">
                     </head>
                     <body>
                         <h1>Welcome!</h1>
                         <p>This page is generated without a HTML template.</p>
                     </body>
                </html>
                """;
    }

    // demo to use request params as filter
    // http://localhost/spring/web/welcome4?id=4&key=5
    @RequestMapping(
            path = "/welcome4",
            method = RequestMethod.GET,
            params = {"id=4", "key=5"})
    // params = {"id"}      : id must present
    // params = {"!id"}     : id must not present
    // params = {"id!=4"}   : id must not equal to 4
    @ResponseBody
    public String welcomeAsHtmlTextWithParams() {

        return """
                <!DOCTYPE HTML>
                <html>
                     <head>
                         <title>Welcome Page</title>
                         <meta charset="UTF-8">
                         <meta name="viewport" content="width=device-width, initial-scale=1.0">
                     </head>
                     <body>
                         <h1>Welcome!</h1>
                         <p>This page requires two HTTP request parameters: id=4 and key=5.</p>
                     </body>
                </html>
                """;
    }


    // curl -i http://localhost/spring/web/acronym?text=City%20University%20Of%20Hong%20Kong
    @RequestMapping(value = {"/acronym"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody               // ask the view resolver to treat the returned text as response body
    public String acronym_website(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "min", required = false) String min,
            @RequestParam(name = "max", required = false) String max
    ) {
        records.clear();
        if (text != null) {
            // Do something with the 'text' parameter
            input = text;
            try {
                if (min != null) {
                    acronym_min_length = Integer.parseInt(min);
                }
            } catch (Exception e) {
            }
            try {
                if (max != null) {
                    acronym_max_length = Integer.parseInt(max);
                }
            } catch (Exception e) {
            }


            findAcronyms();
            if (records.isEmpty())
                return "";
            // Step 6: Print all record
            String jsonString = "{";


            for (Record record : records) {

                jsonString += "\"acronym\":\"" + record.getField1() + "\",";
                jsonString += "\"sentence\":\"" + record.getField2() + "\",";
                jsonString += "\"weight\":" + record.getIntegerValue();
                jsonString += "},{";


                //System.out.println(jsonString);
            }
            jsonString = jsonString.substring(0, jsonString.length() - 1);
            jsonString = jsonString.substring(0, jsonString.length() - 1);
            return "[" + jsonString + "]";
        }
        return "";

    }


    // curl -i http://localhost/spring/web/acronym_sequence?text=City%20University%20Of%20Hong%20Kong
    @RequestMapping(value = {"/acronym_sequence"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody               // ask the view resolver to treat the returned text as response body
    public String acronym_sequence_website(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "min", required = false) String min,
            @RequestParam(name = "max", required = false) String max
    ) {
        records.clear();
        List<String> subsequences  = new ArrayList<>();;
        if (text != null) {
            // Do something with the 'text' parameter
            input = text;
            try {
                if (min != null) {
                    acronym_min_length = Integer.parseInt(min);
                }
            } catch (Exception e) {
            }
            try {
                if (max != null) {
                    acronym_max_length = Integer.parseInt(max);
                }
            } catch (Exception e) {
            }


            subsequences = generateSubsequences(input.toLowerCase().replaceAll("[^A-Za-z]", ""));
            for (String each: subsequences) {
                if(search(each) == true){
                    String ProcessedSentence = capitalizeSubstring(input.toLowerCase(), each);
                    records.add(new Record(each, ProcessedSentence, Integer.sum(calculateScore(ProcessedSentence), checkOnList(each))));
            }
            }



            if (records.isEmpty())
                return "";

            Collections.sort(records, new Comparator<Record>() {
                @Override
                public int compare(Record record1, Record record2) {
                    // Compare based on the integer field , descending order
                    //return Integer.compare(record1.getIntegerValue(), record2.getIntegerValue());
                    return Integer.compare(record2.getIntegerValue(), record1.getIntegerValue());
                }
            });

            String jsonString = "{";


            for (Record record : records) {

                jsonString += "\"acronym\":\"" + record.getField1() + "\",";
                jsonString += "\"sentence\":\"" + record.getField2() + "\",";
                jsonString += "\"weight\":" + record.getIntegerValue();
                jsonString += "},{";


                //System.out.println(jsonString);
            }
            jsonString = jsonString.substring(0, jsonString.length() - 1);
            jsonString = jsonString.substring(0, jsonString.length() - 1);
            return "[" + jsonString + "]";
        }
        return "";

    }

}
