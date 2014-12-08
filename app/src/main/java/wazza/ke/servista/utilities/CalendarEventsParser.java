package wazza.ke.servista.utilities;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by kelli on 11/20/14.
 */
public class CalendarEventsParser {
    //Loggers
    private static final String EXP_TAG = "CALEV_EXCEPTION";
    private static final String ERR_TAG = "CALEV_ERROR";
    private static final String INFO_TAG = "CALEV_INFO";

    private ArrayList<CalendarEvent> eventsList;
    private CalendarEvent calendarEvent;
    private String currentKeyVal="";
    private XmlPullParser xpp;

    private String eventName;
    private String eventDescription;
    private String eventStartDate;
    private String eventEndDate;
    private String eventId;

    /**
     * Create XML parser
     */
    public CalendarEventsParser() {
        eventsList = new ArrayList<CalendarEvent>();
        calendarEvent=new CalendarEvent();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            xpp = factory.newPullParser();
            Log.i(INFO_TAG, "Created events parser");
        } catch (XmlPullParserException e) {
            Log.d(EXP_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get course details from the moodle web service function: get_course_content
     */
    public ArrayList<CalendarEvent> parseDocument(String document) {
        if (document == null) {
            Log.e(ERR_TAG, "xml document (calendarEvents) is null");
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
                        if (startTagName.equals("SINGLE") && xpp.getDepth() ==5) {
                            Log.d(INFO_TAG, "Created new event instance...");
                            calendarEvent=new CalendarEvent();
                        }
                        //if tag name is 'key'...
                        else if (startTagName.equals("KEY") && xpp.getDepth()==6) {

                            String attribName = xpp.getAttributeValue(null, "name");
                            Log.i(INFO_TAG, "Current Attribute value: " + attribName);
                            if (attribName.equals("name")) {
                                Log.i(INFO_TAG, "Event title found");
                                currentKeyVal = "name";
                            } else if (attribName.equals("description")) {
                                Log.i(INFO_TAG, "Found event description");
                                currentKeyVal = "description";
                            } else if (attribName.equals("timestart")) {
                                Log.i(INFO_TAG, "Found event start");
                                currentKeyVal = "timestart";
                            } else if (attribName.equals("timeduration")) {
                                Log.i(INFO_TAG, "Found event duration");
                                currentKeyVal = "timeduration";
                            } else {
                                currentKeyVal = "none";
                            }
                            Log.i(INFO_TAG, "Value of flag: " + currentKeyVal);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("SINGLE") && xpp.getDepth() == 5) {
                            Log.i(INFO_TAG, "Depth of the end tag: " + String.valueOf(xpp.getDepth()));
                            Log.i(INFO_TAG, "Name of the end tag: " + xpp.getName());
                            eventsList.add(calendarEvent);
                            Log.i(INFO_TAG, "Added calendarEvent :" + calendarEvent.getEventName()+ " " +
                                    "starts on" + calendarEvent.getEventStartDate());
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (currentKeyVal.equals("name")) {
                            Log.i(INFO_TAG, "Value of text: " + xpp.getText());
                            eventName=xpp.getText();
                            calendarEvent.setEventName(eventName);
                        } else if (currentKeyVal.equals("description")) {
                            Log.i(INFO_TAG, "Value of id: " + xpp.getText());
                            eventDescription=xpp.getText();
                            calendarEvent.setEventId(eventDescription);
                        }else if (currentKeyVal.equals("timestart")) {
                            Log.i(INFO_TAG, "Value of id: " + xpp.getText());
                            eventStartDate=xpp.getText();
                            Long startDate=Long.parseLong(eventStartDate);
                            calendarEvent.setEventStartDate(startDate);
                        }else if (currentKeyVal.equals("timeduration")) {
                            eventEndDate=xpp.getText();
                            Long endDate=Long.parseLong(eventEndDate);
                            Log.i(INFO_TAG, "Value of id: " + xpp.getText());
                            calendarEvent.setEventEndDate(endDate);
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
        return eventsList;
    }

    /**
     * Test utility
     * Get the number of assignments retrived from Moodle
     *
     * @param events
     */
    public void getAssignmentCount(ArrayList<CalendarEvent> events) {
        if (events != null)
            for (CalendarEvent ce: events) {
//                Log.i(INFO_TAG, u.getFullName());
//                Log.i(INFO_TAG, u.getCode());
            }
        else {
            Log.e(ERR_TAG, "List(CalendarEvents) is null");
        }
    }
}
