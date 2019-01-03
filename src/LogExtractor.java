import java.io.*;
import java.util.*;

public class LogExtractor {

    private static ArrayList<LogEntry> entryArrayList = new ArrayList<>();

    private static Config config = new Config("config.properties");

    public static void main(String[] args) {

        try {
            System.out.println("INFO: Starting program...");

            // get list of files and process them
            File fLogDir = new File(config.getValue("LOG_DIR").get(0));
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

            // Sort ArrayList by Timestamp
            System.out.println("INFO: Sorting entries...");
            entryArrayList.sort(Comparator.comparing(LogEntry::getTimestamp));

            // Write to file
            System.out.println("INFO: Writing to file " + config.getValue("RESULT_FILE").get(0));
            System.out.println("INFO: Writing to file " + config.getValue("RESULT_FILE_MODULES").get(0));
            PrintWriter writer = new PrintWriter(config.getValue("RESULT_FILE").get(0), "UTF-8");
            PrintWriter writerBrief = new PrintWriter(config.getValue("RESULT_FILE_MODULES").get(0), "UTF-8");

            for (LogEntry entry : entryArrayList) {
//                System.out.println(entry.getsFileName());
//                System.out.println(entry.getTranData());
                writer.println();
                writer.println("\t\t"
                        + config.getValue("SEPARATOR").get(0)
                        + " File: "
                        + entry.getFileName()
                        + " "
                        + config.getValue("SEPARATOR").get(0)
                        + " Found by: "
                        + entry.getStringSetFoundBy().toString()
                        + " "
                        + config.getValue("SEPARATOR").get(0));
                writer.println();
                writer.println(entry.getTranData());

                writerBrief.println("FILE: "
                        + entry.getFileName()
                        + "\tFound by: "
                        + entry.getStringSetFoundBy().toString()
                        + "\t Timestamp: "
                        + entry.getTimestamp() / 1000000L
                        + "."
                        + entry.getTimestamp() % 1000000L
                        + System.lineSeparator());
            }
            writer.close();
            writerBrief.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processing log file
     *
     * @param fileEntryForProcess log file
     */
    private static void processLogFile(File fileEntryForProcess) {
        try {
            // IF ignore
            for (String ignore : config.getValue("IGNORED_FILES_CONTAINS")) {
                if (fileEntryForProcess.toString().contains(ignore)) {
                    System.out.println("WARNING: Ignore file " + fileEntryForProcess);
                    return;
                }
            }

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
                if (str.startsWith(config.getValue("DELIMITER").get(0))) {
                    // check if save needed and start new entry
                    if (logEntry.isForSave()) entryArrayList.add(logEntry);
                    logEntry = new LogEntry(fileEntryForProcess.getName());
                    isFirstAfterDelimiter = true;
                }
                // add string
                logEntry.appendTranData(str + "\r\n");

                // if string starts with prefix and it is first after delimiter
                if (isFirstAfterDelimiter
                        && str.startsWith(config.getValue("BEFORE_TIMESTAMP").get(0))) {
                    isFirstAfterDelimiter = false;
                    // extract timestamp
                    //>>1545643874.287122:
                    logEntry.setTimestamp(Long.valueOf(str.substring(2, 19).replace(".", "")));
                }

                // if string contains search string - fill the flag and add found by data
                for (String s : config.getValue("SEARCH_STRING")) {
                    if (str.contains(s)) {
                        logEntry.setForSave(true);
                        logEntry.addSetFoundBy(s);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
