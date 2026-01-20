package ro.pub.cs.systems.eim.practicaltest02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThread extends Thread {

    public interface ServerStatusListener {
        void onServerError(String msg);
        void onServerStarted(int port);
    }

    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    private final Map<String, WeatherData> cache = new ConcurrentHashMap<>();
    private final ServerStatusListener listener;

    public ServerThread(int port, ServerStatusListener listener) {
        this.port = port;
        this.listener = listener;
    }

    public Map<String, WeatherData> getCache() {
        return cache;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            if (listener != null) {
                listener.onServerStarted(port);
            }

            while (running) {
                Socket clientSocket = serverSocket.accept();
                new CommunicationThread(this, clientSocket).start();
            }

        } catch (IOException e) {
            if (listener != null) {
                listener.onServerError(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException ignored) { }
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) { }
    }
}
