import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class LogExtractor {

    private static String sLogDir = "", sOutDir = "", sDelimiter = "", sSearchString, sBeforeTimestamp;

    private static ArrayList<LogEntry> entryArrayList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            System.out.println("INFO: Starting program, reading config file...");
            // read config file
            readConfig();

            // get list of files and process them
            File fLogDir = new File(sLogDir);
            System.out.println("INFO: Start reading each file...");
            for (final File fileEntry : Objects.requireNonNull(fLogDir.listFiles())) {
                if (!fileEntry.isDirectory()) {
//                    System.out.println(fileEntry.getName());
//                    System.out.println(fileEntry.getCanonicalPath());
                    processLogFile(fileEntry);

                }
                /*else { // don't need directories (now?)
                    //listFilesForFolder(fileEntry);
                }*/
            }

/*
            for (LogEntry entry : entryArrayList) {
                System.out.println(entry.getsFileName());
                System.out.println(entry.getTranData());
            }
*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read config file
     */
    private static void readConfig() {
        try {
            File fileDir = new File("config.cfg");
            String paramName, paramValue;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String str;
            // reading file
            while ((str = in.readLine()) != null) {
                if (!str.startsWith("#") && !str.trim().equals("")) { // это не комментарий и не пустая строка
                    str = str.trim();
                    if (str.contains("=")) {
                        paramName = str.split("=")[0].trim();
                        paramValue = str.split("=")[1].trim();

                        switch (paramName) {
                            case "LOG_DIR":
                                sLogDir = paramValue;
                                break;
                            case "OUT_DIR":
                                sOutDir = paramValue;
                                break;
                            case "DELIMITER":
                                sDelimiter = paramValue;
                                break;
                            case "SEARCH_STRING":
                                sSearchString = paramValue;
                                break;
                            case "BEFORE_TIMESTAMP":
                                sBeforeTimestamp = paramValue;
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // checking
        if (sLogDir.length() == 0) {
            System.out.println("ERROR: No log dir!");
            System.exit(1);
        } else {
            System.out.println("INFO: Log dir is " + sLogDir);
        }

        if (sOutDir.length() == 0) {
            System.out.println("ERROR: No out dir!");
            System.exit(1);
        } else {
            System.out.println("INFO: Out dir is " + sOutDir);
        }

        if (sDelimiter.length() == 0) {
            System.out.println("ERROR: No delimiter!");
            System.exit(1);
        } else {
            System.out.println("INFO: Delimiter is " + sDelimiter);
        }
        if (sSearchString.length() == 0) {
            System.out.println("ERROR: No search string!");
            System.exit(1);
        } else {
            System.out.println("INFO: Search string is " + sSearchString);
        }
        if (sBeforeTimestamp.length() == 0) {
            System.out.println("ERROR: No prefix string!");
            System.exit(1);
        } else {
            System.out.println("INFO: Prefix string is " + sBeforeTimestamp);
        }

    }

    /**
     * Processing log file
     *
     * @param fileEntryForProcess log file
     */
    private static void processLogFile(File fileEntryForProcess) {
        try {
            // start reading file
            System.out.println("INFO: Analyze file " + fileEntryForProcess);
            // start reading
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileEntryForProcess), "UTF8"));

            String str;

            // reading file
            boolean isFirstAfterDelimiter = false;
            LogEntry logEntry = new LogEntry(fileEntryForProcess.getName());

            while ((str = in.readLine()) != null) {
                // if line starts with delimiter
                if (str.startsWith(sDelimiter)) {
                    // check if save needed and start new entry
                    if (logEntry.isForSave()) entryArrayList.add(logEntry);
                    logEntry = new LogEntry(fileEntryForProcess.getName());
                    isFirstAfterDelimiter = true;
                }
                // add string
                logEntry.appendTranData(str + "\r\n");

                // if string starts with prefix and it is first after delimiter
                if (isFirstAfterDelimiter && str.startsWith(sBeforeTimestamp)) {
                    isFirstAfterDelimiter = false;
                    // extract timestamp
                    //>>1545643874.287122:
                    logEntry.setlTimestamp(Long.valueOf(str.substring(2, 19).replace(".", "")));
                }

                // if string contains search string - fill the flag
                if (str.contains(sSearchString)) logEntry.setForSave(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
