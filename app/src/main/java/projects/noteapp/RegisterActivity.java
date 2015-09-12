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


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {
    TextView email;
    TextView password;
    TextView confirmPassword;
    TextView firstName;
    TextView lastName;
    Button registerButton;

    public void invokeError(String error){
        if(error.equals("invalidEmail")){
            email.setError("Please enter a valid email!");
        }else if(error.equals("takenEmail")){
            email.setError("Email has been taken!");
        }else{
            email.setError("UNKNOWN ERROR: " + error);
        }
    }

    public void registerSuccess(){
        Intent myIntent = new Intent(RegisterActivity.this, MenuActivity.class);
        RegisterActivity.this.startActivity(myIntent);
        RegisterActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (TextView)findViewById(R.id.email);
        password = (TextView)findViewById(R.id.password);
        confirmPassword = (TextView)findViewById(R.id.confirm);
        firstName = (TextView)findViewById(R.id.firstname);
        lastName = (TextView)findViewById(R.id.lastname);
        registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    @Override
    public void onClick(View v) {
        boolean error = false;
        if(email.getText().length() == 0){ //ALSO CHECK IF EMAIL IS VALID
            error = true;
            email.setError("Please enter a valid email address!");
        }
        if(password.getText().length() < 6){
            error = true;
            password.setError("Password must be 6 characters or longer!");
        }
        if(!password.getText().toString().equals(confirmPassword.getText().toString())){
            error = true;
            confirmPassword.setError("Passwords must match!");
            System.out.println(password.getText());
            System.out.println(confirmPassword.getText());
        }
        if(firstName.getText().length() == 0){
            error = true;
            firstName.setError("Please enter your first name!");
        }
        if(lastName.getText().length() == 0){
            error = true;
            lastName.setError("Please enter your last name!");
        }

        if(!error){
            System.out.println("REGISTRATION GOOD");
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Connecting..."); // Calls onProgressUpdate()
            try {
                String data  = URLEncoder.encode("email", "UTF-8")
                        + "=" + URLEncoder.encode(email.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("firstname", "UTF-8")
                        + "=" + URLEncoder.encode(firstName.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("lastname", "UTF-8")
                        + "=" + URLEncoder.encode(lastName.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("key", "UTF-8")
                        + "=" + URLEncoder.encode("REGISTER", "UTF-8");

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

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            String[] response = resp.split("&");
            if(response[0].equals("register")){
                Storage.getInstance().userID = Integer.parseInt(response[1]);
                Storage.getInstance().email = response[2];
                Storage.getInstance().firstname = response[3];
                Storage.getInstance().lastname = response[4];
                Storage.getInstance().loginTime = System.currentTimeMillis();
                registerSuccess();
            }else{
                invokeError(response[0]);
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
