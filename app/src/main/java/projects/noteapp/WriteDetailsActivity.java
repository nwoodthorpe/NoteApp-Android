package projects.noteapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class WriteDetailsActivity extends ActionBarActivity {
    TextView titleBox;
    TextView descriptionBox;
    TextView tagsBox;
    Button addButton;
    Button uploadButton;
    ScrollViewExtended tagsView;
    TextView tagsText;
    String formattedTags = "";

    public void uploadSuccess(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(WriteDetailsActivity.this);
        builder1.setMessage("Note Uploaded Successfully!");
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent myIntent = new Intent(WriteDetailsActivity.this, MenuActivity.class);
                        WriteDetailsActivity.this.startActivity(myIntent);
                        WriteDetailsActivity.this.finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void sendNote(){
        NetworkThread runner = new NetworkThread();
        runner.execute();
    }

    public void onUploadClicked(){
        boolean error = false;
        if(!(titleBox.getText().toString().length() > 0)){
            error = true;
            titleBox.setError("Please enter a title!");
        }
        if(!(descriptionBox.getText().toString().length() > 0)){
            error = true;
            descriptionBox.setError("Please enter a description!");
        }
        String tags = tagsText.getText().toString();
        String[] tagsArray = tags.split("\n");
        ArrayList<String> tagsList = new ArrayList<>();
        for (int i = 0; i < tagsArray.length; i++) {
            String tag = tagsArray[i];
            while (tag.charAt(0) == ' ') {
                tag = tag.substring(1);
            }
            while (tag.charAt(tag.length() - 1) == ' ') {
                tag = tag.substring(0, tag.length() - 1);
            }
            if (!(tag.equals(null) || tag.equals(""))) {
                tagsList.add(tag);
            }
        }
        if(!(tagsList.size() > 0)){
            error = true;
            tagsBox.setError("Please enter atleast 1 tag.");
        }else {
            formattedTags = "";
            for (int i = 1; i < tagsList.size(); i++) {
                formattedTags += tagsList.get(i);
                if (i != tagsList.size() - 1) {
                    formattedTags += "&";
                }
            }
        }
        if(!error){
            System.out.println("NO ERRORS, SENDING NOTE");
            sendNote();
        }
    }

    public void onAddClicked(){
        if ((tagsBox.getText().toString() == null) || tagsBox.getText().toString().equals("")) {
            tagsBox.setError("ENTER A TAG");
        } else {
            tagsText.setText(tagsText.getText() + "\n" + tagsBox.getText());
            tagsBox.setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_details);
        titleBox = (TextView)findViewById(R.id.titleBox);
        descriptionBox = (TextView)findViewById(R.id.descriptionBox);
        tagsBox = (TextView)findViewById(R.id.tagsBox);
        addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onAddClicked();
            }
        });
        uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onUploadClicked();
            }
        });
        tagsView = (ScrollViewExtended)findViewById(R.id.tagsScroll);
        tagsText = new TextView(this);
        tagsText.setText("TAGS");
        tagsText.setTextSize(20);
        tagsView.addView(tagsText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_details, menu);
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
            publishProgress("Connecting..."); // Calls onProgressUpdate()
            try {
                String data = URLEncoder.encode("title", "UTF-8")
                        + "=" + URLEncoder.encode(titleBox.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("description", "UTF-8")
                        + "=" + URLEncoder.encode(descriptionBox.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("tags", "UTF-8")
                        + "=" + URLEncoder.encode(formattedTags, "UTF-8");
                data += "&" + URLEncoder.encode("note", "UTF-8")
                        + "=" + URLEncoder.encode(Storage.getInstance().noteData, "UTF-8");
                data += "&" + URLEncoder.encode("userID", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                data += "&" + URLEncoder.encode("key", "UTF-8")
                        + "=" + URLEncoder.encode("UPLOADTEXT", "UTF-8");

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
            switch(resp) {
                case "TITLE_ERROR":
                    titleBox.setError("You have used this title before! Please choose a unique title.");
                    System.out.println("Title Error");
                    break;
                case "UNKNOWN_ERROR":
                    System.out.println("SERVER ERROR!");
                    break;
                case "SUCCESS":
                    System.out.println("Note Upload success!");
                    uploadSuccess();
                    break;
                default:
                    System.out.println("UNKNOWN ERROR: " + resp);

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
            System.out.println(text[0]);
        }
    }
}

