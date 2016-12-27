package in.naushad.lenden;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class edit_entry extends AppCompatActivity {
    EditText etEditLen,etEditDen,etEditPersonName;
    Button btEditEntry;
    String StringetEditLen,StringetEditDen,StringetEditPersonName;
    String Len,Den,Name;
    File jsonfile;
    String indexToEditFromBundle;

    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        initXML();


        createJsonIfNotExists();

        Bundle basket = getIntent().getExtras();
        indexToEditFromBundle=basket.getString("indexToEdit");

        fillInCurrentValues();

        btEditEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringetEditLen = etEditLen.getText().toString();
                StringetEditDen = etEditDen.getText().toString();
                StringetEditPersonName = etEditPersonName.getText().toString();


                EditContentsInJSON();

                startActivity(new Intent(edit_entry.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(edit_entry.this, MainActivity.class));
        finish();
    }

    void initXML(){
        etEditLen = (EditText) findViewById(R.id.etEditLen);
        etEditDen = (EditText) findViewById(R.id.etEditDen);
        etEditPersonName = (EditText) findViewById(R.id.etEditPersonName);
        btEditEntry = (Button) findViewById(R.id.btEditEntry);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fillInCurrentValues(){
        try {
            JSONObject obj = new JSONObject(loadJSONFromSDCard());
            JSONArray m_jArry = obj.getJSONArray("JSONLenDen");
            JSONObject jo_inside = m_jArry.getJSONObject(Integer.valueOf(indexToEditFromBundle));

                Len = jo_inside.getString("Len");
                Den = jo_inside.getString("Den");
                Name = jo_inside.getString("Name");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        etEditLen.setText(Len);
        etEditDen.setText(Den);
        etEditPersonName.setText(Name);
        etEditLen.setSelection(etEditLen.getText().length());
        etEditDen.setSelection(etEditDen.getText().length());
        etEditPersonName.setSelection(etEditPersonName.getText().length());
    }

    private void createJsonIfNotExists(){
        //create file in sdcard if not already there
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

    private String loadJSONFromSDCard() {
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
    private void EditContentsInJSON(){
        try {
            JSONObject jsonObjMain = new JSONObject(loadJSONFromSDCard());
            JSONArray jA = jsonObjMain.getJSONArray("JSONLenDen");
            JSONObject jOB=jA.getJSONObject(Integer.valueOf(indexToEditFromBundle));

            //Edit Data
            jOB.put("Len",StringetEditLen);
            jOB.put("Den",StringetEditDen);
            jOB.put("Name", StringetEditPersonName);

            try {
                FileOutputStream fos = new FileOutputStream(jsonfile);
                String test=jsonObjMain.toString();
                fos.write(test.getBytes());
                fos.close();
            }catch (FileNotFoundException fnf){
                fnf.printStackTrace();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
