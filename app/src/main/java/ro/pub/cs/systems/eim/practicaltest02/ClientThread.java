package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Handler;
import android.os.Looper;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String city;
    private String infoType;
    private Handler handler;
    private ResultListener listener;

    public interface ResultListener {
        void onResult(String result);
    }

    public ClientThread(String address, int port, String city, String infoType,
                        ResultListener listener) {
        this.address = address;
        this.port = port;
        this.city = city;
        this.infoType = infoType;
        this.listener = listener;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, port);

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true
            );

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Trimite cererea
            out.println(city);
            out.println(infoType);

            // Citește tot răspunsul
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }

            socket.close();

            // Trimite rezultatul în UI
            handler.post(() -> listener.onResult(result.toString()));

        } catch (Exception e) {
            handler.post(() -> listener.onResult("Error: " + e.getMessage()));
        }
    }
}
