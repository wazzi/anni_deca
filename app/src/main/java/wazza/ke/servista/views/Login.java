package wazza.ke.servista.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import wazza.ke.servista.R;
import wazza.ke.servista.utilities.LimeService;


public class Login extends ActionBarActivity {

    private String INFO_LOG="info.my_activity";
    private String ERR_LOG="error.my_activity";

    private TextView tUser, tPass;

    private static String WS_CONNNECTION="wazza.ke.servista.action.CONNECT";
    SharedPreferences preferences;

    /** Receive notification on completing login attempt, save the auth details and
     * show main UI.
     */
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String tokenValue=null;
        Log.i(INFO_LOG, "inside onReceive...");
            Bundle bundle=intent.getExtras();
            if(bundle!=null){
                String token=bundle.getString("service_token");
                preferences= context.getSharedPreferences("MOODLE_TOKEN_OBJ", Context.MODE_PRIVATE);

                //write token for future use.
                if (!token.isEmpty() && token.contains("token")) {
                    Log.i(INFO_LOG, "Result contains: " + token);
                    SharedPreferences.Editor editor = preferences.edit();
                    //parse the JSON string to get the value of "token"
                    try {
                        JSONObject object=new JSONObject(token);
                        tokenValue=object.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.putString("user_token", tokenValue);
                    editor.apply();
                }
                else if(token.contains("error")){
                    //no user token?...authentication failed!
                    Toast.makeText(context,"User not Found",Toast.LENGTH_LONG).show();
                    return;
                }
                //show the main UI...
                Intent uiIntent = new Intent(Login.this, Home.class);
                context.startActivity(uiIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        tUser=(TextView)findViewById(R.id.txtuserName);
        tPass=(TextView)findViewById(R.id.txtPassword);

    }

    public void onResume(){
        super.onResume();
        registerReceiver(receiver,new IntentFilter(LimeService.LOGIN_NOTIFICATION));
    }

    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void onClick(View view){

        //
        String user=tUser.getText().toString();
        String pass=tPass.getText().toString();
        Intent intent=new Intent(this,LimeService.class);
        intent.putExtra("user_name", user);
        intent.putExtra("password", pass);
        intent.setAction(WS_CONNNECTION);
        Log.i(INFO_LOG, "intent created");
        if(intent!=null){
        startService(intent);
            //show progress dialog...waiting for notification
        }else{
            Log.e(ERR_LOG,"Cannot start service, intent is null");
        }

    }
}
