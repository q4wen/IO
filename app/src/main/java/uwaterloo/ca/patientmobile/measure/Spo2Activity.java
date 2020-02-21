package uwaterloo.ca.patientmobile.measure;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uwaterloo.ca.patientmobile.R;

public class Spo2Activity extends AppCompatActivity {

    private final String TAG = Spo2Activity.class.getSimpleName();

    private Button connect, start, stop, reset;
    private TextView spo2View, pulseView, message;

    private UsbSerialPort port;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;

    //private CMS50FWBluetoothConnectionManager cms50FWBluetoothConnectionManager = null;

    private static final String BPM_STRING = " bpm";
    private static final String PERCENT_SIGN_STRING = "%";
    private static final String EMPTY_STRING = "";
    private static final String FINGER_OUT_MESSAGE = "Finger Out";
    private static final String FINGER_OUT_TOO_LONG_MESSAGE = "Finger Out For Too Many Seconds";
    private static final String OXYGEN_LEVEL_TOO_LOW_MESSAGE = "Oxygen Level Too Low For Too Many Seconds";
    private static final String DATA_FRAME_NULL_ALARM_MESSAGE = "Bluetooth connection to CMS50FW has apparently been lost";

    private static final int FINGER_OUT_MESSAGE_THRESHOLD = 10;
    private static final int FINGER_OUT_ALARM_THRESHOLD = 600;
    private static final int OXYGEN_LEVEL_TOO_LOW_ALARM_THRESHOLD = 600;
    private static final int DATA_FRAME_NULL_ALARM_THRESHOLD = 600;
    private static final String SEARCHING_FOR_SIGNAL_MESSAGE = "Searching for O2 level and pulse ...";
    private static final int ONE_HUNDRED = 100;


    // alarm and sound related properties
    Integer minimumSpo2Percentage = null;
    private long consecutiveFingerOutDataFrameCount = 0;
    private long consecutiveSpO2OutOfRangeCount = 0;
    private long consecutiveDataFrameNullCount = 0;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    Spo2Activity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Spo2Activity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spo2);

        connect = findViewById(R.id.connect);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        reset = findViewById(R.id.reset);
        spo2View = findViewById(R.id.spo2);
        pulseView = findViewById(R.id.pulse);
        message = findViewById(R.id.message);

        stop.setEnabled(false);

//        cms50FWBluetoothConnectionManager = new CMS50FWBluetoothConnectionManager("SpO202");

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = new byte[5];

                try {
                    port.write("246".getBytes(), 100);
                    port.read(bytes, 100);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                Log.v(TAG, bytes[0] + " " + bytes[1] + " " + bytes[2]);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    initializeLiveData();
                } catch (Exception e) {
                    Log.e(TAG, "Initialization failed.");
                }
            }
        });

//        stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopIoManager();
//            }
//        });

//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    cms50FWBluetoothConnectionManager.reset();
//                    connect.setEnabled(true);
//                    start.setEnabled(true);
//                    stop.setEnabled(false);
//                } catch (Exception e) {
//                    System.err.println(e);
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();


        UsbManager manager = (UsbManager) getSystemService(this.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        UsbDevice device = deviceList.get("/dev/bus/usb/001/002");

        final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        manager.requestPermission(device, permissionIntent);

        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return;
        }

        port = driver.getPorts().get(0); // Most devices have just one port (port 0)

        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (Exception e) {
            System.err.println(e);
            Log.e(TAG, e.toString());
            try {
                port.close();
            } catch (IOException e2) {
                // Ignore.
            }
            port = null;
            return;
        }
    }

    private void initializeDevice() throws Exception{
        byte[] hello1 = {(byte)0x7d, (byte)0X81, (byte)0xa7, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80};
        port.write(hello1, 100);
        byte[] response = new byte[9];
        int len = port.read(response, 100);
        Log.v(TAG, "The lenght of the response is " + len);
        String result = "First response:";
        for (byte b : response) {
            result += b + " ";
        }
        Log.v(TAG, result);

        byte[] hello2 = {(byte)0x7d, (byte)0X81, (byte)0xa2, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80};
        port.write(hello2, 100);
        byte[] hello3 = {(byte)0x7d, (byte)0X81, (byte)0xa0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80};
        port.write(hello3, 100);
        byte[] response1 = new byte[9];
        port.read(response1, 100);

        result = "Second response:";
        for (byte b : response) {
            result += b + " ";
        }
        Log.v(TAG, result);
    }

    private void initializeLiveData() throws Exception{
        while (true) {
            initializeDevice();

            byte[] reiveCmd = {(byte) 0x7d, (byte) 0X81, (byte) 0xa1, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
            port.write(reiveCmd, 100);

            processData();
        }
    }

    private void processData() throws Exception{
        while(true) {
            byte[] data = new byte[5];
            port.read(data, 100);
            ArrayList<String> dataStr = new ArrayList<>();
            String raw = "Raw data: ";
            for (int i = 0; i < 5; i++) {
                raw += data[i] + " ";
                String s = String.format("%8s", Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0');
                dataStr.add(s);
            }
            Log.v(TAG, raw);
            Log.v(TAG, dataStr.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (port != null) {
            try {
                port.close();
            } catch (IOException e) {
                // Ignore.
            }
            port = null;
        }
        finish();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (port != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(port, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void updateReceivedData(byte[] data) {
        message.setText("Read " + data.length + " bytes: \n" + data[0] + "\n" + data[1]);
    }
//
//    void setUIAlert(final String alertMessage) {
//        this.onResume();
//        message.setText(alertMessage);
//    }
//
//    private void updateUI(long time, final String spo2, final String pulse) {
//        spo2View.setText(spo2);
//        pulseView.setText(pulse);
//    }
//
//    public void processDataFrame(DataFrame dataFrame) {
//        if (dataFrame == null) {
//            consecutiveDataFrameNullCount++;
//            if (consecutiveDataFrameNullCount > DATA_FRAME_NULL_ALARM_THRESHOLD) {
//                setUIAlert(DATA_FRAME_NULL_ALARM_MESSAGE);
//            }
//        } else {
//            consecutiveDataFrameNullCount = 0;
//            if (dataFrame.spo2Percentage <= ONE_HUNDRED) { // valid data frame
//                consecutiveFingerOutDataFrameCount = 0;
//                updateUI(dataFrame.time, dataFrame.spo2Percentage + PERCENT_SIGN_STRING, dataFrame.pulseRate + BPM_STRING);
//                if (dataFrame.spo2Percentage < minimumSpo2Percentage) {
//                    consecutiveSpO2OutOfRangeCount++;
//                } else {
//                    consecutiveSpO2OutOfRangeCount = 0;
//                }
//                if (consecutiveSpO2OutOfRangeCount > OXYGEN_LEVEL_TOO_LOW_ALARM_THRESHOLD) {
//                    setUIAlert(OXYGEN_LEVEL_TOO_LOW_MESSAGE);
//                } else {
//                    //unsetUIAlert();
//                }
//            } else { // probably not valid data frame
//                if (dataFrame.isFingerOutOfSleeve) {
//                    consecutiveFingerOutDataFrameCount++;
//                    if (consecutiveFingerOutDataFrameCount > FINGER_OUT_MESSAGE_THRESHOLD) {
//                        updateUI(dataFrame.time, FINGER_OUT_MESSAGE, EMPTY_STRING);
//                    }
//                    if (consecutiveFingerOutDataFrameCount > FINGER_OUT_ALARM_THRESHOLD) {
//                        setUIAlert(FINGER_OUT_TOO_LONG_MESSAGE);
//                    }
//                } else {
//                    updateUI(dataFrame.time, SEARCHING_FOR_SIGNAL_MESSAGE, EMPTY_STRING);
//                }
//            }
//        }
//    }

}
