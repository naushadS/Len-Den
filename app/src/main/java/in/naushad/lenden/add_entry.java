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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class add_entry extends AppCompatActivity {
    EditText etAddLen,etAddDen,etAddPersonName;
    Button btAddEntry;
    File jsonfile;
    //int n;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(add_entry.this, MainActivity.class));
        finish();
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
    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        initXML();

        btAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //convert text from edittexts to string
                String StringetAddLen = etAddLen.getText().toString();
                String StringetAddDen = etAddDen.getText().toString();
                String StringetAddPersonName = etAddPersonName.getText().toString();

                if(StringetAddPersonName.trim().length() != 0) {
                    //create file in sdcard if not already there
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/LenDen");
                    if (!dir.exists())
                        dir.mkdir();
                    jsonfile = new File(dir, "data.json");
                    if (!jsonfile.exists()) {
                        try {
                            jsonfile.createNewFile();
                            FileOutputStream f = new FileOutputStream(jsonfile, true);
                            String StringToWrite ="{\"JSONLenDen\": [{\"Len\":\"150\",\"Den\":\"250\",\"Name\":\"Naushad Shukoor\"}]}";
                            f.write(StringToWrite.getBytes());
                            f.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

/*
                //create file in sdcard if not already there
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/LenDen");
                if(!dir.exists())
                    dir.mkdir();
                File textfile = new File(dir, "data.txt");
                if(!textfile.exists()) {
                    try {
                        textfile.createNewFile();
                    }catch (IOException e){
                        Toast.makeText(add_entry.this, "error creating file exception", Toast.LENGTH_SHORT).show();
                    }
                }

                //to store and display contents to/from a text file
                try {
                    //store in a text file
                    FileOutputStream f = new FileOutputStream(file,true);
                    String StringToWrite=StringetAddLen+"\n"+StringetAddDen+"\n"+StringetAddPersonName+"\n\n";
                    f.write(StringToWrite.getBytes());
                    f.close();

                    //read from a text file
                    FileInputStream is = new FileInputStream(file);
                    StringBuffer fileContent = new StringBuffer("");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    while ((n = is.read(buffer)) != -1)
                    {
                        fileContent.append(new String(buffer, 0, n));
                    }
                    Toast.makeText(add_entry.this, fileContent , Toast.LENGTH_SHORT).show();
                    is.close();

                }catch (FileNotFoundException e){
                    Toast.makeText(add_entry.this, "file not found exception", Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Toast.makeText(add_entry.this, "file IO exception", Toast.LENGTH_SHORT).show();
                }
                */

                    AppendContentsToJSON(StringetAddLen, StringetAddDen, StringetAddPersonName);
                    startActivity(new Intent(add_entry.this, MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(add_entry.this, "Person Name Compulsory", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    void initXML(){
        etAddLen = (EditText) findViewById(R.id.etAddLen);
        etAddDen = (EditText) findViewById(R.id.etAddDen);
        etAddPersonName = (EditText) findViewById(R.id.etAddPersonName);
        btAddEntry = (Button) findViewById(R.id.btAddEntry);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    protected void AppendContentsToJSON(String StringetAddLen1,String StringetAddDen1,String StringetAddPersonName1) {
        try {

            JSONObject jsonObjMain = new JSONObject(loadJSONFromSDCard());
            JSONObject jO = new JSONObject();
            JSONArray jA = jsonObjMain.getJSONArray("JSONLenDen");

            //Add Data
            jO.put("Len", StringetAddLen1);
            jO.put("Den", StringetAddDen1);
            jO.put("Name", StringetAddPersonName1);

            //Append
            jA.put(jO);

            try {
                FileOutputStream fos = new FileOutputStream(jsonfile);
                String test=jsonObjMain.toString();
                fos.write(test.getBytes());
                fos.close();
            }catch (FileNotFoundException fnf){
                Toast.makeText(add_entry.this,"File Not Found",Toast.LENGTH_SHORT).show();
            }catch (IOException ioe){
                Toast.makeText(add_entry.this,"IOexception",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
