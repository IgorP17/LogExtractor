public class LogEntry {

    private long lTimestamp; // wo dot
    private String sFileName;
    private StringBuffer sTranData;
    private boolean isForSave = false;

    // Constructor
    LogEntry (String sFileName){
        this.sFileName = sFileName;
        this.sTranData = new StringBuffer();
    }

    // Append data
    public void appendTranData(String s){
        sTranData.append(s);
    }

    // get timestamp
    public long getlTimestamp() {
        return lTimestamp;
    }

    // get file name
    public String getsFileName() {
        return sFileName;
    }

    // set timestamp
    public void setlTimestamp(long lTimestamp) {
        this.lTimestamp = lTimestamp;
    }

    // For save getter
    public boolean isForSave() {
        return isForSave;
    }
    // For save setter
    public void setForSave(boolean forSave) {
        isForSave = forSave;
    }
}
