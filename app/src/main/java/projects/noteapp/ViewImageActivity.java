package projects.noteapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ViewImageActivity extends AppCompatActivity {
    String title;
    int num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        title = getIntent().getStringExtra("title");
        num = getIntent().getIntExtra("num", 0);

        NetworkThread networkThread = new NetworkThread();
        networkThread.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_image, menu);
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
            int fileSize = 0;
            try {
                String data  = URLEncoder.encode("userID", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                data += "&" + URLEncoder.encode("title", "UTF-8")
                        + "=" + URLEncoder.encode(title, "UTF-8");
                data += "&" + URLEncoder.encode("num", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(num), "UTF-8");
                data += "&" + URLEncoder.encode("key", "UTF-8")
                        + "=" + URLEncoder.encode("DOWNLOADIMAGENOTE-INIT", "UTF-8");

                URL url = new URL("http://www.nwoodthorpe.com/note/app.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                resp = rd.readLine();
                try{
                    fileSize = Integer.parseInt(resp);
                    resp = "good";
                }catch(NumberFormatException e){
                    resp = "NaN";
                }
                if(!resp.equals("NaN")){
                    String downloadedImage = "";
                    resp = "";
                    int segment = 0;
                    do{
                        downloadedImage += resp;
                        data  = URLEncoder.encode("userID", "UTF-8")
                                + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                        data += "&" + URLEncoder.encode("title", "UTF-8")
                                + "=" + URLEncoder.encode(title, "UTF-8");
                        data += "&" + URLEncoder.encode("num", "UTF-8")
                                + "=" + URLEncoder.encode(Integer.toString(num), "UTF-8");
                        data += "&" + URLEncoder.encode("segment", "UTF-8")
                                + "=" + URLEncoder.encode(Integer.toString(segment), "UTF-8");
                        data += "&" + URLEncoder.encode("key", "UTF-8")
                                + "=" + URLEncoder.encode("DOWNLOADIMAGENOTE-DOWNLOAD", "UTF-8");

                        conn = url.openConnection();
                        conn.setDoOutput(true);
                        wr = new OutputStreamWriter(conn.getOutputStream());
                        wr.write(data);
                        wr.flush();

                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        resp = rd.readLine();
                        System.out.println("Downloaded One...");
                        segment++;
                        onProgressUpdate(Integer.toString(fileSize), Integer.toString(segment*1000000));
                    }while(!resp.equals("finished"));
                }else{
                    System.out.println("DOWNLOAD NOTE WAS NOT INITIATED CORRECTLY");
                    errorFlag = true;
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

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {
            int totalSize = Integer.parseInt(text[0]);
            int currentSize = Integer.parseInt(text[1]);
            double currentProgress = Math.min(currentSize / totalSize, 1);
            System.out.println("CURRENT PROGRESS: " + currentProgress);
        }
    }
}
