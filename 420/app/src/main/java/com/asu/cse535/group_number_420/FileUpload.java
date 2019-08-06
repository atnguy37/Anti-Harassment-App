package com.asu.cse535.group_number_420;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUpload {


    public boolean uploadFile(String path, String file_name, String server_address){
        boolean result = false;

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), path);
        String fileName = file_name;

        try {
            InputStream iS = new FileInputStream(file);
            byte[] dataFile;
            try {
                dataFile = IOUtils.toByteArray(iS);

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(server_address);

                InputStreamBody iSB = new InputStreamBody(new ByteArrayInputStream(dataFile), fileName);
                MultipartEntity mpE = new MultipartEntity();
                mpE.addPart("file", iSB);
                httpPost.setEntity(mpE);

                HttpResponse httpResponse = httpClient.execute(httpPost);

                Log.d("MainActivity", "It worked!");
                result = true;

            } catch (IOException e) {
                Log.e("MainActivity", e.toString());
                result = false;
            }
        } catch (FileNotFoundException e) {
            Log.e("MainActivity", e.toString());
            result = false;
        }

        return result;

    }
}
