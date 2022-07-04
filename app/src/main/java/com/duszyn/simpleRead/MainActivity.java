package com.duszyn.simpleRead;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.pda.rfid.EPCModel;
import com.pda.rfid.IAsynchronousMessage;
import com.pda.rfid.uhf.UHFReader;
import com.port.Adapt;

public class MainActivity extends AppCompatActivity implements IAsynchronousMessage {
    private static final String TAG = "Demo";
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private boolean isOpened = false;
    private boolean isReading = false;
    private TextView view;

    private void initView() {
        Adapt.init(this);
        isOpened = UHFReader.getUHFInstance().OpenConnect(this);
        if (!isOpened) {
            Log.d(TAG, "open UHF failed!");
        }
// Set base band auto mode, q=1, session=1, flag = 0 flagA
        UHFReader._Config.SetEPCBaseBandParam(255, 0, 1, 0);

        // set ant 1 power to 20dBm
        // TODO tutaj dodać edittext z którego będzie brać moc anteny w dBm
        Button confirmB = findViewById(R.id.confirm);
        confirmB.setOnClickListener(v -> {
            EditText antPower = findViewById(R.id.antPower);
            String antpowertext = antPower.getText().toString();


            System.out.println(antPower.getText().getClass().getSimpleName() + "esia");
            UHFReader._Config.SetANTPowerParam(1, Integer.parseInt(antpowertext));
            view = findViewById(R.id.test);
            //UHFReader._Config.GetANTPowerParam() zwraca -1 (failure)
            view.setText("current antena power is " +  UHFReader._Config.GetANTPowerParam());
        });

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
//request permission
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            initView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        UHFReader.getUHFInstance().CloseConnect();
        super.onDestroy();
    }

    // read button onClick handle
    public void onRead(View v) {
        if (!isOpened) {
            return;
        }
        if (isReading) {
            return;
        }
// start reading 6C tags using ant 1 in cycle continuous reading mode
        isReading = UHFReader._Tag6C.GetEPC(1, 1) == 0;
        view.setText(UHFReader._Tag6C.GetEPC(1,1));
    }

    // stop button onClick handle
    public void onStop(View v) {
        if (!isOpened) {
            return;
        }
        if (!isReading) {
            return;
        }
        UHFReader.getUHFInstance().Stop();
        isReading = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keyCode = " + keyCode);

        //KOD KTÓRY ZWRACA E/libc: Access denied finding property "ro.serialno" CO KLIKNIĘCIE
//        if ((Adapt.DEVICE_TYPE_HY820 == Adapt.getDeviceType() && (keyCode == KeyEvent.KEYCODE_F9
//                /* RFID Handle button*/ || keyCode == 285 /* Left shortcut*/ || keyCode == 286 /* Right shortcut*/))
//                || ((Adapt.getSN().startsWith("K3")) && (keyCode == KeyEvent.KEYCODE_F1 || keyCode
//                == KeyEvent.KEYCODE_F5))
//                || ((Adapt.getSN().startsWith("K6")) && (keyCode == KeyEvent.KEYCODE_F1 || keyCode
//                == KeyEvent.KEYCODE_F5))) { // Press the handle button
            onRead(null);

//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp keyCode = " + keyCode);
        //TO SAMO CO W ONKEYDOWN
//        if ((Adapt.DEVICE_TYPE_HY820 == Adapt.getDeviceType() && (keyCode == KeyEvent.KEYCODE_F9
//                /* RFID Handle button*/ || keyCode == 285 /* Left shortcut*/ || keyCode == 286 /* Right shortcut*/))
//                || ((Adapt.getSN().startsWith("K3")) && (keyCode == KeyEvent.KEYCODE_F1 || keyCode
//                == KeyEvent.KEYCODE_F5))
//                || ((Adapt.getSN().startsWith("K6")) && (keyCode == KeyEvent.KEYCODE_F1 || keyCode
//                == KeyEvent.KEYCODE_F5))) { // release the handle button
            onStop(null);
//        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void OutPutEPC(EPCModel epcModel) {
        Log.d(TAG, " EPC: " + epcModel._EPC
                + " TID: " + epcModel._TID
                + " UserData:" + epcModel._UserData);
// TODO save the data and process in other thread
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            initView();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}