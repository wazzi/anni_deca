package wazza.ke.servista.views;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import wazza.ke.servista.R;
import wazza.ke.servista.utilities.Assignment;
import wazza.ke.servista.utilities.AssignmentsParser;
import wazza.ke.servista.utilities.CalendarEvent;
import wazza.ke.servista.utilities.CalendarEventsParser;
import wazza.ke.servista.utilities.UnitsParser;
import wazza.ke.servista.utilities.LimeService;
import wazza.ke.servista.utilities.Unit;

import java.util.ArrayList;



public class Home extends ActionBarActivity {

    //web service actions
    private static final String GET_COURSES = "wazza.ke.servista.action.GET_COURSES";
    private static final String GET_CALENDAR_EVENTS = "wazza.ke.servista.action.GET_CALENDAR_EVENTS";
    private static final String GET_ASSIGNMENTS = "wazza.ke.servista.action.GET_ASSIGNMENTS";

    //Log tags
    private static final String EXP_TAG = "GRID_CTL_EXCEPTION";
    private static final String ERR_TAG = "GRID_CTL_ERROR";
    private static final String INFO_TAG = "GRID_CTL_INFO";

    private GridView gv;
    private ArrayList<Assignment> assignments;
    private ArrayList<Unit> units;
    private ArrayList<CalendarEvent>events;
    private MyHandler mHandler;
    private ProgressDialog progressDialog;

    /**
     * create a broadcast listener for web service results
     */

    private BroadcastReceiver coursesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //call the parser on received xml
            UnitsParser cp = new UnitsParser();
            Bundle bundle = intent.getExtras();
            String courses = bundle.getString("courses");

            units = cp.parseDocument(courses);

            //prepare intent for showing units activity
            Intent i = new Intent(getApplicationContext(), UnitsRegistered.class);
            i.putParcelableArrayListExtra("list", units);
            Log.i(INFO_TAG, "onReceive()_Items in list units" + String.valueOf(units.size()));
            startActivity(i);
        }
    };

    /**Listener for starting of web service task...display dialog*/
    private BroadcastReceiver progressReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(INFO_TAG,"inside progress receiver...");
            //show the dialog
            boolean finished=intent.getBooleanExtra("done",false);
                Log.i(INFO_TAG,"status of finished is: "+finished);
            if(!finished){
                Message m=new Message();
                Bundle b=new Bundle();
                b.putString("status","undone");
                m.setData(b);
                Log.i(INFO_TAG,"Message (1): "+m.getData().get("status"));
                Log.i(INFO_TAG,"mHandler (2): "+(mHandler==null? "is null":"not null"));
                mHandler.sendMessage(m);
            }else{
                Message m=new Message();
                Bundle b=new Bundle();
                b.putString("status","done");
                m.setData(b);
                Log.i(INFO_TAG,"Message (2): "+m.getData().get("status"));
                Log.i(INFO_TAG,"mHandler (2): "+(mHandler==null? "is null":"not null"));
                mHandler.sendMessage(m);
            }
        }
    };

    /**Listens for received 'moodle assignments' xml document, formats and forwards to UI*/
    private BroadcastReceiver assignmentsReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //call the parser on received xml
            AssignmentsParser assignmentsParser=new AssignmentsParser();
            Bundle bundle = intent.getExtras();
            String courses = bundle.getString("assignments");

            assignments = assignmentsParser.parseDocument(courses);
            Log.i(INFO_TAG,"list: assignments= "+(assignments==null? "null":"not null"));
            //prepare intent for showing units activity
            Intent i = new Intent(getApplicationContext(), AssignmentsViewer.class);
            i.putParcelableArrayListExtra("key.assignments", assignments);
            Log.i(INFO_TAG, "onReceive()_Items in list assignments" + String.valueOf(assignments.size()));
            startActivity(i);
        }
    };

    /**Get the events published in moodle calendar */
    private BroadcastReceiver calendarEventsReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //call the parser on received xml
            CalendarEventsParser eventsParser=new CalendarEventsParser();
            Bundle bundle = intent.getExtras();
            String eventsBundle = bundle.getString("events");

            events = eventsParser.parseDocument(eventsBundle);
            Log.i(INFO_TAG,"list: events= "+(events==null? "null":"not null"));
            //prepare intent for showing units activity
            Intent i = new Intent(getApplicationContext(), EventsViewer.class);
            i.putParcelableArrayListExtra("key.events", events);
            Log.i(INFO_TAG, "onReceive()_Items in list events" + String.valueOf(events.size()));
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**This registers itself with the thread in which its created...in this case, the main thread*/
        try{
            Class.forName("android.os.AsynTask");
        }catch(ClassNotFoundException cfe){
            Log.d(INFO_TAG,"Error Loading class");
        }
        super.onCreate(savedInstanceState);
        mHandler= new MyHandler();
        Log.i(INFO_TAG,"created handler thread");
        setContentView(R.layout.activity_home);
        gv = (GridView) findViewById(R.id.gridView);
        AppAdapter adapter = new AppAdapter(this);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(adapter);
    }

    public void onResume() {
        super.onResume();
        registerReceiver(coursesReceiver, new IntentFilter(LimeService.COURSES_NOTIFICATION));
        registerReceiver(progressReceiver, new IntentFilter(LimeService.PROGRESS_NOTIFICATION));
        registerReceiver(assignmentsReceiver, new IntentFilter(LimeService.ASSIGNMENTS_NOTIFICATION));
        registerReceiver(calendarEventsReceiver,new IntentFilter(LimeService.CALENDAR_EVENTS_NOTIFICATION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(coursesReceiver);
        unregisterReceiver(progressReceiver);
        unregisterReceiver(assignmentsReceiver);
        unregisterReceiver(calendarEventsReceiver);
    }

    class AppAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        Context context;
        //create an arrayList of SingleGridItem objects
        ArrayList<SingleGridItem> gridItemsList;

        public AppAdapter(Context context) {

            this.context = context;
            gridItemsList = new ArrayList<SingleGridItem>();
            Resources resources = context.getResources();
            String[] itemsArr = resources.getStringArray(R.array.items);//get the menu item names
            int[] itemImgs = {R.drawable.assignments1, R.drawable.messages1, R.drawable.notify1,
                    R.drawable.profile1, R.drawable.units1};//get images to be used with names

            //use loop to populate the listArray
            for (int i = 0; i < 5; i++) {
                SingleGridItem singleGridItem = new SingleGridItem(itemsArr[i], itemImgs[i]);
                gridItemsList.add(singleGridItem);
            }
        }

        @Override
        public int getCount() {
            return gridItemsList.size();
        }

        @Override
        public Object getItem(int i) {
            return gridItemsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            /**
             *  1. Get the id of selected menu item
             *  2. Determine the web service function to be called
             */
            int selectedMenuItem = i;
            Intent homeIntent = new Intent(Home.this, LimeService.class);
            //get the token stored
            SharedPreferences p = getBaseContext().getSharedPreferences("MOODLE_TOKEN_OBJ", Context.MODE_PRIVATE);
            String token = p.getString("user_token", "No token found");

            switch (selectedMenuItem) {
                case 0://units
                    homeIntent.setAction(GET_COURSES);
                    homeIntent.putExtra("security_token", token);
                    homeIntent.putExtra("function", "core_course_get_courses");
                    startService(homeIntent);
                    break;
                case 1:
                    //messages
                    homeIntent.setAction(GET_CALENDAR_EVENTS);
                    homeIntent.putExtra("security_token", token);
                    homeIntent.putExtra("function", "core_calendar_get_calendar_events");
                    startService(homeIntent);
                    break;
                case 2:
                    //assignments
                    homeIntent.setAction(GET_ASSIGNMENTS);
                    homeIntent.putExtra("security_token", token);
                    homeIntent.putExtra("function", "mod_assign_get_assignments");
                    startService(homeIntent);
                    break;
                case 3:
                    //profile

                    break;
                case 4:
                    //notifications

                    break;
                default:
                    break;

            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View row = view;
            ViewHolder holder;
            //if creating view for the first time...
            if (row == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(R.layout.single_grid_item, viewGroup, false);
                holder = new ViewHolder(row);
                row.setTag(holder);//store the view for use later...

            } else {
                holder = (ViewHolder) row.getTag();
            }
            SingleGridItem current = gridItemsList.get(i);
            holder.imgView.setImageResource(current.imgID);
            holder.textView.setText(current.itemName);
            return row;
        }

        //class to hold all images and avoid recurrent fetching
        class ViewHolder {
            ImageView imgView;
            TextView textView;

            public ViewHolder(View v) {

                imgView = (ImageView) v.findViewById(R.id.imageView);
                textView = (TextView) v.findViewById(R.id.txtCode);

            }
        }
    }   class SingleGridItem {
        private String itemName;
        private int imgID;

        public SingleGridItem(String itemName, int imgID) {
            this.itemName = itemName;
            this.imgID = imgID;
        }
    }

    /**Show the progress dialog from separate thread.*/
//        class Progressor implements Runnable{
//            public void run(){
//                Log.i(INFO_TAG,"inside progressor run");
//                Log.i(INFO_TAG,"showing dialog...");
//
//            }}

    class MyHandler extends android.os.Handler{
        public void handleMessage(Message message){
            super.handleMessage(message);
            Bundle received=message.getData();
                Log.i(INFO_TAG,"Bundle is: "+(received==null? "null":"not null"));
            String taskStatus=received.getString("status");
                Log.i(INFO_TAG,"TaskStatus is: "+taskStatus);
            if(taskStatus.equals("undone")){
                Log.i(INFO_TAG, "status from reciever is undone...");
                progressDialog=ProgressDialog.show(Home.this,"","Getting Units...");
            }else{
                Log.i(INFO_TAG, "sending dismiss message from handler...");
            progressDialog.dismiss();
            }
        }
    }
}

