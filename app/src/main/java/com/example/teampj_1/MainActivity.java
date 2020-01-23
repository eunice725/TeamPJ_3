package com.example.teampj_1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    LinearLayout card;
    TextView tvReadCard;
    //LinearLayout dialogAct
    String read;

    Button btnSend;
    EditText edtSendMsg;
    ImageView ivBluetooth;
    TextView tvReceive;
    ListView listview;


    ImageView ivBT;
    //    EditText edtSendMsg;
//    Button btnSend;

    BluetoothAdapter bluetoothAdapter;
    static final int REQUEST_ENABLE_BT = 10;
    int mPairedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    Thread mWorkerThread = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';
    byte readBuffer[];
    int readBufferPosition;


    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvReceive = (TextView) findViewById(R.id.tvReceive);
        btnSend = (Button) findViewById(R.id.btnSend);
        edtSendMsg = (EditText) findViewById(R.id.edtSendMsg);
        //  ivBluetooth = (ImageView) findViewById(R.id.ivBluetooth);
        tvReceive = (TextView) findViewById(R.id.tvReceive);
        //  listview = (ListView) findViewById(R.id.listview);

        card = (LinearLayout) findViewById(R.id.card);
        checkBluetooth();
        card.setOnClickListener(new View.OnClickListener() { //카드등록
            @Override
            public void onClick(View v) {
                createCard();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(edtSendMsg.getText().toString());
                edtSendMsg.setText("");
            }
        });
    }

    void createCard() { //신규카드 등록
        AlertDialog.Builder builder_createCard = new AlertDialog.Builder(this);
        builder_createCard.setTitle("");
        dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_createcard, null);
        tvReadCard = (TextView) dialogView.findViewById(R.id.tvReadCard);
        //   mDevices = bluetoothAdapter.getBondedDevices();
        //   mPairedDeviceCount = mDevices.size();

        // TODO: 2020-01-23 카드를 갖다댔을떄 RFID 값 받아야함
        //  readCard();
        builder_createCard.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("등록을 취소하였습니다");
            }
        });
        // TODO: 2020-01-23 등록버튼말고 tvReadCard 에 띄어진 항목 터치시 등록으로
        builder_createCard.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("카드를 등록하였습니다");
            }
        });
        tvReadCard.setText(read); //test //대충 아두이노가 핸드폰의 RFID 를 읽어온 값
        builder_createCard.setView(dialogView);
        builder_createCard.setCancelable(false);
        builder_createCard.show();
    }

    void checkBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) { //블루투스서비스를 지원하지않는다
            showToast("현재 기기는 블루투스서비스를 지원하지않습니다");
        } else {
            if (!bluetoothAdapter.isEnabled()) { //블루투스가 활성화되어있지않다
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //블루투스 실행권한 요청 창
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT); //블루투스 승인요청 창
            } else {
                selectDevice();
            }
        }
    } //checkBluetooth() END

    void selectDevice() {
        mDevices = bluetoothAdapter.getBondedDevices(); //페어링된 장치목록
        mPairedDeviceCount = mDevices.size(); //페어링된 장치의 갯수

        if (mPairedDeviceCount == 0) { //연결된 디바이스가 없는경우
            showToast("연결할 블루투스 장치가 하나도 없습니다");
        } else {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this); //Dialog 생성
            builder.setTitle("블루투스 장치 선택");
            //  builder.setIcon(R.drawable.android);
            List<String> listItems = new ArrayList<String>(); //동적배열
            for (BluetoothDevice device : mDevices) {
                listItems.add(device.getName()); //페어링된 장치이름
            }
            listItems.add("취소");
            final CharSequence items[] = listItems.toArray(new CharSequence[listItems.size()]); //

            builder.setItems(items, new DialogInterface.OnClickListener() { //아이템항목 선택하기위해
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == mPairedDeviceCount) { //카운트의 개수는 4개이고, 다이어로그의 인덱스4는 취소이다
                        showToast("취소를 선택하였습니다");
                    } else {
                        connectToSelectDevice(items[which].toString()); //선택한 장치와 연결시도 (페어링 작업)
                    }
                }
            });
            builder.setCancelable(false); //다이어로그창 띄어지고 프레임밖 부분을 터치해도 안사라지게
            builder.show();
        }
    } //selectDevice() END

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //승인요청 수락시 메서드 실행
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT: //10
                if (resultCode == RESULT_OK) { //승인
                    selectDevice();
                } else if (resultCode == RESULT_CANCELED) { //취소
                    showToast("블루투스 연결을 취소하였습니다");
                }
                break;
        }
    } //onActivityResult END

    BluetoothDevice getDeviceBondedList(String name) { //페어링된 블루투스 장치 이름으로 찾기
        BluetoothDevice selectedDevice = null;
        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) { //
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    void beginListenForData() { //데이터를 수신 준비 및 처리
        final Handler handler = new Handler(); //
        readBuffer = new byte[1024]; //
        readBufferPosition = 0; //
        mWorkerThread = new Thread(new Runnable() { //
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) { //정상적이게 통신중일때 //Interrupted:가로막힌, 중단된
                    try {
                        int bytesAvailable = mInputStream.available(); //
                        if (bytesAvailable > 0) { //받을자료가 있으면
                            byte paketBytes[] = new byte[bytesAvailable];
                            mInputStream.read(paketBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = paketBytes[i];
                                if (b == mCharDelimiter) { //데이타를 다 받앗다는 뜻으로 (?
                                    byte encodeBytes[] = new byte[readBufferPosition]; //
                                    System.arraycopy(readBuffer, 0, encodeBytes, 0, encodeBytes.length);
                                    final String data = new String(encodeBytes, "UTF-8"); //US-ASCII : 아스키코드 //UTF-8 :한글안깨짐


                                    readBufferPosition = 0; //
                                    handler.post(new Runnable() { //아두이노에 작성한 전송부분을 수신하여 작업할 곳
                                        @Override
                                        public void run() { //수신된 문자열 데이터에 대한 처리작업
                                            tvReceive.setText(data + mStrDelimiter); //데이타 (?
                                            read = tvReceive.getText().toString()+"RF값 보내줘어엉";
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException e) {
                        showToast("데이터 수신 중 오류가 발생하였습니다");
                    }
                }
            }
        });
        mWorkerThread.start();
    }

    void sendData(String msg) { //데이터를 송신
        msg += mStrDelimiter; //mStrDelimiter 문자 끝을 알리는...
        try {
            mOutputStream.write(msg.getBytes()); //문자 전송
        } catch (Exception e) {
            showToast("데이터 전송 중 오류가 발생하였습니다");
        }
    }

    void connectToSelectDevice(String selectedDeviceName) { //선택된 장치 연결(페어링) 메서드
        mRemoteDevice = getDeviceBondedList(selectedDeviceName); //
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//블루투스 소켓 통신 *************

        try {
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid); //
            mSocket.connect(); //연결시도
            mOutputStream = mSocket.getOutputStream(); //송신 //ex led 조종
            mInputStream = mSocket.getInputStream(); //수신 //ex 온도습도 값
            beginListenForData(); //
            // ivBluetooth.setImageResource(R.drawable.bluetooth_icon);
        } catch (Exception e) {
            showToast("블루투스 연결 중 오류가 발생하였습니다");
            //  ivBluetooth.setImageResource(R.drawable.bluetooth_grayicon);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mWorkerThread.interrupt(); //데이터 수신 쓰레드를 종료 thread
            mInputStream.close();
            mOutputStream.close();
            mSocket.connect();

        } catch (Exception e) {
        }
    }

    void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

/////////////////////////////////////