package com.example.user.a403final;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by User on 11/29/2016.
 */

public class CourseGrid extends AppCompatActivity {
    private String course_prefix; // Passed from main activity
    private String apiExtension;
    private String cn;
    private String prefix;
    private String desc;
    private String title;
    private String instructorName;
    private int credits;
    private String building;
    private String roomNum;
    private String startTime;
    private String endTime;
    private String days;
    private String term;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_grid);

        // Create db object
        myDb = new DatabaseHelper(this);
        myDb.restartDb(myDb);

        // Bundle to get the extra data sent along with the intent
        Bundle bundle = getIntent().getExtras();

        course_prefix = bundle.getString("buttonText");

        // Build the api query extension
        apiExtension = "?prefix=" + course_prefix;

        // Query api
        new JSONTask().execute("https://api.svsu.edu/courses"+apiExtension);
    }

    // Build list of all classes for the selected major
    public void buildList() {

        Cursor cr = myDb.retrieveData(myDb);
        cr.moveToFirst();

        // Create an array of prefix+coursenumber combinations to avoid adding duplicates to the StringBuffer
        ArrayList<String> Values = new ArrayList<String>();

        // LinearLayout to add buttons to for each course
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll);

        do {
            // Check that a course doesn't already have a button
            if (!Values.contains(cr.getString(1)+cr.getString(2)) && cr.getString(1).equals(course_prefix)) {
                // Add the course so it isn't added again
                Values.add(cr.getString(1)+cr.getString(2));
                String buttonTxt = cr.getString(1)+cr.getString(2)+"\n"+cr.getString(4)+"\n"+cr.getString(6)+" Credits";
                Button btn = new Button(this);
                btn.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                btn.setId(cr.getInt(0));
                Log.d("","cr.getInt = "+cr.getInt(0));
                btn.setTag(cr.getString(1)+cr.getString(2));
                btn.setText(buttonTxt);
                btn.setTextSize(15);
                btn.setBackgroundResource(R.drawable.buttonshape);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer id = v.getId();
                        String str_id = id.toString();
                        getCourseDetail(str_id);
                    }
                });
                // Add the button to the view
                ll.addView(btn);
            }
        }while(cr.moveToNext());
    }

    public void getCourseDetail(String id) {
        Intent intent = new Intent(this, CourseDetail.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    // Go back to MainActivity upon button click
    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // AsyncTask used to gather JSON objects from the api query
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // URL for the api site
                URL url = new URL(params[0]);
                try {
                    // Create a connection to the page
                    connection = (HttpURLConnection)url.openConnection();
                    connection.connect();

                    // Create an input stream to read from the page
                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    // Buffer to store read data
                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    // Loop through and read results line by line
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    // Convert the buffer to a string
                    String finalJson = buffer.toString();
                    // Create a new buffer for the final output
                    StringBuffer finalBufferedData = new StringBuffer();
                    JSONObject parentObject = null;

                    try {
                        // Create a JSON object from the buffer string
                        parentObject = new JSONObject(finalJson);
                        // Create an array from the JSON object
                        JSONArray parentArray = parentObject.getJSONArray("courses");

                        // Loop through the JSON object
                        for (int i = 0; i < parentArray.length(); i++) {
                            JSONObject parentObj = parentArray.getJSONObject(i);
                            JSONArray instructorsArray =  parentObj.getJSONArray("instructors");
                            JSONObject instructorsObj = instructorsArray.getJSONObject(0);
                            JSONArray locationArray =  parentObj.getJSONArray("meetingTimes");
                            JSONObject locationObj = locationArray.getJSONObject(0);

                            if (parentObj.has("courseNumber"))
                                cn = parentObj.getString("courseNumber");
                            else
                                cn = "";

                            if (parentObj.has("prefix"))
                                prefix = parentObj.getString("prefix");
                            else
                                prefix = "";

                            if (parentObj.has("description"))
                                desc = parentObj.getString("description");
                            else
                                desc = "";

                            if (parentObj.has("title"))
                                title = parentObj.getString("title");
                            else
                                title = "";

                            if (parentObj.has("credit"))
                                credits = parentObj.getInt("credit");
                            else
                                credits = 0;

                            if (parentObj.has("credit"))
                                term = parentObj.getString("term");
                            else
                                term = "";

                            if (instructorsObj.has("name"))
                                instructorName = instructorsObj.getString("name");
                            else
                                instructorName = "";

                            String method = locationObj.getString("method");

                            if (!method.equals("ONL")) {
                                if (locationObj.has("building"))
                                    building = locationObj.getString("building");
                                else
                                    building = "";

                                if(locationObj.has("room"))
                                    roomNum = locationObj.getString("room");
                                else
                                    roomNum = "";

                                if(locationObj.has("startTime"))
                                    startTime = locationObj.getString("startTime");
                                else
                                    startTime = "";


                                if(locationObj.has("endTime"))
                                    endTime = locationObj.getString("endTime");
                                else
                                    endTime = "";

                                if(locationObj.has("days"))
                                    days = locationObj.getString("days");
                                else
                                    days = "";
                            } else {
                                building = "";
                                roomNum = "";
                                startTime = "";
                                endTime = "";
                                days = "";
                                term = "";
                            }

                            //Log.d("row", i + "course = "+prefix+cn+" instructor name = "+instructorName+" building = "+building+" roomNum = "+roomNum+" startTime = "+startTime+" endTime = "+endTime+" days = "+days+" term = "+term);

                            myDb.insertData(prefix,cn,desc,title,term,credits,building,roomNum,startTime,endTime,days,instructorName);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Return to the onPostExecute() method
                    //return finalBufferedData.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // Close connection and reader in finally block (If they were initialized)
            finally {
                if(connection != null){
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            buildList();
            //data.setText(result);
        }
    }

}

