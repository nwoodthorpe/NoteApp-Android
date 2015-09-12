package projects.noteapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

public class ViewTextActivity extends AppCompatActivity {
    TextView noteText;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_text);
        noteText = (TextView)findViewById(R.id.noteText);
        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("SAVE BUTTON NOT IMPLEMENTED YET");
            }
        });
        title = getIntent().getStringExtra("title");

        NetworkThread networkThread = new NetworkThread();
        networkThread.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_text, menu);
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
        Boolean errorFlag = false;
        ArrayList<String> text = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {
            try {
                String data  = URLEncoder.encode("userID", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                data += "&" + URLEncoder.encode("title", "UTF-8")
                        + "=" + URLEncoder.encode(title, "UTF-8");
                data += "&" + URLEncoder.encode("key", "UTF-8")
                        + "=" + URLEncoder.encode("DOWNLOADTEXTNOTE", "UTF-8");

                URL url = new URL("http://www.nwoodthorpe.com/note/app.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                System.out.println("NOTE:");
                while((resp = rd.readLine()) != null){
                    text.add(resp);
                    System.out.println(resp);
                }

                wr.close();
                rd.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
                resp = "error 1";
                errorFlag = true;
            } catch (IOException e) {
                e.printStackTrace();
                resp = "error 2";
                errorFlag = true;
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
            System.out.println("SETTING NOTE:");
            if(errorFlag){
                System.out.println(resp);
            }else {
                for(int i= 0; i<text.size(); i++){
                    noteText.setText(noteText.getText() + text.get(i));
                    if(i < text.size() - 1){
                        noteText.setText(noteText.getText() + "\n");
                    }
                    System.out.println(text.get(i));
                }
                System.out.println("Note set.");
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            noteText.setHint("Downloading Note...");
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
