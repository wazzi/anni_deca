package wazza.ke.servista.views;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import wazza.ke.servista.R;
import wazza.ke.servista.utilities.Assignment;
import wazza.ke.servista.utilities.CalendarEvent;
import wazza.ke.servista.utilities.LimeService;


public class EventsViewer extends ActionBarActivity {

    ListView listView;
    CalendarEvent event;
    ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();

    //Tags
    private static final String EXP_TAG = "EVENTS_VIEW_EXCEPTION";
    private static final String ERR_TAG = "EVENTS_VIEW_ERROR";
    private static final String INFO_TAG = "EVENTS_VIEW_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_viewer);
        //get the bundle from the intent that called this activity
        Bundle data = getIntent().getExtras();
        Log.i(INFO_TAG, (data == null ? "events data bundle is null" : "data bundle not null"));
        events = data.getParcelableArrayList("key.events");

        //get the listViewer and set the custom adapter on it.
        listView = (ListView) findViewById(R.id.eventslistview);
        EventsListAdapter adapter = new EventsListAdapter(events);
        listView.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class EventsListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private ArrayList<CalendarEvent> list;
        private LayoutInflater inflater;
        private CalendarEvent calendarEvent;

        public EventsListAdapter(ArrayList<CalendarEvent> events) {
            list = events;
            Log.i(INFO_TAG, "EventsAdapter constructor: size of list" + String.valueOf(list.size()));
            inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View row = view;
            ViewHolder viewTag;
            if (row == null) {
                row = inflater.inflate(R.layout.single_event_item, viewGroup, false);
                /**viewviewTag to contain the elements in the single item xml file*/
                viewTag = new ViewHolder(row);
                //set the tag using the inflater
                row.setTag(viewTag);
            } else {
                viewTag = (ViewHolder) row.getTag();
            }

            //populate the viewTag for each row
            if (list.size() <= 0) {
                Log.i(INFO_TAG, "Size of list items: " + String.valueOf(list.size()));
                viewTag.eventTitle.setText("No Data");
                viewTag.eventDetails.setText("No Data");
                row.setTag(viewTag);
            } else {
                calendarEvent = list.get(i);
                /**set the list items in viewTag elements*/
                Log.i(INFO_TAG, "Size of list items: " + String.valueOf(list.size()));
                viewTag.eventTitle.setText(calendarEvent.getEventName());
                String startsOn=String.valueOf(calendarEvent.getEventStartDate());
                String endsOn=String.valueOf(calendarEvent.getEventEndDate());
                /**test dating...btw there's this girl I saw...*/

                Long sDate=calendarEvent.getEventStartDate()+System.currentTimeMillis();
                GregorianCalendar gc=new GregorianCalendar();
                gc.setTimeInMillis(sDate);
                viewTag.eventDetails.setText(Html.fromHtml(calendarEvent.getEventId()) + "Due On: " + gc.getTime().toString());
//                viewTag.assignmentGrade.setText(currentUnit.getFullName());
                Log.i(INFO_TAG, "current fullName" + viewTag.eventTitle.getText());
                row.setTag(viewTag);
            }
            this.notifyDataSetChanged();
            return row;
        }

        /**View viewTag for rows*/

        class ViewHolder {
            public TextView eventTitle;
            public TextView eventDetails;
//            public WebView webView;

            //public TextView assignmentGrade;
            public ViewHolder(View v) {
                eventTitle = (TextView) v.findViewById(R.id.txtEventName);
                eventDetails = (TextView) v.findViewById(R.id.txtEventDetails);
                eventDetails.setMovementMethod(LinkMovementMethod.getInstance());
                eventTitle.setTextColor(Color.BLUE);
                eventDetails.setTextColor(Color.BLACK);
            }
        }

        //list item interactivity...
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    }
}
