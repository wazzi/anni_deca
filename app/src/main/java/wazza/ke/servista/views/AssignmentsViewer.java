package wazza.ke.servista.views;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import wazza.ke.servista.R;
import wazza.ke.servista.utilities.Assignment;
import wazza.ke.servista.utilities.Unit;

public class AssignmentsViewer extends ActionBarActivity {

    //Tags
    private static final String EXP_TAG = "ASSIGNM_VIEW_EXCEPTION";
    private static final String ERR_TAG = "ASSIGNM_VIEW_ERROR";
    private static final String INFO_TAG = "ASSIGNM_VIEW_INFO";

    ListView listView;
    Assignment currentAssignment;
    ArrayList<Assignment> userAssignments=new ArrayList<Assignment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_viewer);

        //get the bundle from the intent that called this activity
        Bundle data = getIntent().getExtras();
        Log.i(INFO_TAG, (data == null ? "data bundle is null" : "data bundle not null"));
        userAssignments = data.getParcelableArrayList("key.assignments");

        //get a reference to the list view object
        listView = (ListView) findViewById(R.id.assignListView);
        AssignmentAdapter adapter = new AssignmentAdapter(userAssignments);
        listView.setAdapter(adapter);
    }

    class AssignmentAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private ArrayList<Assignment>list;
        private Unit currentUnit;
        private LayoutInflater inflater;

        public AssignmentAdapter(ArrayList<Assignment> assignments) {
            list = assignments;
            Log.i(INFO_TAG, "AssignmentAdapter constructor: size of list" + String.valueOf(list.size()));
            inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        //Get the size of the passed arraylist
        public int getCount() {
            if (list != null) {
                Log.i(INFO_TAG, "Inside getCount: size of list(Assignments)" + String.valueOf(list.size()));
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
                row = inflater.inflate(R.layout.single_assignment_item, viewGroup, false);

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
                holder.assignmentTitle.setText("No Data");
//                holder.assignmentGrade.setText("No Data");
                row.setTag(holder);
            } else {
                currentAssignment = list.get(i);
                /**set the list items in holder elements*/
                Log.i(INFO_TAG, "Size of list items: " + String.valueOf(list.size()));
                holder.assignmentTitle.setText(currentAssignment.getTitle());
//                holder.assignmentGrade.setText(currentUnit.getFullName());
                Log.i(INFO_TAG, "current fullName" + holder.assignmentTitle.getText());
                row.setTag(holder);
            }
                this.notifyDataSetChanged();
            return row;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }

        //create a viewholder object to contain inflated xml objects
        class ViewHolder {
            public TextView assignmentTitle;
//            public TextView assignmentGrade;

            public ViewHolder(View v) {

                assignmentTitle = (TextView) v.findViewById(R.id.assignTextView);
//                assignmentGrade = (TextView) v.findViewById(R.id.txtCode);

            }
        }
    }


}
