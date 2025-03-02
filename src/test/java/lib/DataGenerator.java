package lib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {

    public static String getRandomValidEmail() {

        String timestamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        return "learnqa" + timestamp + "@example.com";

    }

    public static String getRandomInvalidEmailFormat() {

        String timestamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        return "learnqa" + timestamp + "example.com";

    }


    public static Map<String, String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomValidEmail());
        data.put("password", "123");
        data.put("username", "learnqa");
        data.put("firstName", "learnqa");
        data.put("lastName", "learnqa");
        return data;

    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
        Map<String, String> defaultValues = DataGenerator.getRegistrationData();
        Map<String, String> userData = new HashMap<>();
        String[] fields = {"email", "password", "username", "firstName", "lastName"};

        for (String field : fields) {
            if (nonDefaultValues.containsKey(field)) {

                userData.put(field, nonDefaultValues.get(field));
            } else {
                userData.put(field, defaultValues.get(field));
            }
        }
        return userData;

    }
}

