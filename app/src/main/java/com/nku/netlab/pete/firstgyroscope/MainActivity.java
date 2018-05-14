package com.nku.netlab.pete.firstgyroscope;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
    public static final String TAG = "MainActivity";
    private static final double NS2S = 1.0 / 1000000000.0;
    private SensorManager m_sensorManager;
    private Sensor m_gyroSensor;
    private TextView m_tvGyroX;
    private TextView m_tvGyroY;
    private TextView m_tvGyroZ;
    private TextView m_tvRotaX;
    private TextView m_tvRotaY;
    private TextView m_tvRotaZ;
    private TextView m_tvMessage;
    private boolean m_firstFlag;
    private long m_lastTimeStamp;
    private double [] m_lastRotaValue;
    private float [] m_lastGyroValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        m_gyroSensor = null;
        m_tvGyroX = findViewById(R.id.tvGXValue);
        m_tvGyroY = findViewById(R.id.tvGYValue);
        m_tvGyroZ = findViewById(R.id.tvGZValue);
        m_tvRotaX = findViewById(R.id.tvRXValue);
        m_tvRotaY = findViewById(R.id.tvRYValue);
        m_tvRotaZ = findViewById(R.id.tvRZValue);
        m_tvMessage = findViewById(R.id.tvMessage);

        m_firstFlag = true;
        m_lastTimeStamp = 0;
        m_lastRotaValue = new double[3];
        for (int i = 0; i < m_lastRotaValue.length; i++) {
            m_lastRotaValue[i] = 0.0;
        }
        m_lastGyroValue = new float[3];
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (m_sensorManager == null)
            m_sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        m_gyroSensor = m_sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (m_gyroSensor != null) {
            m_sensorManager.registerListener(this, m_gyroSensor, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            m_sensorManager = null;
            m_tvMessage.setText(R.string.tv_no_sensor);
        }
    }

    @Override
    protected void onStop() {
        if (m_sensorManager != null) {
            m_sensorManager.unregisterListener(this);
        }
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            m_tvGyroX.setText(String.format("%.5f", event.values[0]));
            m_tvGyroY.setText(String.format("%.5f", event.values[1]));
            m_tvGyroZ.setText(String.format("%.5f", event.values[2]));

            if (m_firstFlag) {
                m_firstFlag = false;
                for (int i = 0; i < m_lastRotaValue.length; i++) {
                    m_lastRotaValue[i] = 0.0;
                }
                m_lastTimeStamp = event.timestamp;
                System.arraycopy(event.values, 0, m_lastGyroValue, 0, event.values.length);
            }
            else {
                long currentTime = event.timestamp;
                double durationInSec = (currentTime - m_lastTimeStamp) * NS2S;
                for (int i = 0; i < m_lastRotaValue.length; i++) {
                    m_lastRotaValue[i] += 0.5 * (m_lastGyroValue[i] + event.values[i]) * durationInSec;
                }
                m_lastTimeStamp = currentTime;
                System.arraycopy(event.values, 0, m_lastGyroValue, 0, event.values.length);
            }
            m_tvRotaX.setText(String.format("%.2f", m_lastRotaValue[0] * (180.0 / Math.PI)));
            m_tvRotaY.setText(String.format("%.2f", m_lastRotaValue[1] * (180.0 / Math.PI)));
            m_tvRotaZ.setText(String.format("%.2f", m_lastRotaValue[2] * (180.0 / Math.PI)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
