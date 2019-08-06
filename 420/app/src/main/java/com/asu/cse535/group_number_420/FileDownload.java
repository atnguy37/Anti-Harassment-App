package com.asu.cse535.group_number_420;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javax.net.ssl.HttpsURLConnection;

public class FileDownload extends AsyncTask<String, Integer, String> {

    private Context context;
    ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;

    public FileDownload(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        System.out.println("URL 1: " + sUrl[0]);
        //searchButton = (Button) findViewById(R.id.button1);

        if(sUrl[0].contains("https")) {
            HTTPSDownload(sUrl);
        }
        else {
            System.out.println("Link: " + sUrl[0]);
            HTTPDownload(sUrl);
        }
        return null;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("URL onPreExecute: ");
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        System.out.println("URL Wave: ");
        //mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
//        mProgressDialog.setIndeterminate(false);
//        mProgressDialog.setMax(100);
//        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        //mProgressDialog.dismiss();
        if (result != null){
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();


        }else{
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
//            if(searchButtonPress){
//                extractAppName();
//                searchButtonPress = false;
//            }else if(downloadButtonPress){
//                installApp(appName);
//                downloadButtonPress = false;
//            }

            //uninstallApp();
	            /*Process install;

	            try {

	            install = Runtime.getRuntime().exec("/system/bin/busybox install " + Environment.getExternalStorageDirectory() + "/downloads/" + "RaRandomFlashlight.apk");

	            int iSuccess = install.waitFor();

	            Log.e("TEST", ""+iSuccess);

	            } catch (IOException e) {
	            	Toast.makeText(context,"I/oException", Toast.LENGTH_SHORT).show();
	            } catch (InterruptedException e) {
	            	Toast.makeText(context,"I/oException", Toast.LENGTH_SHORT).show();
	            }*/
        }
    }

//    private void installApp(String ...sUrl ){
//	    	/*Intent intent = new Intent(Intent.ACTION_VIEW);
//
//    		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/downloads/" + sUrl[0])), "application/vnd.android.package-archive");
//
//    		startActivity(intent);*/
//        Process install;
//
//
//        try {
//
//            install = Runtime.getRuntime().exec("su -c /system/xbin/busybox install -c " + Environment.getExternalStorageDirectory() + "/downloads/" + appName + " /system/app/");
//
//            int iSuccess = install.waitFor();
//
//            Log.e("TEST", ""+iSuccess);
//
//        } catch (IOException e) {
//
//        } catch (InterruptedException e) {
//
//        }
//    }

//    private void uninstallApp(String ... sUrl){
//
//        Intent intent = new Intent(Intent.ACTION_DELETE);
//        intent.setData(Uri.parse("package:com.example."+sUrl[0]));
//        startActivity(intent);
//    }

    private void extractAppName(){
        InputStream input = null;

        try {
            input = new FileInputStream(Environment.getExternalStorageDirectory().getPath()+"/downloads/AppName.txt");
            InputStreamReader isr = new InputStreamReader ( input ) ;
            BufferedReader buffreader = new BufferedReader ( isr ) ;

            //appName = buffreader.readLine( ) ;


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String HTTPSDownload(String... sUrl) {
        int fileLength;
        InputStream input = null;
        OutputStream output = null;
        HttpsURLConnection connection = null;
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            System.out.println("URL 1: " + sUrl[0]);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            String contentType = connection.getContentType();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            input = connection.getInputStream();

            fileLength = connection.getContentLength();
            System.out.println("Length: " + fileLength);

            output = new FileOutputStream(Environment.getExternalStorageDirectory()+sUrl[1]);



            //downloadButton.setText("Connecting .....");
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
    //                System.out.println("Total: " + total);
            }
        } catch (Exception e) {
            System.out.println("URL error: " + sUrl[0]);
            return e.toString();
        } finally {
            try {
                System.out.println("URL final: " + sUrl[0]);
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    private String HTTPDownload (String ... f_url) {
        int count;
        URLConnection connection = null;
        System.out.println("URL 1: " + f_url[0]);
        try {
            URL url = new URL(f_url[0]);
            System.out.println("URL 2: " + f_url[0]);
            connection = url.openConnection();
            System.out.println("URL 3: " + f_url[0]);
            connection.connect();
            System.out.println("URL 4   : " + f_url[0]);
            // Get Music file length
            int lenghtOfFile = connection.getContentLength();
            System.out.println("Length: " + lenghtOfFile);
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(),10*1024);
            // Output stream to write file in SD card
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+f_url[1]);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // Publish the progress which triggers onProgressUpdate method
                //publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // Write data to file
                output.write(data, 0, count);
                System.out.println("count: " + count);
            }
            // Flush output
            output.flush();
            // Close streams
            output.close();
            input.close();
            System.out.println("Path: " + Environment.getExternalStorageDirectory().getPath()+f_url[1]);
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }
}
