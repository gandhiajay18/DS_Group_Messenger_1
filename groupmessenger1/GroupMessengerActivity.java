package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
// Reference: PA1 assignment - with some editing to enable multicasting
//            developers.android.com
/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
  private int seqNo = 0;                            //Sequence number

    static final int SERVER_PORT = 10000;           //Server Port Number - Similar to PA1
    static final String TAG = GroupMessengerActivity.class.getSimpleName();         //For Logging Purposes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             *
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            ServerSocket serverSocket = new ServerSocket(10000);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat pr100000ints out all the messages that
             * Log class writes.
             *
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
          Log.e(TAG, "Can't create a ServerSocket"+myPort);
            return;
        }
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
        //Log.e(TAG, "Before button creation");
//Reference: https://developer.android.com/reference/android/widget/Button.html
//Reference: https://developer.android.com/reference/android/view/View.OnClickListener.html

        Button button = (Button) findViewById(R.id.button4);                //Create a Button
        final EditText et = (EditText) findViewById(R.id.editText1);        //Create a editText
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Inside OnClick");

                System.out.println("Test OnClick");                         //Debugging purposes
                String msg = et.getText().toString() + "\n";                //Get data from editText
                et.setText(""); // This is one way to reset the input box.  //Clear editText
                TextView TextView = (TextView) findViewById(R.id.textView1);//Generate a TextView
                TextView.append(msg+"\n");                                  // This is to display msg on TextView


                    /*
                     * Note that the following AsyncTask uses AsyncTask.SERIAL_EXECUTOR, not
                     * AsyncTask.THREAD_POOL_EXECUTOR as the above ServerTask does. To understand
                     * the difference, please take a look at
                     * http://developer.android.com/reference/android/os/AsyncTask.html
                     */
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort); //Send message 'msg'
                Log.e(TAG, "End OnClick");


            }

        });}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
        @Override

        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            while (true) {
                try {
                    Log.e(TAG, "Server Socket");

                    Socket example = serverSocket.accept();
                    InputStreamReader input = new InputStreamReader(example.getInputStream());
                    BufferedReader reader = new BufferedReader(input);
                    Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");
                    String str;
                    Log.e(TAG, "Server Socket2");
         //  Reference: Content Values - https://developer.android.com/reference/android/content/ContentProvider.html#insert(android.net.Uri,%20android.content.ContentValues)
         // Reference: Some suggestions from Piazza posts!

                    //   System.out.println(reader.readLine());
                    if ((str = reader.readLine()) != null) {
                        System.out.println("Before Incrementing :"+seqNo);
                        ContentValues cVal = new ContentValues();                     //Create a contentValues variable 'cVal'
                        cVal.put("key", ((String.valueOf(seqNo))));                   //Insert  key (Sequence Number) into the cVal
                        seqNo++;                                                      //Increment Sequence Number
                        System.out.println("After Incrementing :"+seqNo);
                        cVal.put("value",str.toString());                             //Insert value (Data/msg) into the cVal
                        Uri uri = getContentResolver().insert(mUri,cVal);
                        publishProgress(str);                                         //Publish the Message
                        //input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            }
        }
        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String strReceived = strings[0].trim();
            TextView TextView = (TextView) findViewById(R.id.textView1);
            TextView.append(strReceived + "\t\n");
         //   TextView localTextView = (TextView) findViewById(R.id.local_text_display);
         //   localTextView.append("\n");

            /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             *
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */

//            String filename = "SimpleMessengerOutput";
//            String string = strReceived + "\n";
//            FileOutputStream outputStream;
//
//            try {
//                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//                outputStream.write(string.getBytes());
//                outputStream.close();
//            } catch (Exception e) {
//                Log.e(TAG, "File write failed");
//            }

            return;
        }
    }

    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... msgs) {
            int[] PORTS = {11108,11112,11116,11120,11124};                              //Array of all 5 port values

            try {
                for (int i : PORTS){                                                    //Loop over all ports to multicast
                    // String remotePort = REMOTE_PORT0;
                    // if (msgs[1].equals(REMOTE_PORT0))
                    //    remotePort = REMOTE_PORT1;
                    Log.e(TAG, "Inside ClientTask");

                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), i );    //Create a socket for every port
                    Log.e(TAG, "Client Socket Created");

                    String msgToSend = msgs[0];                                                           //Message to be sent
                    // OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
                    //BufferedWriter writer = new BufferedWriter(output);

                    System.out.println(msgToSend);
                    PrintStream os = new PrintStream(socket.getOutputStream());
                    // while(true)
                    //{
                    os.println(msgToSend);                                                                //Print the message
                    // }
                    // PrintWriter print = new PrintWriter(socket.getOutputStream());
                    // print.println(msgToSend);
                    // print.close();
                    // System.out.println("reached here");
                /*
                 * TODO: Fill in your client code that sends out a message.
                 */
                    //print.close();
                    //output.close();
                    Thread.sleep(100);
                socket.close();
                    }
            } catch (UnknownHostException e) {
              Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
    }




}
