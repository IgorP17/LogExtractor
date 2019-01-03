import java.util.HashSet;

public class LogEntry {

    private long lTimestamp; // wo dot
    private String sFileName;
    private StringBuffer sTranData;
    private boolean isForSave = false;
    private HashSet<String> stringSetFoundBy = new HashSet<>();

    // Constructor
    LogEntry (String sFileName){
        this.sFileName = sFileName;
        this.sTranData = new StringBuffer();
    }

    // Append data
    public void appendTranData(String s){
        this.sTranData.append(s);
    }

    // get data
    public StringBuffer getTranData(){
        return sTranData;
    }

    // get timestamp
    public long getTimestamp() {
        return lTimestamp;
    }

    // get file name
    public String getFileName() {
        return sFileName;
    }

    // set timestamp
    public void setTimestamp(long lTimestamp) {
        this.lTimestamp = lTimestamp;
    }

    // For save getter
    public boolean isForSave() {
        return isForSave;
    }
    // For save setter
    public void setForSave(boolean forSave) {
        this.isForSave = forSave;
    }


    public HashSet<String> getStringSetFoundBy() {
        return stringSetFoundBy;
    }

    public void addSetFoundBy(String s) {
        this.stringSetFoundBy.add(s);
    }
}
