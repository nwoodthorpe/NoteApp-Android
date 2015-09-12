package projects.noteapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
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

public class ViewActivity extends AppCompatActivity {
    TableLayout table;
    int tableIDCounter = 0;
    String[] tags;

    public void tableRecieved(String[] titles, String[] descriptions, String[] tags){
        this.tags = tags;
        for(int i = 0; i<titles.length; i++){
            addRowToTable(table, titles[i], descriptions[i]);
        }
    }

    public void initTable(TableLayout layout){
        TableRow rowHead = new TableRow(this);
        TextView view1 = new TextView(this);
        view1.setText("                                                   .");
        view1.setTextSize(25);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        //view1.setTypeface(typeFace);
        rowHead.addView(view1);

        layout.addView(rowHead);
    }

    public void addRowToTable(TableLayout layout, final String title, final String desc){
        TableRow tableRow = new TableRow(this);

        tableRow.setId(tableIDCounter);
        tableIDCounter++;
        TextView view1 = new TextView(this);
        view1.setTextSize(25);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        //view1.setTypeface(typeFace);
        view1.setText(Html.fromHtml("" + title + "<br />" + "<small>" + desc + "<br /></small>"));
        view1.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellborder));
        tableRow.addView(view1);

        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(v.getId());
                System.out.println(title);
                System.out.println(desc);
                System.out.println(tags[v.getId()]);

                Intent myIntent = new Intent(ViewActivity.this, NoteDetailsActivity.class);
                myIntent.putExtra("title", title);
                ViewActivity.this.startActivity(myIntent);
            }
        });
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
        int leftMargin=0;
        int topMargin=0;
        int rightMargin=0;
        int bottomMargin=4;
        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        tableRow.setLayoutParams(tableRowParams);
        tableRow.setMinimumWidth(500);
        tableRow.setPadding(0, 55, 0, 0);
        layout.addView(tableRow);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        table = (TableLayout)findViewById(R.id.tableLayout);
        initTable(table);
        NetworkThread runner = new NetworkThread();
        runner.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
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

        private  String[] titles;
        private String[] descriptions;
        private String[] tags;
        String resp;

        @Override
        protected String doInBackground(String... params) {
            try {
                String data = URLEncoder.encode("userID", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                data += "&" + URLEncoder.encode("key", "UTF-8")
                        + "=" + URLEncoder.encode("FETCHNOTELIST", "UTF-8");

                URL url = new URL("http://www.nwoodthorpe.com/note/app.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                System.out.println("ID:" + Integer.toString(Storage.getInstance().userID));
                resp = rd.readLine();
                System.out.println(resp);
                String[] list = resp.split("\\|");

                int numOfEntries = list.length / 3;
                if(numOfEntries > 0) {
                    titles = new String[numOfEntries];
                    descriptions = new String[numOfEntries];
                    tags = new String[numOfEntries];
                    int counterA = 0;
                    int counterB = 0;
                    do {
                        titles[counterA] = list[counterB];
                        counterB++;
                        descriptions[counterA] = list[counterB];
                        counterB++;
                        tags[counterA] = list[counterB];
                        counterB++;
                        counterA++;
                    } while (counterB < list.length);

                }else{
                    System.out.println("ERROR - NO ENTRIES");
                }
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
            tableRecieved(titles, descriptions, tags);
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
