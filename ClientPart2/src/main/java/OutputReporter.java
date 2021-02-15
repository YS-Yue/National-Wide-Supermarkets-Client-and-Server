import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The OutputReporter will calculate the statistic, show the result in the console,
 * and write the output csv file.
 */
public class OutputReporter {
    private final long wallStart;
    private final long wallEnd;
    private final List<RequestRecord> allResRecords;
    private final int maxThreads;
    private final int totalSuccess;
    private final int totalFailure;
    private final List<Long> latencies;

    public OutputReporter(long wallStart, long wallEnd, List<RequestRecord> allResRecords, int maxThreads,
                          int totalSuccess, int totalFailure){
        this.wallStart= wallStart;
        this.wallEnd = wallEnd;
        this.allResRecords = allResRecords;
        this.maxThreads = maxThreads;
        this.totalSuccess = totalSuccess;
        this.totalFailure = totalFailure;
        latencies = new ArrayList<>();
        for (RequestRecord requestRecord : allResRecords){
            latencies.add(requestRecord.getLatency());
        }
    }

    public void report() {
        Collections.sort(latencies);
        long sumResponseTime = latencies.stream().mapToLong(Long::longValue).sum();
        double meanResponseTime = (double)sumResponseTime/latencies.size();
        long medianResponseTime = latencies.get(latencies.size()/2 - 1);
        double wallTime = (double)(wallEnd - wallStart)/1000;
        double throughput = (totalSuccess + totalFailure)/wallTime;
        long p99ResponseTime = latencies.get(latencies.size()*99/100 - 1);
        long maxResponseTime = latencies.get(latencies.size() - 1);

        System.out.println("The max thread: " + maxThreads);
        System.out.println("The total number of successful requests sent: " + totalSuccess);
        System.out.println("The total number of unsuccessful requests sent: " + totalFailure);
        System.out.println("The wall time (in second): " + wallTime);
        System.out.println("The throughput (requests per second): " + throughput);
        System.out.println("The mean response time: " + meanResponseTime);
        System.out.println("The median response time: " + medianResponseTime);
        System.out.println("The p99(99th percentile) response time: " + p99ResponseTime);
        System.out.println("The max response time: " + maxResponseTime);
    }

    public void writeToCsv(String outPutPath) {
        try (PrintWriter output = new PrintWriter(outPutPath)) {
            output.println("start time,request type (ie POST),latency,response code");
            for (RequestRecord record : allResRecords) {
                output.println(record.stringInCsv());
            }
            System.out.println( allResRecords.size() +" records written to csv file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Failed to write '" + outPutPath + "'.");
        }
    }
}
