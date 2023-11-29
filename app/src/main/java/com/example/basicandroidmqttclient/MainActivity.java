package com.example.basicandroidmqttclient;

import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonParseException;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RegistredTempAdapter adapter;
    private List<RegistredTemp> registredTemps;
    public static final String brokerURI = "ec2-18-214-249-54.compute-1.amazonaws.com";

    Gson gson = new Gson();
    SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    Activity thisActivity;
    TextView subMsgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        registredTemps = new ArrayList<>();
        adapter = new RegistredTempAdapter(registredTemps);
        recyclerView.setAdapter(adapter);

        Button resetButton = findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(this::resetRecords);
        updateResetButtonVisibility();

        thisActivity = this;

        sendSubscription();
    }

    /** Called when the user taps the Send button */
    public void publishMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText tempMin = (EditText) findViewById(R.id.editTextTempMin);
        EditText tempMax = (EditText) findViewById(R.id.editTextTempMax);

        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();
        client.publishWith()
                .topic("min")
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(tempMin.getText().toString().getBytes())
                .send();
        client.publishWith()
                .topic("max")
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(tempMax.getText().toString().getBytes())
                .send();
        client.disconnect();
        startActivity(intent);
    }

    public void sendSubscription() {

        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback lambda function to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter("tempMonitor")
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    thisActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                final String payload = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                                RegistredTemp registredTemp = gson.fromJson(payload, RegistredTemp.class);

                                final String text = registredTemp.getTemp() + "Cº - " + dateFormater.format(registredTemp.getDate());
                                addRegistredTemp(registredTemp);

                            } catch (JsonParseException ignored) {

                            }
                        }
                    });
                })
                .send();
    }

    private void addRegistredTemp(RegistredTemp registredTemp) {
        registredTemps.add(registredTemp);
        adapter.notifyItemInserted(registredTemps.size() - 1);
        updateResetButtonVisibility();
    }

    public void resetRecords(View view) {
        registredTemps.clear();
        adapter.notifyDataSetChanged();
        updateResetButtonVisibility();
    }

    private void updateResetButtonVisibility() {
        Button resetButton = findViewById(R.id.buttonReset);
        if (registredTemps.isEmpty()) {
            resetButton.setVisibility(View.GONE);
        } else {
            resetButton.setVisibility(View.VISIBLE);
        }
    }


}
