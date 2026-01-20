package ro.pub.cs.systems.eim.practicaltest02;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String API_KEY = "e03c3b32cfb5a6f7069f2ef29237d87e";

    public static WeatherData getWeatherFromInternet(String city) throws Exception {

        String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + API_KEY;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        reader.close();

        JSONObject json = new JSONObject(result.toString());

        WeatherData data = new WeatherData();

        double tempK = json.getJSONObject("main").getDouble("temp");
        double tempC = tempK - 273.15;

        data.temperature = String.format("%.2f C", tempC);
        data.windSpeed = json.getJSONObject("wind").getDouble("speed") + " m/s";
        data.condition = json.getJSONArray("weather")
                .getJSONObject(0)
                .getString("description");
        data.pressure = json.getJSONObject("main").getInt("pressure") + " hPa";
        data.humidity = json.getJSONObject("main").getInt("humidity") + " %";
        data.timestamp = System.currentTimeMillis();

        return data;
    }
}
