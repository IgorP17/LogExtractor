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

            for (String key : prop.stringPropertyNames()) {
                String value = prop.getProperty(key);

                if (value.contains(";")) {
                    //we have multiple values
                    current = Arrays.asList(trimAll(value.split(";")));
                } else {
                    // we have single value
                    current = Collections.singletonList(value.trim());
                }
                System.out.println(key + "=>" + value);
                hConfig.put(key, current);
            }
            System.out.println("INFO: Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getValue(String key){
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
}
