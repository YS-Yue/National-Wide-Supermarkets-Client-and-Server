/**
 * Represents the record for a single purchase request.
 */
public class RequestRecord {
    private final String startTime;
    private final String reqType;
    private final Long latency;
    private final Integer resCode;

    public RequestRecord(String startTime, String reqType, Long latency, Integer resCode) {
        this.startTime = startTime;
        this.reqType = reqType;
        this.latency = latency;
        this.resCode = resCode;
    }

    public String getStartTime(){
        return startTime;
    }

    public String getReqType(){
        return reqType;
    }

    public Long getLatency(){
        return latency;
    }

    public Integer getResCode(){
        return resCode;
    }

    @Override
    public String toString(){
        return "RequestRecord{" +
                "startTime='" + startTime + '\'' +
                ", reqType='" + reqType + '\'' +
                ", latency=" + latency +
                ", resCode=" + resCode +
                '}';
    }

    /**
     * The format of string line in the csv file.
     */
    public String stringInCsv() {
        return startTime + "," + reqType + "," + latency + "," + resCode;
    }
}
