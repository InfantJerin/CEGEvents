package com.cegevents.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class EventDetailsActivity extends Activity {

	TextView eventName;
	TextView eventDesc;
	TextView eventCategory;
	TextView eventVenue;
	TextView eventStartDate;
	TextView eventStartTime;
	TextView eventEndDate;
	TextView eventEndTime;
    
	String eid;
	 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
 
    // single event url
    private static final String url_event_details = "http://10.0.2.2:80/app/get_event_details.php";
 
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_event = "event";
    private static final String TAG_EID = "eid";
    private static final String TAG_NAME = "name";
    private static final String TAG_EDESC = "edesc";
    private static final String TAG_VNAME = "vname";
    private static final String TAG_SDATE = "sdate";
    private static final String TAG_STIME = "stime";
    private static final String TAG_EDATE = "edate";
    private static final String TAG_ETIME = "etime";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
 
        // getting event details from intent
        Intent i = getIntent();
 
        // getting event id (pid) from intent
        eid = i.getStringExtra(TAG_EID);
        
        // Getting complete event details in background thread
        new GeteventDetails().execute();
 
    }
 
    /**
     * Background Async Task to Get complete event details
     * */
    class GeteventDetails extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventDetailsActivity.this);
            pDialog.setMessage("Loading event details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting event details in background thread
         * */
        protected String doInBackground(String... params) {
 
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("eid", eid));
                        Log.d("debug", "prior sending get request");
                        // getting event details by making HTTP request
                        // Note that event details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_event_details, "GET", params);
 
                        // check your log for json response
                        Log.d("debug", json.toString());
 
                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received event details
                            JSONArray eventObj = json
                                    .getJSONArray(TAG_event); // JSON Array
 
                            // get first event object from JSON Array
                            JSONObject event = eventObj.getJSONObject(0);
                           
                            // event with this pid found
                            // Edit Text
                            eventName = (TextView) findViewById(R.id.eventName);
                            eventDesc = (TextView) findViewById(R.id.eventDesc);
                            eventVenue = (TextView) findViewById(R.id.eventVenue);
                            eventStartDate = (TextView) findViewById(R.id.eventStartDate);
                            eventStartTime = (TextView) findViewById(R.id.eventStartTime);
                            eventEndDate = (TextView) findViewById(R.id.eventEndDate);
                            eventEndTime = (TextView) findViewById(R.id.eventEndTime);
                            
                            // display event data in EditText
                            eventName.setText(event.getString(TAG_NAME));
                            String desc = event.getString(TAG_EDESC);
                            if(desc.compareToIgnoreCase("null") == 0)
                            		desc = "No description given";
                            eventDesc.setText(desc);
                            eventVenue.setText(event.getString(TAG_VNAME));
                            eventStartDate.setText(event.getString(TAG_SDATE));
                            eventStartTime.setText(event.getString(TAG_STIME));
                            eventEndDate.setText(event.getString(TAG_EDATE));
                            eventEndTime.setText(event.getString(TAG_ETIME));
 
                        }else{
                            // event with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }
     
}
