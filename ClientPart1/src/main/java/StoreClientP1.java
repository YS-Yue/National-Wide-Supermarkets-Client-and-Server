import io.swagger.client.*;
import io.swagger.client.model.*;
import io.swagger.client.api.PurchaseApi;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Represents a client that generates and sends synthetic item purchases to a server in the cloud.
 */
public class StoreClientP1 implements Runnable{
    private final ApiClient store;
    private final Integer storeID;
    private final Integer customersPerStore;
    private final Integer maxItemID;
    private final Integer numPurchasesPerHour;
    private final Integer numItemsPerPurchase;
    private final String date;
    private Integer successCount = 0;
    private Integer failureCount = 0;

    private static CountDownLatch waitForCentral;
    private static CountDownLatch waitForWest;
    private static CountDownLatch waitForComplete;
    private static Integer totalSuccess = 0;
    private static Integer totalFailure = 0;

    private static final String URL_DEFAULT = "http://100.24.52.187:8080/Server_war/supermarkets";

    /**
     * Constructs a store that sends purchase order to the supermarket 9 hours in a single day.
     */
    public StoreClientP1(Integer storeID, Integer customersPerStore, Integer maxItemID,
                         Integer numPurchasesPerHour, Integer numItemsPerPurchase,
                         String date, String url) {
        this.storeID = storeID;
        this.customersPerStore = customersPerStore;
        this.maxItemID = maxItemID;
        this.numPurchasesPerHour = numPurchasesPerHour;
        this.numItemsPerPurchase = numItemsPerPurchase;
        this.date = date;

        this.store = new ApiClient();
        store.setBasePath(url);
    }

    /**
     * Represents a store's behavior from open to close.
     * @see Thread#run()
     */
    @Override
    public void run(){
        // A store sends purchase order 60 times per hour for 9 hours.
        for (int i = 0; i < numPurchasesPerHour*9; i++) {
            sendPurchaseOrder();
            // If any store has sent purchase request 180 times (60 times * 3 hours),
            // countDown() the countDown latch set for the stores in Central zone.
            if (i == numPurchasesPerHour*3 - 1) {
                waitForCentral.countDown();
            }
            // If any store has sent purchase request 300 times (60 times * 5 hours),
            // countDown() the countDown latch set for the stores in West zone.
            if (i == numPurchasesPerHour*5 - 1) {
                waitForWest.countDown();
            }
        }

        // After 9 hours (sent 540 purchase orders), report the counts of successful and unsuccessful requests
        // to the static class fields totalSuccess and totalFailure respectively.
        reportSuccessCount(this.successCount);
        reportFailureCount(this.failureCount);

        // countDown() the countDown latch set for all stores close.
        waitForComplete.countDown();
    }

    /**
     * Sends a purchase request to the server, then records the response status.
     */
    private void sendPurchaseOrder() {
        // Generates a Purchase object.
        PurchaseApi apiInstance = new PurchaseApi(this.store);
        Purchase body = new Purchase();

        // Generate a random customerID
        Integer customerID = 1000 * this.storeID + new Random().nextInt(this.customersPerStore);

        // Generates PurchaseItems objects and add them to the Purchase.
        for (int i = 0; i < this.numItemsPerPurchase; i++) {
            String itemID = String.valueOf(new Random().nextInt(this.maxItemID));
            PurchaseItems item = new PurchaseItems().itemID(itemID).numberOfItems(1);
            body.addItemsItem(item);
        }

        // Sends the request to the server and records the response.
        try {
            int resStatusCode = apiInstance.newPurchaseWithHttpInfo(body, this.storeID, customerID, this.date)
                    .getStatusCode();
            if (Integer.toString(resStatusCode).equals("201")) {
                this.successCount++;
            } else {
                this.failureCount++;
                System.err.println("Request Fail With Status Code" + resStatusCode);
            }
        } catch (ApiException e) {
            System.err.println("Exception when calling PurchaseApi#newPurchase in sendPurchaseOrder()");
            e.printStackTrace();
        }
    }

    /**
     * Reports the store's counts of successful requests in a day to the static class fields totalSuccess.
     * @param successCount the store's counts of successful requests in a day
     */
    public static synchronized void reportSuccessCount(Integer successCount) {
        totalSuccess += successCount;
    }

    /**
     * Reports the store's counts of unsuccessful requests in a day to the static class fields totalFailure.
     * @param failureCount the store's counts of unsuccessful requests in a day
     */
    public static synchronized void reportFailureCount(Integer failureCount) {
        totalFailure += failureCount;
    }


    public static void main(String[] args) throws InterruptedException{

        try (InputStream inputStream = new FileInputStream("client1.properties")) {
            // Load parameters from the properties file.
            Properties properties = new Properties();
            properties.load(inputStream);

            // If not provided, the maxStores(max threads) will be set as default 128
            int maxStores = Integer.parseInt(properties.getProperty("maxStores", "128"));

            // If not provided, the customersPerStore will be set as default 1000
            int customersPerStore = Integer.parseInt(properties.getProperty("customersPerStore", "1000"));

            // If not provided, the maxItemID will be set as default 100000
            int maxItemID = Integer.parseInt(properties.getProperty("maxItemID", "100000"));

            //  If not provided, the numPurchasesPerHour(using as a proxy for time) will be set as default 60
            int numPurchasesPerHour = Integer.parseInt(properties.getProperty("numPurchasesPerHour", "60"));

            // If not provided, the numItemsPerPurchase will be set as default 5
            int numItemsPerPurchase = Integer.parseInt(properties.getProperty("numItemsPerPurchase", "5"));

            String date = properties.getProperty("date", "20210101");

            // The url of the server - SupermarketsServlet
            String url = properties.getProperty("url", URL_DEFAULT);

            // The CountdownLatch is set as 1 - after any store thread has sent 3 hours of purchases,
            // launch another (maxStores/4) threads - the central phase.
            waitForCentral = new CountDownLatch(1);

            // The CountdownLatch is set as 1 - after any store thread has sent 5 hours of purchases,
            // launch the remaining (maxStores/2) threads - the west phase.
            waitForWest = new CountDownLatch(1);

            // The CountDownLatch is set for all threads completion - all stores closed.
            waitForComplete = new CountDownLatch(maxStores);

            // record the start time
            long wallStart = System.currentTimeMillis();

            // phase 1
            System.out.println("---- Open stores in East ----");
            for (int i = 0; i < maxStores / 4; i++) {
                Integer storeID = i + 1;
                StoreClientP1 storeClient = new StoreClientP1(storeID, customersPerStore, maxItemID,
                        numPurchasesPerHour, numItemsPerPurchase, date, url);
                Thread thread = new Thread(storeClient);
                thread.start();
            }

            System.out.println("---- Waiting 3 hours for Central ----");
            waitForCentral.await();

            // phase 2
            System.out.println("---- Open stores in Central ----");
            for (int i = 0; i < maxStores / 4; i++) {
                Integer storeID = i + 1 + maxStores / 4;
                StoreClientP1 storeClient = new StoreClientP1(storeID, customersPerStore, maxItemID,
                        numPurchasesPerHour, numItemsPerPurchase, date, url);
                Thread thread = new Thread(storeClient);
                thread.start();
            }

            System.out.println("---- Waiting 2 hours for West ----");
            waitForWest.await();

            //phase 3
            System.out.println("---- Open stores in West ----");
            for (int i = 0; i < maxStores / 2; i++) {
                Integer storeID = i + 1 + maxStores / 2;
                StoreClientP1 storeClient = new StoreClientP1(storeID, customersPerStore, maxItemID,
                        numPurchasesPerHour, numItemsPerPurchase, date, url);
                Thread thread = new Thread(storeClient);
                thread.start();
            }

            System.out.println("---- Waiting for all stores closed ----");
            waitForComplete.await();

            System.out.println("---- All stores closed ----");
            long wallEnd = System.currentTimeMillis();

            double wallTime = (double) (wallEnd - wallStart) / 1000;
            double throughput = (totalSuccess + totalFailure) / wallTime;

            System.out.println("The max thread: " + maxStores);
            System.out.println("The total number of successful requests sent: " + totalSuccess);
            System.out.println("The total number of unsuccessful requests sent: " + totalFailure);
            System.out.println("The wall time (in second): " + wallTime);
            System.out.println("The throughput (requests per second): " + throughput);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to find properties file.");
        }
    }
}
