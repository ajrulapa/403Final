package com.example.user.a403final;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by User on 12/4/2016.
 */

public class CourseDetail extends AppCompatActivity {

    private static final String semesterChar1 = "\\";
    private static final String semesterChar2 = "/";

    private String courseName;
    private String courseDesc;
    private String year;
    private String semester;
    private String building;
    private String term;
    private String startTime;
    private String endTime;
    private String days;
    private String room;
    private String instructor;
    private String imageName;

    private DatabaseHelper db;

    public CourseDetail() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Create db object
        db = new DatabaseHelper(this);

        // Get extras which include table id
        Bundle bundle = getIntent().getExtras();
        Log.d("","id = "+ bundle.getString("id"));

        // Get all db rows that match the prefix+coursenumber combination
        Cursor cr = db.retrieveCourseData(db, bundle.getString("id"));

        // Start at the first element in the first row
        cr.moveToFirst();

        getQueryResults(cr);

        // Add the course prefix and course number to the top most text view
        // Add the course description under it
        TextView cn = (TextView)findViewById(R.id.course_name);
        cn.append(courseName);
        TextView cd = (TextView)findViewById(R.id.course_desc);
        cd.append(courseDesc);

        LinearLayout ll = (LinearLayout)findViewById(R.id.section_layout);

        // Position the cursor back at the start
        cr.moveToFirst();

        int cnt = 0;

        do {
            getQueryResults(cr);

            cnt += 1;
            TextView tv = new TextView(this);
            tv.setId(cnt);

            // Add a divider
            Button btn = new Button(this);
            btn.setBackgroundResource(R.drawable.divider);
            ll.addView(btn);

            StringBuffer buffer = new StringBuffer();

            // Break up term based on the \/
            getTerms();

            getDays();

            buffer.append("\n"+instructor);
            tv.append(buffer.toString());

            String name = "";

            // Get the instructors last name
            if(instructor.contains(".") || instructor.contains(" ")) {
                String[] names = instructor.split(" ");
                name = names[1];
            } else {
                name = instructor;
            }

            name = name.toUpperCase().replaceAll("\\s+","");

            getImageName(name);

            String uri = "@drawable/"+imageName;  // where myresource (without the extension) is the file

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());

            // Add the professors picture to the view
            //ImageView image = (ImageView)findViewById(R.id.prof_img);
            ImageView im = new ImageView(this);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(900,800);
            im.setId(cnt);
            Drawable res = getResources().getDrawable(imageResource);
            im.setImageDrawable(res);
            im.setLayoutParams(params);

            // Add views
            ll.addView(im);
            ll.addView(tv);

            StringBuffer buffer2 = new StringBuffer();
            buffer2.append("\n"+semester + " " + year + "\n");
            buffer2.append(building + room + "\n");
            buffer2.append(days + "\n");
            buffer2.append(startTime + "-" + endTime+"\n\n");

            // Set the textView
            tv.append(buffer2.toString());
            tv.setTextSize(getResources().getDimension(R.dimen.textsize));
            tv.setTextColor(Color.BLACK);
        }while(cr.moveToNext());

    }

    private void getTerms() {
        String[] termParts = term.split(semesterChar1+semesterChar2);

        year = "20"+termParts[0];
        semester = "";

        // Get the semester name
        switch(termParts[1]){
            case "WI":
                semester = "Winter";
                break;
            case "FA":
                semester = "Fall";
                break;
            case "SP":
                semester = "Spring";
                break;
            case "SU":
                semester = "Summer";
                break;
            default:
                semester = "Not Available";
                break;
        }
    }

    private void getDays(){
        days = days.toUpperCase().replaceAll("\\s+","");
        String[] daysParts = days.split("");
        days = "";
        // Get the actual days of the week
        for(int i = 1; i < daysParts.length; i++) {
            switch (daysParts[i]) {
                case "M":
                    days = days + "Monday/";
                    break;
                case "T":
                    days = days + "Tuesday/";
                    break;
                case "W":
                    days = days + "Wednesday/";
                    break;
                case "R":
                    days = days + "Thursday/";
                    break;
                case "F":
                    days = days + "Friday/";
                    break;
                case "S":
                    days = days + "Saturday/";
                    break;
                default:
                    days = days + "Sunday/";
                    break;
            }

        }

        // Remove the last / from the days string
        days = days.substring(0,days.length()-1);
    }

    private void getImageName(String name) {

        // Get the instructors picture if they have one
        if(name.equals("BEYERS"))
            imageName = "beyers";
        else if(name.equals("BIDGOLI"))
            imageName = "bidgoli";
        else if(name.equals("CHO"))
            imageName = "cho";
        else if(name.equals("CORSER"))
            imageName = "corser";
        else if(name.equals("DHARAM"))
            imageName = "dharam";
        else if(name.equals("RAHMAN"))
            imageName = "rahman";
        else if(name.equals("STACKHOUSE"))
            imageName = "stackhouse";
        else
            imageName = "noimage";
    }

    private void getQueryResults(Cursor cr){
        courseName = cr.getString(1)+cr.getString(2);
        courseDesc = "    " + cr.getString(3);
        term = cr.getString(5);
        building = cr.getString(7);
        room = cr.getString(8);
        startTime = cr.getString(9);
        endTime = cr.getString(10);
        days = cr.getString(11);
        instructor = cr.getString(12);
    }
}
