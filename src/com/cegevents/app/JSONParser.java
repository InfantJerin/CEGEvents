package com.cegevents.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.os.StrictMode;
import android.util.Log;
 
public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
 
    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
            List<NameValuePair> params) {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    	StrictMode.setThreadPolicy(policy); 
        // Making HTTP request
        try {
        	Log.d("JSON", "testing");
            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                Log.d("JSON", "in POST1");
                HttpPost httpPost = new HttpPost(url);
                Log.d("JSON", "in POST2");
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                Log.d("JSON", "in POST3");
 
                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.d("JSON", "in POST4");
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.d("JSON", "in POST5");
                if(httpEntity != null) {
                	Log.d("JSON", "not null");
                	is = httpEntity.getContent();
                }
            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                Log.d("JSON", "in GET1");
                String paramString = URLEncodedUtils.format(params, "utf-8");
                Log.d("JSON", "in GET2");
                url += "?" + paramString;
                Log.d("JSON", "url:" + url);
                HttpGet httpGet = new HttpGet(url);
                Log.d("JSON", "in GET4");
 
                HttpResponse httpResponse = httpClient.execute(httpGet);
                Log.d("JSON", "in GET5");
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.d("JSON", "in GET6");
                if(httpEntity != null) {
                	Log.d("JSON", "not null");
                	is = httpEntity.getContent();
                }
            }
 
        } catch (UnsupportedEncodingException e) {
        	Log.d("JSON", e.toString());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
        	Log.d("JSON", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
        	Log.d("JSON", e.toString());
            e.printStackTrace();
        }
 
        try {
        	if(is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.e("debug", json);
        	}
        	else {
        		json = "{\"events\":[{\"eid\":\"10001\",\"ename\":\"Abacus Inaugration\"},{\"eid\":\"10003\",\"ename\":\"NSS Unit-11\"}],\"success\":1}";
        		Log.d("JSON", "hardcoding due to error");
        	}
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jObj;
 
    }
}