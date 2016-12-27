package in.naushad.lenden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Riyas on 22-03-2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    File jsonfile;
    List<Person> persons;
    Context c;

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView tvLen,tvColumnName;
        TextView tvDen;
        TextView tvPersonName;
        ImageButton btRemove,btEdit;
        RelativeLayout rlBtRemove,rlBtEdit;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            tvLen = (TextView)itemView.findViewById(R.id.tvLen);
            tvDen = (TextView)itemView.findViewById(R.id.tvDen);
            tvPersonName = (TextView)itemView.findViewById(R.id.tvPersonName);
            btRemove = (ImageButton)itemView.findViewById(R.id.btRemove);
            btEdit=(ImageButton) itemView.findViewById(R.id.btEdit);
            rlBtRemove=(RelativeLayout) itemView.findViewById(R.id.rlBtRemove);
            rlBtEdit=(RelativeLayout)itemView.findViewById(R.id.rlBtEdit);
        }
    }

    RVAdapter(List<Person> persons){
        this.persons = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, final int i) {

        personViewHolder.tvLen.setText(persons.get(i).Len);
        personViewHolder.tvDen.setText(persons.get(i).Den);
        personViewHolder.tvPersonName.setText(persons.get(i).Person_Name);
        personViewHolder.rlBtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c=v.getContext();
                CoordinatorLayout cv=(CoordinatorLayout)((Activity)c).findViewById(R.id.clMainActivity);
                Snackbar sbDelete=Snackbar.make(cv, "Confirm Delete?", Snackbar.LENGTH_SHORT);
                sbDelete
                        .setDuration(5000)
                        .setAction("YES!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (i != RecyclerView.NO_POSITION) {
                                    delete(i);
                                }

                                TextView tvLastModified=(TextView)((Activity)c).findViewById(R.id.tvLastModified);
                                tvLastModified.setText("\nLast Modified: "+String.valueOf(DateUtils.getRelativeTimeSpanString(jsonfile.lastModified(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)));
                                Toast.makeText(c, "Delete Successful!", Toast.LENGTH_SHORT).show();
                            }
                        });
                sbDelete.show();
            }
        });
        /*personViewHolder.btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        personViewHolder.rlBtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Proceed to Edit Item", Toast.LENGTH_SHORT).show();

                Bundle basket = new Bundle();
                basket.putString("indexToEdit", String.valueOf(i));

                c=v.getContext();

                //finish current main activity
                ((Activity) c).finish();

                //start edit_entry activity
                Intent editEntryIntent = new Intent(c, edit_entry.class);
                editEntryIntent.putExtras(basket);
                c.startActivity(editEntryIntent);
            }
        });
        /*personViewHolder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/
    }

    private void delete(int position){
        removeEntry(position);
        persons.remove(position);
        notifyDataSetChanged();

    }
    @Override
    public int getItemCount() {
        return persons.size();
    }


   private void removeEntry(int i){
        createJsonIfNotExists();
        RemoveContentsFromJSON(i);
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

    private void RemoveContentsFromJSON(int i) {
        try {

            JSONObject jsonObjMain = new JSONObject(loadJSONFromSDCard());
            JSONArray jA = jsonObjMain.getJSONArray("JSONLenDen");

            //Remove Data
            jA.remove(i);

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
