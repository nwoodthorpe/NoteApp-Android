package projects.noteapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteDetailsActivity extends AppCompatActivity {
    TableLayout table;
    int tableIDCounter = 0;
    String title;
    int status = 0;

    public void setMetaFields(String[] metaData){
        TextView titleField = (TextView)findViewById(R.id.titleField);
        TextView descriptionField = (TextView)findViewById(R.id.descriptionField);
        TextView tagsField = (TextView)findViewById(R.id.tagsField);
        TextView dateField = (TextView)findViewById(R.id.dateField);

        titleField.setText(title);
        descriptionField.setText(metaData[0]);
        try {
            String dateString = metaData[2];
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date = format.parse(dateString);
            String finalDateString = new SimpleDateFormat("MMM d, yyyy").format(date);
            dateField.setText(finalDateString);
        }catch(ParseException e){
            e.printStackTrace();
        }

        String[] tagsList = metaData[1].split("&");
        tagsField.setText("");
        for(int i = 0; i<tagsList.length; i++){
            tagsField.setText(tagsField.getText() + tagsList[i]);
            if(i < tagsList.length - 1){
                tagsField.setText(tagsField.getText() + ", ");
            }
        }

        for(int i = 0; i<Integer.parseInt(metaData[3]); i++){
            addRowToTable(table, i + 1);
        }
        status = Integer.parseInt(metaData[4]);

        System.out.println("STATUS: " + status);

    }

    public void addRowToTable(TableLayout layout, int num){
        TableRow tableRow = new TableRow(this);

        TextView view1 = new TextView(this);
        view1.setTextSize(25);
        view1.setText("File " + Integer.toString(num));
        view1.setPadding(0, 0, 60, 0);
        //view1.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellborder));
        tableRow.addView(view1);

        Button viewButton = new Button(this);
        viewButton.setText("View");
        viewButton.setId(tableIDCounter);
        tableIDCounter++;
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Opening note with ID: " + v.getId());
                if (status == 0) { //Text Note
                    Intent myIntent = new Intent(NoteDetailsActivity.this, ViewTextActivity.class);
                    myIntent.putExtra("title", title);
                    NoteDetailsActivity.this.startActivity(myIntent);
                }else{ //Image Note
                    Intent myIntent = new Intent(NoteDetailsActivity.this, ViewImageActivity.class);
                    myIntent.putExtra("title", title);
                    myIntent.putExtra("num", v.getId());
                    NoteDetailsActivity.this.startActivity(myIntent);
                }

            }
        });
        tableRow.addView(viewButton);

        layout.addView(tableRow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        table = (TableLayout)findViewById(R.id.tableLayout);
        Intent thisIntent = getIntent();
        title = thisIntent.getStringExtra("title");
        final TextView titleField = (TextView)findViewById(R.id.titleField);
        final TextView descriptionField = (TextView)findViewById(R.id.descriptionField);
        final TextView tagsField = (TextView)findViewById(R.id.tagsField);
        final TextView dateField = (TextView)findViewById(R.id.dateField);

        titleField.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(NoteDetailsActivity.this).create();
                alertDialog.setTitle("Title:");
                alertDialog.setMessage(titleField.getText());
                alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        descriptionField.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(NoteDetailsActivity.this).create();
                alertDialog.setTitle("Description:");
                alertDialog.setMessage(descriptionField.getText());
                alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        tagsField.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(NoteDetailsActivity.this).create();
                alertDialog.setTitle("Tags:");
                alertDialog.setMessage(tagsField.getText());
                alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        dateField.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(NoteDetailsActivity.this).create();
                alertDialog.setTitle("Date:");
                alertDialog.setMessage(dateField.getText());
                alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        NetworkThread runner = new NetworkThread();
        runner.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class NetworkThread extends AsyncTask<String, String, String> {
        private String resp;

        @Override
        protected String doInBackground(String... params) {
            try {
                String data  = URLEncoder.encode("userID", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                data += "&" + URLEncoder.encode("title", "UTF-8")
                        + "=" + URLEncoder.encode(title, "UTF-8");
                data += "&" + URLEncoder.encode("key", "UTF-8")
                        + "=" + URLEncoder.encode("FETCHNOTEMETA", "UTF-8");

                URL url = new URL("http://www.nwoodthorpe.com/note/app.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                resp = rd.readLine();
                System.out.println(resp);

                wr.close();
                rd.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
                resp = "error 1";
            } catch (IOException e) {
                e.printStackTrace();
                resp = "error 2";
            }
            return resp;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                String[] metaData = result.split("\\|");
                setMetaFields(metaData);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
