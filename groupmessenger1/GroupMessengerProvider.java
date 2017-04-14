package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

// Reference: https://developers.android.com
//            https://developer.android.com/reference/android/content/ContentProvider.html#insert(android.net.Uri,%20android.content.ContentValues)
/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */



        //String string = strReceived + "\n";
        //FileOutputStream outputStream;

        try {
            String filename = (String) values.get("key");
            String val = (String) values.get("value");

            //getContext().openFileOutput(filename);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(val.toString());
            outputStreamWriter.close();


            //outputStream = getContext().openFileOutput(filename, MODE_PRIVATE);
            //outputStream.write(val.getBytes());
            System.out.println("File Name is:"+filename);
            System.out.println("Value Name is:"+val.toString());
            //outputStream.close();





        } catch (Exception e) {
            Log.e(TAG, "File write failed");
        }





        Log.v("insert", values.toString());
        return uri;
    }




    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {


        try {
            System.out.println(selection);
            String filename = selection;
  /*         // System.out.println(filename);
            //String string = strReceived + "\n";
            InputStreamReader inputStream = new InputStreamReader(getContext().openFileInput(selection));
            String str = inputStream.toString();
            //getContext().openFileOutput(filename);
            //inputStream = getContext().openFileInput(selection);
            //BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            //String str = br.readLine();
            System.out.println(str.toString());
            //String str = inputStream.read();
            //outputStream.write(string.getBytes());
*/
            FileInputStream fis = getContext().openFileInput(selection);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line,p = null;
            while ((line = bufferedReader.readLine()) != null) {
                //sb.append(line);
                p = line;
            }

            System.out.println("VALLLUEUEUUEUEUEUEE ="+sb);
//Reference : https://developer.android.com/reference/android/database/MatrixCursor.html
            MatrixCursor matrixCursor = new MatrixCursor(new String[] { "key", "value" });
            matrixCursor.addRow(new Object[] { filename, p });
            /*MatrixCursor myCursor = new MatrixCursor(new String[] { "key", "value"});
                MatrixCursor.RowBuilder builder = myCursor.newRow();
                builder.add("key", selection);
                builder.add("value", line.toString());
            myCursor.setNotificationUri(getContext().getContentResolver(),uri);
            *///inputStream.close();
            return matrixCursor;


        } catch (Exception e) {
            Log.e(TAG, "File query failed");
        }

        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */


        Log.v("query", selection);
        return null;
    }
}
