package com.cegevents.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class AddEventFragment extends Fragment {
	
	public AddEventFragment(){}
	
	
	String cname;
	 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<String> categoriesList;
    ArrayList<HashMap<String, String>> eventList;
    // url to get all categories list
    private static String url_all_categories = "http://10.0.2.2:80/app/get_categories_list.php";
    private static String url_events = "http://10.0.2.2:80/app/get_events_in_category.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_CNAME = "cname";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_EID = "eid";
    private static final String TAG_ENAME = "ename";
    // categories JSONArray
    JSONArray categories = null;
	JSONArray events = null;
	
    Spinner catsp;
    Spinner evesp;
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_add_event, container, false);
        // Hashmap for ListView
        categoriesList = new ArrayList<String>();
        eventList = new ArrayList<HashMap<String,String>>();
        
        // Get listview
        catsp = (Spinner) rootView.findViewById(R.id.categorySpinner);
        evesp = (Spinner) rootView.findViewById(R.id.eventSpinner);
        // Loading categories in Background Thread
        new LoadAllcategories().execute();
 
        catsp.setOnItemSelectedListener(new OnItemSelectedListener() 
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
            {
            	cname = (String) catsp.getSelectedItem().toString();
            	eventList.clear();
            	new LoadEvents().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) 
            {
                Toast.makeText(getActivity(), "hello", 5).show();
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
	 
	  
	   class LoadAllcategories extends AsyncTask<String, String, String> {
	 
	      
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            catsp.setEnabled(false);

	        }
	 
	       
	        protected String doInBackground(String... args) {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            // getting JSON string from URL
	            JSONObject json = jParser.makeHttpRequest(url_all_categories, "GET", params);
	 
	            // Check your log cat for JSON reponse
	            Log.d("All categories: ", json.toString());
	 
	            try {
	                // Checking for SUCCESS TAG
	                int success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1) {
	                    // categories found
	                    // Getting Array of categories
	                    categories = json.getJSONArray(TAG_CATEGORIES);
	 
	                    // looping through All categories
	                    for (int i = 0; i < categories.length(); i++) {
	                        JSONObject c = categories.getJSONObject(i);
	 
	                        // Storing each json item in variable
	                        String name = c.getString(TAG_CNAME);
	 
	                        // creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();
	 
	                        // adding each child node to HashMap key => value
	                        map.put(TAG_CNAME, name);
	 
	                        // adding HashList to ArrayList
	                        categoriesList.add(name);
	                    }
	                } 
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	 
	            return null;
	        }
	 
	
	        protected void onPostExecute(String file_url) {
	            // updating UI from Background Thread
	            getActivity().runOnUiThread(new Runnable() {
	                public void run() {
	                	 ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddEventFragment.this.getActivity(),
	                             android.R.layout.simple_dropdown_item_1line, categoriesList);
	                     catsp.setEnabled(true);
	                     catsp.setAdapter(adapter);

	                    
	                }
	            });
	            
	        }
	 
	    }
	   
	   class LoadEvents extends AsyncTask<String, String, String> {
			 
		      
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            evesp.setEnabled(false);

	        }
	 
	       
	        protected String doInBackground(String... args) {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("cname", cname));
	            // getting JSON string from URL
	            JSONObject json = jParser.makeHttpRequest(url_events, "GET", params);
	 
	            // Check your log cat for JSON reponse
	            Log.d("All events: ", json.toString());
	 
	            try {
	                // Checking for SUCCESS TAG
	                int success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1) {
	                    // categories found
	                    // Getting Array of categories
	                    categories = json.getJSONArray(TAG_EVENTS);
	 
	                    // looping through All categories
	                    for (int i = 0; i < categories.length(); i++) {
	                        JSONObject c = categories.getJSONObject(i);
	 
	                        // Storing each json item in variable
	                        String name = c.getString(TAG_ENAME);
	                        String eid = c.getString(TAG_EID);
	                        // creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();
	 
	                        // adding each child node to HashMap key => value
	                        map.put(TAG_ENAME, name);
	                        map.put(TAG_EID, eid);
	                        // adding HashList to ArrayList
	                        eventList.add(map);
	                    }
	                } 
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	 
	            return null;
	        }
	 
	
	        protected void onPostExecute(String file_url) {
	            // updating UI from Background Thread
	            getActivity().runOnUiThread(new Runnable() {
	                public void run() {
	                	 /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddEventFragment.this.getActivity(),
	                             android.R.layout.simple_dropdown_item_1line, eventList);
	                     evesp.setAdapter(adapter);*/
	                     
	                     ListAdapter adapter = new SimpleAdapter(
		                            AddEventFragment.this.getActivity(), eventList,
		                            R.layout.spinner_item, new String[] { TAG_EID,
		                                    TAG_ENAME},
		                            new int[] { R.id.id, R.id.name });
		                    // updating listview
	                     evesp.setEnabled(true);
		                 evesp.setAdapter((SpinnerAdapter) adapter);

	                    
	                }
	            });
	            
	        }
	 
	    }
}
