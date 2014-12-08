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
public class UnitsParser {

    //Loggers
    private static final String EXP_TAG = "UPARSE_EXCEPTION";
    private static final String ERR_TAG = "UPARSE_ERROR";
    private static final String INFO_TAG ="UPARSE_INFO";

    private ArrayList<Unit> units;
    private Unit unit;
    private String keyFlag;
    private XmlPullParser xpp;

    private String unitName;
    private String code;
    private String summary;

    /**
     * Create XML parser
     */
    public UnitsParser() {
        units = new ArrayList<Unit>();
        unit = new Unit();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            xpp = factory.newPullParser();
            Log.i(INFO_TAG, "Created parser");
        } catch (XmlPullParserException e) {
            Log.d(EXP_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get course details from the moodle web service function: get_course_content
     */
    public ArrayList<Unit> parseDocument(String document) {
        if (document == null) {
            Log.e(ERR_TAG, "The string passed is null");
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

                        //if tag name is 'single'...
                        if (startTagName.equals("SINGLE") && xpp.getDepth() == 3) {
                            Log.d(INFO_TAG, "Created new unit instance...");
                            unit = new Unit();
                        }
                        //if tag name is 'key'...
                        else if (startTagName.equals("KEY")) {

                            String attribName = xpp.getAttributeValue(null, "name");
                            Log.i(INFO_TAG, "Current Attrib: " + attribName);
                            if (attribName.equals("fullname")) {
                                Log.i(INFO_TAG, "attribute name: fullname found");
                                keyFlag = "fullname";
                            } else if (attribName.equals("idnumber")) {
                                Log.i(INFO_TAG, "attribute name: idnumber found");
                                keyFlag = "idnumber";
                            }
                            else if (attribName.equals("summary")) {
                                Log.i(INFO_TAG, "attribute name: summary found");
                                keyFlag = "summary";
                            }
                            else {
                                keyFlag = "none";
                            }
                            Log.i(INFO_TAG, "Value of flag: " + keyFlag);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("SINGLE") && xpp.getDepth() == 3) {
                            Log.i(INFO_TAG, "Depth of the end tag: " + String.valueOf(xpp.getDepth()));
                            Log.i(INFO_TAG, "Name of the end tag: " + xpp.getName());
                            unit.setFullName(unitName);
                            unit.setCode(code);
                            unit.setDescription(summary);
                            units.add(unit);
                            Log.i(INFO_TAG, "Added unit with code:" + unit.getCode() + " and Name " + unit.getFullName());
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (keyFlag.equals("fullname")) {
                            Log.i(INFO_TAG, "Value of text: " + xpp.getText());
                            unitName = xpp.getText();

                        } else if (keyFlag.equals("idnumber")) {
                            Log.i(INFO_TAG, "Value of id: " + xpp.getText());
                            code = xpp.getText();
                        }
                        else if (keyFlag.equals("summary")) {
                            Log.i(INFO_TAG, "Value of id: " + xpp.getText());
                            summary = xpp.getText();
                        }
                        break;
                    default:
                        break;
                }
                eventType = xpp.next();
            }

            int count = units.size();
            System.out.println("number of units: " + count);
        } catch (XmlPullParserException e) {
            Log.e(EXP_TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(EXP_TAG, e.getMessage());
        }
        return units;
    }

    /**
     * Test utility
     * Get the number of units retrived from Moodle
     *
     * @param units
     */
    public void getUnitCount(ArrayList<Unit> units) {
        if (units != null)
            for (Unit u : units) {
                Log.i(INFO_TAG, u.getFullName());
                Log.i(INFO_TAG, u.getCode());
            }
        else {
            Log.e(ERR_TAG, "List(units) is null");
        }
    }
}
