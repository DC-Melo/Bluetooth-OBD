package com.dc.zk_obd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class Main2Activity extends Activity {
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private int mState = UART_PROFILE_DISCONNECTED;

    private UartService mService = null;
    private Button  btnScan,btnStart;
    private ListView messageListView;
    private ArrayAdapter<String> listAdapter;

    private BluetoothAdapter mBtAdapter = null;
    private BluetoothDevice mDevice = null;

    Signal odometer=new Signal();
    Signal tank=new Signal();
    Signal voltage=new Signal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Init_service();// 初始化后台服务
        btnScan = (Button) findViewById(R.id.button_scan);
        btnStart = (Button) findViewById(R.id.button_Start);
        messageListView = (ListView) findViewById(R.id.listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        messageListView.setAdapter(listAdapter);
        messageListView.setDivider(null);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService.mConnectionState==0){
                    // 创建一个蓝牙适配器对象
                    mBtAdapter = BluetoothAdapter.getDefaultAdapter();
                    // 如果未打开蓝牙就弹出提示对话框提示用户打开蓝牙
                    if (!mBtAdapter.isEnabled()) {
                        toastMessage("对不起，蓝牙还没有打开");
                        System.out.println("蓝牙还没有打开");
                        // 弹出请求打开蓝牙对话框
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    } else {
                        // 如果已经打开蓝牙则与远程蓝牙设备进行连接
                        if (btnScan.getText().equals("搜索")) {
                            /**
                             * 当"scan"按钮点击后，进入DeviceListActivity.class类，弹出该类对应的窗口
                             * ，并自动在窗口内搜索周围的蓝牙设备
                             */
                            Intent newIntent = new Intent(Main2Activity.this, DeviceListActivity.class);
                            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                        } else {
                            /**
                             * 当scan按钮点击之后，该按钮就会变成stop按钮， 如果此时点击了stop按钮，那么就会执行下面的内容
                             */
                            if (mDevice != null) {
                                // 断开连接
                                mService.disconnect();
                            }
                        }
                    }
                }
                else if (mService.mConnectionState==2){
                    byte[] bytes=odometer.getCancmd();
                    String s1=Utils.bytesToHexString(bytes);
                    mService.writeRXCharacteristic(bytes);
                    try {
                        listAdapter.add("[" + DateFormat.getTimeInstance().format(new Date()) + "] 发送0x: " +s1);
                        messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }

                }
            }
        });

    }

    private void Init_service() {
        System.out.println("Init_service");
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }
    // UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        // 与UART服务的连接建立
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            System.out.println("uart服务对象：" + mService);
            if (!mService.initialize()) {
                System.out.println("创建蓝牙适配器失败");
                // 因为创建蓝牙适配器失败，导致下面的工作无法进展，所以需要关闭当前uart服务
                finish();
            }
        }
        // 与UART服务的连接失去
        public void onServiceDisconnected(ComponentName classname) {
            // mService.disconnect(mDevice);
            mService = null;
        }
    };
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final Intent mIntent = intent;
        }
    };
    private void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
