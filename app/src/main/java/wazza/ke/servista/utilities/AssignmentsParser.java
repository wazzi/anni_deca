package wazza.ke.servista.utilities;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by kelli on 10/20/14.
 * <p/>
 * Read XML reposnses from Moodle webs service and format to UI
 * on appropriate activity.
 */
public class AssignmentsParser {

    //Loggers
    private static final String EXP_TAG = "APARSE_EXCEPTION";
    private static final String ERR_TAG = "APARSE_ERROR";
    private static final String INFO_TAG = "APARSE_INFO";

    private ArrayList<Assignment> assignments;
    private Assignment assignment;
    private String currentKeyVal="";
    private XmlPullParser xpp;

    private String title;
    private String description;
    private String dueDate;
    private String grade;
    private String submissionsAllowDate;

    /**
     * Create XML parser
     */
    public AssignmentsParser() {
        assignments = new ArrayList<Assignment>();
        assignment=new Assignment();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            xpp = factory.newPullParser();
            Log.i(INFO_TAG, "Created assignments parser");
        } catch (XmlPullParserException e) {
            Log.d(EXP_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get course details from the moodle web service function: get_course_content
     */
    public ArrayList<Assignment> parseDocument(String document) {
        if (document == null) {
            Log.e(ERR_TAG, "xml document (assignments) is null");
            System.exit(1);
        }
        try {
            xpp.setInput(new StringReader(document));
            int eventType = xpp.getEventType();
            //while still inside the xml document...
            while (eventType != xpp.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //get the name of the start tag
                        String startTagName = xpp.getName();
                        Log.i(INFO_TAG, "Name of tag: " + startTagName);
                        Log.i(INFO_TAG, "Depth of tag: " + xpp.getDepth());

                        //if tag name is 'single'...
                        if (startTagName.equals("SINGLE") && xpp.getDepth() ==8) {
                            Log.d(INFO_TAG, "Created new assignment instance...");
                            assignment=new Assignment();
                        }
                        //if tag name is 'key'...
                        else if (startTagName.equals("KEY") && xpp.getDepth()==9) {

                            String attribName = xpp.getAttributeValue(null, "name");
                            Log.i(INFO_TAG, "Current Attrib: " + attribName);
                            if (attribName.equals("name")) {
                                Log.i(INFO_TAG, "attribute name: assignment name found");
                                currentKeyVal = "name";
                            } else if (attribName.equals("duedate")) {
                                Log.i(INFO_TAG, "attribute name: due date found");
                                currentKeyVal = "duedate";
                            } else if (attribName.equals("allowsubmissionsfromdate")) {
                                Log.i(INFO_TAG, "attribute name: allowsubmissionsfromdate found");
                                currentKeyVal = "allowsubmissionsfromdate";
                            } else if (attribName.equals("grade")) {
                                Log.i(INFO_TAG, "attribute name: grade found");
                                currentKeyVal = "grade";
                            } else {
                                currentKeyVal = "none";
                            }
                            Log.i(INFO_TAG, "Value of flag: " + currentKeyVal);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("SINGLE") && xpp.getDepth() == 8) {
                            Log.i(INFO_TAG, "Depth of the end tag: " + String.valueOf(xpp.getDepth()));
                            Log.i(INFO_TAG, "Name of the end tag: " + xpp.getName());
                            assignments.add(assignment);
                            Log.i(INFO_TAG, "Added assignment :" + assignment.getTitle() + " " +
                                    "due on" + assignment.getDueDate());
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (currentKeyVal.equals("name")) {
                            Log.i(INFO_TAG, "Value of text: " + xpp.getText());
                            title=xpp.getText();
                            assignment.setTitle(title);
                        } else if (currentKeyVal.equals("duedate")) {
                            Log.i(INFO_TAG, "Value of id: " + xpp.getText());
                            dueDate=xpp.getText();
                            assignment.setDueDate(dueDate);
                        }else if (currentKeyVal.equals("allowsubmissionsfromdate")) {
                            Log.i(INFO_TAG, "Value of id: " + xpp.getText());
                            submissionsAllowDate=xpp.getText();
                            assignment.setSubmissionsAllowDate(submissionsAllowDate);
                        }else if (currentKeyVal.equals("grade")) {
                            Log.i(INFO_TAG, "Value of id: " + xpp.getText());
                            grade=xpp.getText();
                            assignment.setGrade(grade);
                        }
                        break;
                    default:
                        break;
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(EXP_TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(EXP_TAG, e.getMessage());
        }
        return assignments;
    }

    /**
     * Test utility
     * Get the number of assignments retrived from Moodle
     *
     * @param assignments
     */
    public void getAssignmentCount(ArrayList<Assignment> assignments) {
        if (assignments != null)
            for (Assignment assignment1 : assignments) {
//                Log.i(INFO_TAG, u.getFullName());
//                Log.i(INFO_TAG, u.getCode());
            }
        else {
            Log.e(ERR_TAG, "List(Assignments) is null");
        }
    }
}
