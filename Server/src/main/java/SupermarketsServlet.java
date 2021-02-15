import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/supermarkets")
public class SupermarketsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String urlPath = request.getPathInfo();
        response.setContentType("text/html");

        // check we have a URL
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.getWriter().write("missing parameters");
            return;
        }


        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.getWriter().write("invalid url path");
            return;
        }

        StringBuilder jsonBody = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jsonBody.append(line);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.getWriter().write("failed parsing json request body");
            return;
        }

        String jsonString = jsonBody.toString();

        if (!isBodyValid(jsonString)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("invalid request body");
            return;
            }

        response.setStatus(HttpServletResponse.SC_CREATED);
//        response.getWriter().write("Successful parse");
    }

    // check whether the url is valid
    private boolean isUrlValid(String[] urlParts) {
        return urlParts.length == 7 &&
                urlParts[1].equals("purchase") && isInteger(urlParts[2]) &&
                urlParts[3].equals("customer") && isInteger(urlParts[4]) &&
                urlParts[5].equals("date");
    }

    // check whether the request body is valid
    private boolean isBodyValid(String jsonString) {
        if (jsonString.isEmpty()){
            return false;
        }
        Gson gson = new Gson();
        try {
            Purchase purchase = gson.fromJson(jsonString, Purchase.class);
            List<PurchaseItems> itemsList = purchase.getItems();
            if (itemsList == null || itemsList.size() == 0) {
                return false;
            }
            for (PurchaseItems item : itemsList) {
                String itemID = item.getItemID();
                if (itemID == null || itemID.isEmpty()) {
                    return false;
                }
                Integer num_item = item.getNumberOfItems();
                if (num_item == null) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // check whether a string can be convert to an integer
    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}
