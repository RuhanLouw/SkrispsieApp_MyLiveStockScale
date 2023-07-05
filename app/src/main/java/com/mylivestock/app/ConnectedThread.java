package com.mylivestock.app;

import static android.content.ContentValues.TAG;

import static com.mylivestock.app.MainActivity.MESSAGE_READ;
import static com.mylivestock.app.MainActivity.handlerMain;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket){
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        //get input and output streams - use temps as streams are final
        try{
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        }catch(Exception e){
            Log.e(TAG, "Socket's create method failed", e);
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void runStream(){
        byte[] buffer = new byte[1024]; //store received data in this buffer
        int bytes=0;
        while (true){
            try{
                    /*
                    Read from the InputStream from scale until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                buffer[bytes] = (byte) mmInStream.read();
                String readMessage;
                if(buffer[bytes] == '\n'){
                    readMessage = new String(buffer, 0, bytes);
                    handlerMain.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
                    bytes = 0;
                }else {
                    bytes++;
                }
            }catch(IOException e){
                e.printStackTrace();
                break;
            }
        }
    }

    public void write(String input){
        byte[] bytes = input.getBytes();
        try{
            mmOutStream.write(bytes);
        }catch(IOException e){
            Log.e("Send Error","Unable to send message");
        }
    }

    public void cancel(){
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }


}
//    @Override
//    public void onBackPressed() {
//        // Terminate Bluetooth Connection and close app
//        if (createConnectThread != null){
//            createConnectThread.cancel();
//        }
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//    }
