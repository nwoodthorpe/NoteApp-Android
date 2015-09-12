package projects.noteapp;

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


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{
    TextView emailBox;
    TextView passwordBox;

    @Override
    public void onClick(View view) {
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }

    public void onDebugClick(View view){
        Storage.getInstance().userID = 1;
        Storage.getInstance().firstname = "Nathaniel";
        Storage.getInstance().lastname = "Woodthorpe";
        Storage.getInstance().loginTime = System.currentTimeMillis();
        Storage.getInstance().email = "Njwoodthorpe@gmail.com";
        loggedIn();
    }

    public void onRegisterClick(View view){
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    public void loggedIn() {
        System.out.println("LOG IN SUCCESS");
        Intent myIntent = new Intent(LoginActivity.this, MenuActivity.class);
        LoginActivity.this.startActivity(myIntent);
        LoginActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button registerButton = registerButton = (Button)findViewById(R.id.registerButton);
        Button debugButton = (Button)findViewById(R.id.debugButton);
        emailBox = (TextView)findViewById(R.id.email);
        passwordBox = (TextView)findViewById(R.id.password);
        loginButton.setOnClickListener(this);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onRegisterClick(v);
            }
        });
        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onDebugClick(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Connecting..."); // Calls onProgressUpdate()
            try {
                String data  = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(emailBox.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(passwordBox.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("key", "UTF-8")
                        + "=" + URLEncoder.encode("LOGIN", "UTF-8");

                URL url = new URL("http://www.nwoodthorpe.com/note/app.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                resp = rd.readLine();

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

        /*v
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            String[] response = resp.split("&");
            if (response[0].equals("loggedin")){
                Storage.getInstance().userID = Integer.parseInt(response[1]);
                Storage.getInstance().firstname = response[2];
                Storage.getInstance().lastname = response[3];
                Storage.getInstance().loginTime = System.currentTimeMillis();
                Storage.getInstance().email = emailBox.getText().toString();
                loggedIn();
            }else{
                System.out.println("NO BEANS");
                passwordBox.setError("Incorrect Login!");
                System.out.println(resp);
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
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
}


