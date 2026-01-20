package ro.pub.cs.systems.eim.practicaltest02;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true
            );

            // 1. Citește cererea
            String city = in.readLine();
            String infoType = in.readLine();

            if (city == null || infoType == null) {
                socket.close();
                return;
            }

            city = city.trim().toLowerCase();
            infoType = infoType.trim().toLowerCase();

            Map<String, WeatherData> cache = serverThread.getCache();
            WeatherData data;

            // 2. CACHE CHECK (3.a)
            if (cache.containsKey(city)) {
                data = cache.get(city);
            } else {
                // 3. FETCH INTERNET (3.b)
                data = NetworkUtils.getWeatherFromInternet(city);
                cache.put(city, data);
            }

            // 4. Construiește răspunsul (3.c + 3.d)
            if (infoType.equals("all")) {
                out.println("City: " + city);
                out.println("Temperature: " + data.temperature);
                out.println("Wind: " + data.windSpeed);
                out.println("Condition: " + data.condition);
                out.println("Pressure: " + data.pressure);
                out.println("Humidity: " + data.humidity);
            } else {
                switch (infoType) {
                    case "temperature":
                        out.println(data.temperature);
                        break;
                    case "wind_speed":
                        out.println(data.windSpeed);
                        break;
                    case "condition":
                        out.println(data.condition);
                        break;
                    case "pressure":
                        out.println(data.pressure);
                        break;
                    case "humidity":
                        out.println(data.humidity);
                        break;
                    default:
                        out.println("Unknown info type");
                }
            }

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
