package com.cegevents.app;

import com.cegevents.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ViewEventFragment extends Fragment {
	
	public ViewEventFragment(){}
	
	// Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> eventsList;
 
    // url to get all events list
    private static String url_all_events = "http://10.0.2.2:80/app/get_event_list.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_EID = "eid";
    private static final String TAG_ENAME = "ename";
    
    protected ListView lv;
    // events JSONArray
    JSONArray events = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_view_events, container, false);
        // Hashmap for ListView
        eventsList = new ArrayList<HashMap<String, String>>();
        
        // Loading events in Background Thread
        new LoadAllEvents().execute();
 
        // Get listview
        lv = (ListView) rootView.findViewById(R.id.listView1);
    
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String eid = ((TextView) view.findViewById(R.id.eid)).getText()
                        .toString();
 
                // Starting new intent
                Intent in = new Intent(getActivity(),
                        EventDetailsActivity.class);
                // sending eid to next activity
                in.putExtra(TAG_EID, eid);
 
                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });
        return rootView;
    }
	
	
	 
	    // Response from Edit event Activity
	    @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        // if result code 100
	        if (resultCode == 100) {
	            // if result code 100 is received
	            // means user edited/deleted event
	            // reload this screen again
	            Intent intent = getActivity().getIntent();
	            getActivity().finish();
	            startActivity(intent);
	        }
	 
	    }
	 
	  
	   class LoadAllEvents extends AsyncTask<String, String, String> {
	 
	      
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(ViewEventFragment.this.getActivity());
	            pDialog.setMessage("Loading events. Please wait...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	        }
	 
	       
	        protected String doInBackground(String... args) {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            // getting JSON string from URL
	            JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);
	 
	            // Check your log cat for JSON reponse
	            Log.d("All events: ", json.toString());
	 
	            try {
	                // Checking for SUCCESS TAG
	                int success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1) {
	                    // events found
	                    // Getting Array of events
	                    events = json.getJSONArray(TAG_EVENTS);
	 
	                    // looping through All events
	                    for (int i = 0; i < events.length(); i++) {
	                        JSONObject c = events.getJSONObject(i);
	 
	                        // Storing each json item in variable
	                        String id = c.getString(TAG_EID);
	                        String name = c.getString(TAG_ENAME);
	 
	                        // creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();
	 
	                        // adding each child node to HashMap key => value
	                        map.put(TAG_EID, id);
	                        map.put(TAG_ENAME, name);
	 
	                        // adding HashList to ArrayList
	                        eventsList.add(map);
	                    }
	                } 
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	 
	            return null;
	        }
	 
	
	        protected void onPostExecute(String file_url) {
	            // dismiss the dialog after getting all events
	            pDialog.dismiss();
	            // updating UI from Background Thread
	            getActivity().runOnUiThread(new Runnable() {
	                public void run() {
	                 
	                    ListAdapter adapter = new SimpleAdapter(
	                            ViewEventFragment.this.getActivity(), eventsList,
	                            R.layout.list_item, new String[] { TAG_EID,
	                                    TAG_ENAME},
	                            new int[] { R.id.eid, R.id.ename });
	                    // updating listview
	                    lv.setAdapter(adapter);
	                }
	            });
	            
	        }
	 
	    }
	}
