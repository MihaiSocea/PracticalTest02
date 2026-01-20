package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText editTextServerPort, editTextServerAddress,
            editTextClientPort, editTextCity;
    private Spinner spinnerInfo;
    private TextView textViewResult;
    private Button buttonStartServer, buttonGetWeather;

    private ServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPORTANT: must match activity_practical_test02_main.xml
        setContentView(R.layout.activity_practical_test02_main);

        editTextServerPort = findViewById(R.id.editTextServerPort);
        editTextServerAddress = findViewById(R.id.editTextServerAddress);
        editTextClientPort = findViewById(R.id.editTextClientPort);
        editTextCity = findViewById(R.id.editTextCity);
        spinnerInfo = findViewById(R.id.spinnerInfo);
        textViewResult = findViewById(R.id.textViewResult);
        buttonStartServer = findViewById(R.id.buttonStartServer);
        buttonGetWeather = findViewById(R.id.buttonGetWeather);

        // Spinner values from strings.xml (R.array.weather_options)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.weather_options, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInfo.setAdapter(adapter);

        buttonStartServer.setOnClickListener(v -> startServer());
        buttonGetWeather.setOnClickListener(v -> sendClientRequest());
    }

    private void startServer() {
        String portStr = editTextServerPort.getText().toString().trim();
        if (portStr.isEmpty()) {
            Toast.makeText(this, "Enter server port", Toast.LENGTH_SHORT).show();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid server port", Toast.LENGTH_SHORT).show();
            return;
        }

        if (serverThread != null && serverThread.isAlive()) {
            Toast.makeText(this, "Server already running", Toast.LENGTH_SHORT).show();
            return;
        }

        serverThread = new ServerThread(port);
        serverThread.start();

        textViewResult.setText("Server started on port " + port);
    }

    private void sendClientRequest() {
        String address = editTextServerAddress.getText().toString().trim();
        String portStr = editTextClientPort.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String info = (spinnerInfo.getSelectedItem() != null)
                ? spinnerInfo.getSelectedItem().toString()
                : "";

        if (address.isEmpty() || portStr.isEmpty() || city.isEmpty() || info.isEmpty()) {
            Toast.makeText(this, "Complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid client port", Toast.LENGTH_SHORT).show();
            return;
        }

        textViewResult.setText("Sending request...");

        ClientThread clientThread = new ClientThread(
                address,
                port,
                city,
                info,
                result -> runOnUiThread(() -> textViewResult.setText(result))
        );
        clientThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverThread != null) {
            serverThread.stopServer();   // presupune că ai metoda asta
            serverThread = null;
        }
    }
}
