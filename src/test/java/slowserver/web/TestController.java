package slowserver.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Controller
public class TestController {

    @RequestMapping("/dummy")
    @ResponseBody
    public String dummy(@RequestBody String body) {
        return "dummy" + body;
    }

    @RequestMapping("/dump")
    @ResponseBody
    public String dump(@RequestBody String body, WebRequest request) {
        final List<String> result = new ArrayList<String>();
        final Iterator<String> it = request.getHeaderNames();
        while (it.hasNext()) {
            final String headerName = it.next();
            result.add(headerName + ": " + asList(request.getHeaderValues(headerName)));
        }
        result.add("body: " + body);
        return collectionToDelimitedString(result, "\n");
    }
}
