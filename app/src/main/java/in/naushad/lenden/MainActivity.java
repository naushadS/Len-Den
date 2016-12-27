package in.naushad.lenden;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsService;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    File jsonfile;
    FloatingActionButton fabNewEntry;
    TextView tvLastModified;
    Toast ExitToast;
    DateUtils dateLastModified;

    private List<Person> persons;
    private RecyclerView rv;
    private static final int TIME_INTERVAL = 2000;
    private static final int REQUEST_CODE_ASK_PERMISSIONS=123;
    private long mBackPressed;
    String Len,LenFinal,Den,DenFinal,Name;

    //Chrome Custom Tabs
    public static final String SourceCodeURL="https://github.com/naushadS/Len-Den";
    public static final String DevsGithubURL="https://github.com/naushadS";
    static final String STABLE_PACKAGE = "com.android.chrome";
    static final String BETA_PACKAGE = "com.chrome.beta";
    static final String DEV_PACKAGE = "com.chrome.dev";
    static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
    private static String sPackageNameToUse;
    String finalPackageName;
    //private Bitmap mCloseButtonBitmap;

    CustomTabsClient mClient;
    CustomTabsSession mCustomTabsSession;
    CustomTabsServiceConnection mCustomTabsServiceConnection;
    CustomTabsIntent customTabsIntent;

    //permission required list
    String[] mPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=23) {
            //Requesting permissions
            try {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(mPermission, REQUEST_CODE_ASK_PERMISSIONS);
                }
            } catch (Exception e) {}
        }

        init();

        //create file in sdcard if not already there
        checkNCreateJSONfile();

        //load and display contents from JSONfile
        LoadDisplayContentsFromJSON();

        //returns last modified time of the json file
        tvLastModified.append(String.valueOf(DateUtils.getRelativeTimeSpanString(jsonfile.lastModified(),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS)));

        initializeChromeCustomTab();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void init() {

        //set logo in the action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_bar_icon);

        tvLastModified = (TextView) findViewById(R.id.tvLastModified);
        fabNewEntry=(FloatingActionButton) findViewById(R.id.fabNewEntry);
        fabNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,add_entry.class));
                finish();
            }
        });
        ExitToast = Toast.makeText(MainActivity.this, "Tap Again to Exit", Toast.LENGTH_SHORT);

        //setting up recycler view
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        rv.setLayoutManager(llm);
        rv.hasFixedSize();

        initializeData();
        initializeAdapter();

    }
    private void initializeData(){
        persons = new ArrayList<>();
    }
    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(persons);
        rv.setAdapter(adapter);
    }
    private String loadJSONFromSDCard(){
        String json = null;
        try {
            FileInputStream is = new FileInputStream(jsonfile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    private void LoadDisplayContentsFromJSON(){
        try {
            JSONObject obj = new JSONObject(loadJSONFromSDCard());
            JSONArray m_jArry = obj.getJSONArray("JSONLenDen");

            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);

                Len = jo_inside.getString("Len");
                Den = jo_inside.getString("Den");
                Name = jo_inside.getString("Name");

                //decide whether to add prefix "₹" before Len
                if(Len.trim().matches("")){
                    LenFinal=Len;
                }else{
                    LenFinal="₹"+Len;
                }

                //decide whether to add prefix "₹" before Den
                if(Den.trim().matches("")){
                    DenFinal=Den;
                }else{
                    DenFinal="₹"+Den;
                }

                persons.add(new Person(LenFinal, DenFinal, Name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void checkNCreateJSONfile(){
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/LenDen");
        if(!dir.exists())
            dir.mkdir();
        jsonfile = new File(dir, "data.json");
        if(!jsonfile.exists()) {
            try {
                jsonfile.createNewFile();
                FileOutputStream f = new FileOutputStream(jsonfile,true);
                String StringToWrite="{\"JSONLenDen\": [{\"Len\":\"150\",\"Den\":\"250\",\"Name\":\"Naushad Shukoor\"}]}";
                f.write(StringToWrite.getBytes());
                f.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private void menuClickActionSourceCode(){
        if(finalPackageName!=null) {
            customTabsIntent.launchUrl(MainActivity.this, Uri.parse(SourceCodeURL));
        }else{
            Bundle basket = new Bundle();
            basket.putString("URLToOpen",SourceCodeURL);
            basket.putString("WebpageTitle", "Len-Den (Source Code)");
            basket.putString("WebpageSubtitle", "Hosted by Github!");
            Intent person = new Intent(MainActivity.this,webViewFallback.class);
            person.putExtras(basket);
            startActivity(person);
        }
    }
    private void menuClickActionDevsGithub(){
        if(finalPackageName!=null) {
            customTabsIntent.launchUrl(MainActivity.this, Uri.parse(DevsGithubURL));
        }else{
            Bundle basket = new Bundle();
            basket.putString("URLToOpen", DevsGithubURL);
            basket.putString("WebpageTitle", "Developer's Github Profile");
            basket.putString("WebpageSubtitle", "Hosted by Github!");
            Intent person = new Intent(MainActivity.this,webViewFallback.class);
            person.putExtras(basket);
            startActivity(person);
        }
    }
    private void initializeChromeCustomTab(){
        try {
            finalPackageName = getPackageNameToUse(getBaseContext());
            //cct marlon jones
            if (finalPackageName != null) {
                mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
                    @Override
                    public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                        //warmup
                        mClient = customTabsClient;
                        mClient.warmup(0L);
                        mCustomTabsSession = mClient.newSession(null);

                        //prefetch
                        /*
                        mCustomTabsSession.mayLaunchUrl(Uri.parse(SourceCodeURL), null, null);
                        mCustomTabsSession.mayLaunchUrl(Uri.parse(DevsGithubURL), null, null);
                        */
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        mClient = null;

                    }
                };
                CustomTabsClient.bindCustomTabsService(MainActivity.this, finalPackageName, mCustomTabsServiceConnection);

                customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                        .setShowTitle(true)
                        .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                        .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
                        .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back_white_24dp))
                        .build();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getPackageNameToUse(Context context) {
        if (sPackageNameToUse != null)
            return sPackageNameToUse;

        PackageManager pm = context.getPackageManager();

        // Get default VIEW intent handler.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        if (packagesSupportingCustomTabs.isEmpty()) {
            sPackageNameToUse = null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            sPackageNameToUse = packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                && !hasSpecializedHandlerIntents(context, activityIntent)
                && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            sPackageNameToUse = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains(STABLE_PACKAGE)) {
            sPackageNameToUse = STABLE_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(BETA_PACKAGE)) {
            sPackageNameToUse = BETA_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(DEV_PACKAGE)) {
            sPackageNameToUse = DEV_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(LOCAL_PACKAGE)) {
            sPackageNameToUse = LOCAL_PACKAGE;
        }
        return sPackageNameToUse;
    }
    private static boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(
                    intent,
                    PackageManager.GET_RESOLVED_FILTER);
            if (handlers == null || handlers.size() == 0) {
                return false;
            }
            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter == null) continue;
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue;
                if (resolveInfo.activityInfo == null) continue;
                return true;
            }
        } catch (RuntimeException e) {

        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            MainActivity.this.unbindService(mCustomTabsServiceConnection);
        }catch (Exception e){}
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            ExitToast.cancel();
            super.onBackPressed();
            return;
        }
        else {
            ExitToast.show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_source_code:
                menuClickActionSourceCode();
                break;
            case R.id.action_devs_github:
                menuClickActionDevsGithub();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
