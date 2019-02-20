import java.io.FileInputStream;
import java.util.*;

public class Config {

    private HashMap<String, List<String>> hConfig = new HashMap<>();

    /**
     * Load config
     *
     * @param pathToConfig path to file (config.properties)
     */

    Config(String pathToConfig) {
        try {
            System.out.println("INFO: Loading config file...");
            FileInputStream fileInputStream;
            Properties prop = new Properties();
            //обращаемся к файлу и получаем данные
            fileInputStream = new FileInputStream(pathToConfig);
            prop.load(fileInputStream);

            List<String> current;
            // Заполняем конфиг значениями из конф файла
            for (String key : prop.stringPropertyNames()) {
                String value = prop.getProperty(key);

                if (value.contains(";")) {
                    //we have multiple values
                    current = Arrays.asList(trimAll(value.split(";")));
                } else {
                    // we have single value
                    current = Collections.singletonList(value.trim());
                }
                System.out.println(key + "=" + value);
                hConfig.put(key, current);
            }

            // check properties -Dhello="hello"
            System.out.println("INFO: Checking and override properties");
            String sProperty;
            if ((sProperty = System.getProperty("LOG_DIR")) != null) {
                System.out.println("WARNING! Overload LOG_DIR to value = " + sProperty);
                if (sProperty.contains(";")) {
                    //we have multiple values
                    current = Arrays.asList(trimAll(sProperty.split(";")));
                } else {
                    // we have single value
                    current = Collections.singletonList(sProperty.trim());
                }
                hConfig.replace("LOG_DIR", current);
            }

            // check data
            if (!checkData()) System.exit(1);
            System.out.println("INFO: Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return value from HashMap
     *
     * @param key config key
     * @return config value as List
     */
    public List<String> getValue(String key) {
        return hConfig.get(key);
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

    private boolean checkData() {
        System.out.println("INFO: Checking needed info...");
        List<String> paramNames = new ArrayList<>();
        paramNames.add("LOG_DIR");
        paramNames.add("RESULT_FILE");
        paramNames.add("RESULT_FILE_MODULES");
        paramNames.add("DELIMITER");
        paramNames.add("BEFORE_TIMESTAMP");
        paramNames.add("SEARCH_STRING");
        paramNames.add("SEPARATOR");

        for (String s : paramNames) {
            if (hConfig.get(s) == null) {
                System.out.println("ERROR: Property " + s + " not found!");
                return false;
            }
        }

        return true;
    }
}
