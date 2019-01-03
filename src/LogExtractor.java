import java.io.*;
import java.util.*;

public class LogExtractor {

    private static String
            sLogDir = "",
            sResultFile = "",
            sResultFileModules = "",
            sDelimiter = "",
            sBeforeTimestamp = "",
            sFileSeparator = "";

    private static List<String> lIgnoredFiles = new ArrayList<>();

    private static List<String> lSearchStrings = new ArrayList<>();

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

            // Sort ArrayList by Timestamp
            System.out.println("INFO: Sorting entries...");
            entryArrayList.sort(Comparator.comparing(LogEntry::getTimestamp));

            // Write to file
            System.out.println("INFO: Writing to file " + sResultFile);
            System.out.println("INFO: Writing to file " + sResultFileModules);
            PrintWriter writer = new PrintWriter(sResultFile, "UTF-8");
            PrintWriter writerBrief = new PrintWriter(sResultFileModules, "UTF-8");

            for (LogEntry entry : entryArrayList) {
//                System.out.println(entry.getsFileName());
//                System.out.println(entry.getTranData());
                writer.println();
                writer.println("\t\t"
                        + sFileSeparator
                        + " File: "
                        + entry.getFileName()
                        + " "
                        + sFileSeparator
                        + " Found by: "
                        + entry.getStringSetFoundBy().toString()
                        + " "
                        + sFileSeparator);
                writer.println();
                writer.println(entry.getTranData());

                writerBrief.println("FILE: "
                        + entry.getFileName()
                        + "\tFound by "
                        + entry.getStringSetFoundBy().toString()
                        + System.lineSeparator());
            }
            writer.close();
            writerBrief.close();

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
                            case "RESULT_FILE":
                                sResultFile = paramValue;
                                break;
                            case "RESULT_FILE_MODULES":
                                sResultFileModules = paramValue;
                                break;
                            case "DELIMITER":
                                sDelimiter = paramValue;
                                break;
                            case "SEARCH_STRING":
                                lSearchStrings = Arrays.asList(trimAll(paramValue.split(";")));
                                break;
                            case "BEFORE_TIMESTAMP":
                                sBeforeTimestamp = paramValue;
                                break;
                            case "SEPARATOR":
                                sFileSeparator = paramValue;
                                break;
                            case "IGNORED_FILES_CONTANS":
                                lIgnoredFiles = Arrays.asList(trimAll(paramValue.split(";")));
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

        if (sResultFile.length() == 0) {
            System.out.println("ERROR: No result file!");
            System.exit(1);
        } else {
            System.out.println("INFO: Result file is " + sResultFile);
        }

        if (sResultFileModules.length() == 0) {
            System.out.println("ERROR: No result file for modules!");
            System.exit(1);
        } else {
            System.out.println("INFO: Result file for modules is " + sResultFileModules);
        }

        if (sDelimiter.length() == 0) {
            System.out.println("ERROR: No delimiter!");
            System.exit(1);
        } else {
            System.out.println("INFO: Delimiter is " + sDelimiter);
        }

        if (lSearchStrings.isEmpty()) {
            System.out.println("ERROR: No search string!");
            System.exit(1);
        } else {
            for (String s : lSearchStrings) {
                System.out.println("INFO: Search string is " + s);
            }
        }

        if (sBeforeTimestamp.length() == 0) {
            System.out.println("ERROR: No prefix string!");
            System.exit(1);
        } else {
            System.out.println("INFO: Prefix string is " + sBeforeTimestamp);
        }

        if (sFileSeparator.length() == 0) {
            System.out.println("ERROR: No file separator!");
            System.exit(1);
        } else {
            System.out.println("INFO: File separator is " + sFileSeparator);
        }

        // Ignored files
        if (lIgnoredFiles.isEmpty()) {
            System.out.println("WARNING: No ignored files!");
        } else {
            for (String ignored : lIgnoredFiles) {
                System.out.println("INFO: Ignored file contains " + ignored);
            }
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
            for (String ignore : lIgnoredFiles) {
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
                    logEntry.setTimestamp(Long.valueOf(str.substring(2, 19).replace(".", "")));
                }

                // if string contains search string - fill the flag and add found by data
                for (String s : lSearchStrings) {
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

    /**
     * Trim all elements in string[]
     *
     * @param s massive
     * @return trimmed elements in massive
     */
    private static String[] trimAll(String[] s) {
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i].trim();
        }
        return s;
    }
}
