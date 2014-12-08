package wazza.ke.servista.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import wazza.ke.servista.views.Home;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class LimeService extends IntentService {
    //logs
    private static String INFO_LOG="info.my_intentservice";
    private static String ERR_LOG="error.my_intentservice";

    //web service actions
    private static final String ACTION_CONNECT = "wazza.ke.servista.action.CONNECT";
    private static final String ACTION_GET_COURSES = "wazza.ke.servista.action.GET_COURSES";
    private static final String ACTION_GET_CALENDAR_EVENTS = "wazza.ke.servista.action.GET_CALENDAR_EVENTS";
    private static final String ACTION_GET_ASSIGNMENTS = "wazza.ke.servista.action.GET_ASSIGNMENTS";

    //parameters
    private static final String PARAM_USER_NAME= "user_name";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_TOKEN = "security_token";
    private static final String PARAM_FUNCTION = "function";
    private static final String BASE_WEBSERVICE_URL = "http://10.10.11.94/moodle/webservice/rest/server.php?";
//    private static final String BASE_WEBSERVICE_URL = "http://10.0.2.2/moodle/webservice/rest/server.php?";

    //notifications
    public static final String LOGIN_NOTIFICATION = "wazza.service.login.done";
    public static final String COURSES_NOTIFICATION = "wazza.service.courses.done";
    public static final String ASSIGNMENTS_NOTIFICATION = "wazza.service.assignments.done";
    public static final String CALENDAR_EVENTS_NOTIFICATION = "wazza.service.calendar_events.done";
    public static final String PROGRESS_NOTIFICATION = "wazza.service.messages.progress";

    //broadcast identifiers
    public static final int BCAST_LOGIN=1;
    public static final int BCAST_COURSES=2;
    public static final int BCAST_ASSIGNMENTS=3;
    public static final int BCAST_CALENDAR_EVENTS=4;


    /**
     * Starts this service to perform action wsConnect with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void wsConnect(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LimeService.class);
        intent.setAction(ACTION_CONNECT);
        intent.putExtra(PARAM_USER_NAME, param1);
        intent.putExtra(PARAM_PASSWORD, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action wsGetCourses with the given parameters. If
     * the service is already performing a task this action will be queued.
     */

    public static void wsGetCourses (Context context, String param1, String param2) {
        Intent intent = new Intent(context, LimeService.class);
        intent.setAction(ACTION_GET_COURSES);
        intent.putExtra(PARAM_TOKEN, param1);
        intent.putExtra(PARAM_FUNCTION, param2);
        context.startService(intent);
    }

    public LimeService() {
        super("LimeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(INFO_LOG,"inside onHandle intent...");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CONNECT.equals(action)) {
                final String param1 = intent.getStringExtra(PARAM_USER_NAME);
                final String param2 = intent.getStringExtra(PARAM_PASSWORD);
                Log.i(INFO_LOG,"intent has string action connect....");
                handleActionConnect(param1, param2);
            } else if (ACTION_GET_COURSES.equals(action)) {
                final String param1 = intent.getStringExtra(PARAM_TOKEN);
                final String param2 = intent.getStringExtra(PARAM_FUNCTION);
                Log.i(INFO_LOG,"intent has string action get courses....");
                handleActionGetCourses(param1, param2);
            }
            else if(ACTION_GET_ASSIGNMENTS.equals(action)){
                final String param1 = intent.getStringExtra(PARAM_TOKEN);
                final String param2 = intent.getStringExtra(PARAM_FUNCTION);
                Log.i(INFO_LOG,"intent has string action get assignments....");
                handleActionGetAssignments(param1, param2);
            }
            else if(ACTION_GET_CALENDAR_EVENTS.equals(action)){
                final String param1 = intent.getStringExtra(PARAM_TOKEN);
                final String param2 = intent.getStringExtra(PARAM_FUNCTION);
                Log.i(INFO_LOG,"intent has string action get calendar_events....");
                handleActionGetEvents(param1, param2);
            }
        }else{
        Log.e(ERR_LOG, "intent was null");
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionConnect(String param1, String param2) {

        String token;
        token=MoodleWS_Engine.getStreamContent(MoodleWS_Engine.getWSMethodConnection
                ("http://10.10.11.94/moodle/login/token.php?username="+param1+"&password="+param2+"&service=nina"));
//                ("http://10.0.2.2/moodle/login/token.php?username="+param1+"&password="+param2+"&service=nina"));
        publishResults(token,BCAST_LOGIN);
        Log.i(INFO_LOG,"inside handleActionConnect...");
    }

    /**
     * Get courses the user has registered for...kazi mingi hapa!!!
     */
    private void handleActionGetCourses(String param1, String param2) {
        //create url to fetch courses
        Intent progressIntent=new Intent(PROGRESS_NOTIFICATION);
        progressIntent.putExtra("done",false);
        sendBroadcast(progressIntent);
        String url=BASE_WEBSERVICE_URL+"wstoken="+param1+"&wsfunction="+param2;

        String coursesXml=MoodleWS_Engine.getStreamContent(MoodleWS_Engine.getWSMethodConnection(url));
        publishResults(coursesXml,BCAST_COURSES);

        Log.i(INFO_LOG, "Get courses url: " + url);
    }

    private void handleActionGetAssignments(String param1, String param2){
//        show business...while getting content
        Intent progressIntent=new Intent(PROGRESS_NOTIFICATION);
        progressIntent.putExtra("done",false);
        sendBroadcast(progressIntent);
        String url=BASE_WEBSERVICE_URL+"wstoken="+param1+"&wsfunction="+param2;
        String assignmentsXml=MoodleWS_Engine.getStreamContent(MoodleWS_Engine.getWSMethodConnection(url));
        publishResults(assignmentsXml,BCAST_ASSIGNMENTS);

        Log.i(INFO_LOG, "Get assignments url: " + url);
    }

    private void handleActionGetEvents(String param1, String param2){
//        show business...while getting content
        Intent progressIntent=new Intent(PROGRESS_NOTIFICATION);
        progressIntent.putExtra("done",false);
        sendBroadcast(progressIntent);
        String url=BASE_WEBSERVICE_URL+"wstoken="+param1+"&wsfunction="+param2;
//                "&events[eventids][0]=1&events[eventids][1]=5&events[eventids][2]=6&events[eventids][3]=7";

        String calendarEventsXml=MoodleWS_Engine.getStreamContent(MoodleWS_Engine.getWSMethodConnection(url));
        publishResults(calendarEventsXml,BCAST_CALENDAR_EVENTS);

        Log.i(INFO_LOG, "Get events url: " + url);
    }

    //broad cast after completing task
    public void publishResults(String result, int target){

        Intent intent, progressIntent;
        switch (target){
            case BCAST_LOGIN:
                Log.i(INFO_LOG,"broadcasting login notification...");
                intent=new Intent (LOGIN_NOTIFICATION);
                intent.putExtra("service_token", result);
                sendBroadcast(intent);
                break;
            case BCAST_COURSES:
                //dismiss busy dialog...
                progressIntent=new Intent(PROGRESS_NOTIFICATION);
                progressIntent.putExtra("done",true);
                Log.i(INFO_LOG,"get Units has completed..sending dismiss signal");
                sendBroadcast(progressIntent);
                Log.i(INFO_LOG,"broadcasting get_courses notification...");
                intent=new Intent (COURSES_NOTIFICATION);
                intent.putExtra("courses", result);
                sendBroadcast(intent);
                break;
            case BCAST_ASSIGNMENTS:
                //dismiss busy dialog...
                progressIntent=new Intent(PROGRESS_NOTIFICATION);
                progressIntent.putExtra("done",true);
                Log.i(INFO_LOG,"get Assignments has completed..sending dismiss signal");
                sendBroadcast(progressIntent);
                Log.i(INFO_LOG,"broadcasting get_assignments notification...");
                intent=new Intent (ASSIGNMENTS_NOTIFICATION);
                intent.putExtra("assignments", result);
                sendBroadcast(intent);
                break;
            case BCAST_CALENDAR_EVENTS:
                //dismiss busy dialog...
                progressIntent=new Intent(PROGRESS_NOTIFICATION);
                progressIntent.putExtra("done",true);
                Log.i(INFO_LOG,"get CalendarEvents has completed..sending dismiss signal");
                sendBroadcast(progressIntent);
                Log.i(INFO_LOG,"broadcasting get_calendar_events notification...");
                intent=new Intent (CALENDAR_EVENTS_NOTIFICATION);
                intent.putExtra("events", result);
                sendBroadcast(intent);
                break;
        }
    }
}

