package wazza.ke.servista.views;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import wazza.ke.servista.R;
import wazza.ke.servista.utilities.Unit;

public class UnitsRegistered extends Activity {

    //Tags
    private static final String EXP_TAG = "UNIT_VIEW_EXCEPTION";
    private static final String ERR_TAG = "UNIT_VIEW_ERROR";
    private static final String INFO_TAG = "UNIT_VIEW_INFO";

    ListView listView;
    ArrayList<Unit> units=new ArrayList<Unit>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units_view);

        //get the bundle from the intent that called this activity
        Bundle data = getIntent().getExtras();
        Log.i(INFO_TAG,(data==null? "data bundle is null":"data bundle not null"));
        units = data.getParcelableArrayList("list");

        //get a reference to the list view object
        listView = (ListView) findViewById(R.id.listView);
        UnitsAdapter adapter = new UnitsAdapter(units);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Create an adapter for the units viewer
     * implement onItemClick listener later
     */

    class UnitsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private ArrayList<Unit> list;
        private Unit currentUnit;
        private LayoutInflater inflater;

        public UnitsAdapter(ArrayList<Unit> units1) {
            list = units1;
            Log.i(INFO_TAG, "UnitsAdapter constructor: size of list" + String.valueOf(list.size()));
            inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        //Get the size of the passed arraylist
        public int getCount() {
            if (list != null) {
                Log.i(INFO_TAG, "Inside getCount: size of list" + String.valueOf(list.size()));
                return list.size();
            } else {
                return 1;
            }
        }

        @Override
        //get the current item in the arraylist
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        //create each row for listview
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View row = view;
            ViewHolder holder;
            if (row == null) {
                row = inflater.inflate(R.layout.single_unit_element, viewGroup, false);

                /**viewholder to contain the elements in the single item xml file*/
                holder = new ViewHolder(row);
                //set the tag using the inflater
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            //populate the holder for each row
            if (list.size() <= 0) {
                Log.i(INFO_TAG, "Size of list items: " + String.valueOf(list.size()));
                holder.currentName.setText("No Data");
                holder.currentCode.setText("No Data");
                holder.currentDescription.setText("No Data");
                row.setTag(holder);
            } else {
                currentUnit = list.get(i);
                /**set the list items in holder elements*/
                Log.i(INFO_TAG, "Size of list items: " + String.valueOf(list.size()));
                holder.currentName.setText(currentUnit.getCode());
                holder.currentCode.setText(currentUnit.getFullName());
                holder.currentDescription.setText(Html.fromHtml(currentUnit.getDescription()));
                Log.i(INFO_TAG, "current fullName" + holder.currentName.getText());
                row.setTag(holder);
            }
            return row;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }

        //create a viewholder object to contain inflated xml objects
        class ViewHolder {
            public TextView currentName;
            public TextView currentCode;
            public TextView currentDescription;

            public ViewHolder(View v) {
                currentName = (TextView) v.findViewById(R.id.txtFullname);
                currentCode = (TextView) v.findViewById(R.id.txtCode);
                currentDescription=(TextView)v.findViewById(R.id.txtDescription);
                currentDescription.setTextColor(Color.BLUE);
                currentName.setTextColor(Color.BLACK);

            }
        }
    }

}
