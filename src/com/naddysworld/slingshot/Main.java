/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *                                                                            *                                                                         *
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2011, Motorola Mobility, Inc. All rights reserved.           *
 *                                                                            *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */

package com.naddysworld.slingshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.naddysworld.slingshot.R;

public class Main extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// action bar substitutes the title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.ablayout);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(prefs.getString("username", "").equals("") || prefs.getString("password", "").equals("") )
			startActivity(new Intent(this, Preferences.class));
		else
			new UpdateStationsTask().execute();
	}

	public void onResume()
	{
		super.onResume();
		new UpdateStationsTask().execute();
	}
	
	public void onActionBarItemSelected(View v) {

		// Example of how to handle action bar item selections.
		// Adjust, insert and/or remove case statements for your menu items
		// accordingly.
		switch (v.getId()) {
		// when app icon in action bar or action bar title is clicked, show
		// home/main activity
		case R.id.applicationIcon:
		case R.id.actionBarTitle:
			Intent intent = new Intent(this,
					com.naddysworld.slingshot.Main.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.menuItem1:
			startActivity(new Intent(this, Preferences.class));
			break;
		case R.id.menuItem2:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			if(prefs.getString("username", "").equals("") || prefs.getString("password", "").equals("") )
				startActivity(new Intent(this, Preferences.class));
			else
				new UpdateStationsTask().execute();
			break;
		default:
		}

	}

	private class UpdateStationsTask extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void ...arg0) {
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			boolean allowed = true;
			if (prefs.getBoolean("wifi", false) && !mWifi.isConnected()) {
			    allowed = false;
			}
			if(!allowed)
			{
				publishProgress(1);
				return null;
			}
			StringBuilder builder = new StringBuilder();
		    HttpClient client = new DefaultHttpClient();
		    HttpGet httpGet = new HttpGet("https://www.slingshot.co.nz/MyAccount/api/?username=" + prefs.getString("username", "null") + "&pwd=" + prefs.getString("password", "null"));
		    try {
		      HttpResponse response = client.execute(httpGet);
		      StatusLine statusLine = response.getStatusLine();
		      int statusCode = statusLine.getStatusCode();
		      if (statusCode == 200) {
		        HttpEntity entity = response.getEntity();
		        InputStream content = entity.getContent();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		        String line;
		        while ((line = reader.readLine()) != null) {
		          builder.append(line);
		        }
		      } else {
		    	publishProgress(2);
		    	builder = new StringBuilder();
		    	builder.append("noconnect");
		        Log.e(Main.class.toString(), "Failed to download file");
		      }
		    } catch (ClientProtocolException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    return builder.toString();
		}
		protected void onProgressUpdate(Integer... i) {
		  if(i[0].equals(1)) {
			  Toast.makeText(getApplicationContext(), "Wifi Connection required but not available", Toast.LENGTH_LONG).show();
		  }
		  if(i[0].equals(2)) {
			  ArrayList<SlingshotUsageItem> searchResults = new ArrayList<SlingshotUsageItem>();
				
			 searchResults.add(new SlingshotUsageItem("Could not connect to Slingshot", "",""));
			 final ListView lv = (ListView)findViewById(R.id.listView1);
				
			 lv.setAdapter(new SlingshotAdapter(getApplicationContext(), searchResults));
		  }
		}
		protected void onPostExecute(String jsa)
		{
			if(jsa == null) return;
			ArrayList<SlingshotUsageItem> searchResults = new ArrayList<SlingshotUsageItem>();
			
			if(jsa.contains("valid")) { searchResults.add(new SlingshotUsageItem("Invalid Account Details", "","")); }
			else if(jsa.equals("") || jsa.equals("noconnect") || ( jsa.length() >= 4 && !jsa.substring(0, 4).equals("Time") )) { searchResults.add(new SlingshotUsageItem("Could not connect to Slingshot", "","")); }
			else {
				String[] res = jsa.split(",");
				HashMap<String, String> r = new HashMap<String, String>();
				for(String s : res)
				{
					r.put(s.split("=")[0],s.split("=")[1]);
				}
				
				if(r.get("DataQuotaGB").equals("0"))
				{
					if(r.get("TodayDataSentTotalGB") != null && r.get("TodayDataRcvdTotalGB") != null)
					{
						Float ayce = Float.parseFloat(r.get("TodayDataSentTotalGB")) + Float.parseFloat(r.get("TodayDataRcvdTotalGB"));
						searchResults.add(new SlingshotUsageItem("Usage Today", ayce.toString(),"GB"));
					}
					else searchResults.add(new SlingshotUsageItem("Usage Today", "Fetch Error",""));
				}
				else
				{
					searchResults.add(new SlingshotUsageItem("Quota", r.get("DataQuotaGB"),"GB"));
					
					float percent;
					if(Float.parseFloat(r.get("DataQuotaGB")) == 0f)
						percent = 0;
					else
						percent = ( Float.parseFloat(r.get("DataUsedGB"))/ Float.parseFloat(r.get("DataQuotaGB"))) * 100;
					
					searchResults.add(new SlingshotUsageItem("Used", r.get("DataUsedGB"),"GB", View.VISIBLE, percent));
					searchResults.add(new SlingshotUsageItem("Remaining", (Double.parseDouble(r.get("DataQuotaGB")) - Double.parseDouble(r.get("DataUsedGB"))) + "" ,"GB"));
					searchResults.add(new SlingshotUsageItem("Off Peak", r.get("DataOffPeakGB"),"GB"));
				}
				if(!r.get("iTalkMinutes").equals("0"))
				{
					searchResults.add(new SlingshotUsageItem("iTalk Min", r.get("iTalkMinutes"), ""));
				}
				searchResults.add(new SlingshotUsageItem("Account #", r.get("Account"),""));
				searchResults.add(new SlingshotUsageItem("Next Bill", r.get("NextBill"), ""));
			}
			final ListView lv = (ListView)findViewById(R.id.listView1);
			
			lv.setAdapter(new SlingshotAdapter(getApplicationContext(), searchResults));
		}

	}
	
	
}