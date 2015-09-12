package projects.noteapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MenuActivity extends ActionBarActivity {
    TextView username;


    public void uploadButtonClicked(){
        Intent myIntent = new Intent(MenuActivity.this, UploadActivity.class);
        MenuActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button uploadButton;
        Button writeButton;
        Button viewButton;
        Button logoutButton;
        username = (TextView)findViewById(R.id.username);
        username.setText(Storage.getInstance().firstname);
        uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MenuActivity.this, UploadActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });
        writeButton = (Button)findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(MenuActivity.this, WriteActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });
        viewButton = (Button)findViewById(R.id.viewButton);
        viewButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(MenuActivity.this, ViewActivity.class);
                MenuActivity.this.startActivity(myIntent);
            }
        });
        logoutButton = (Button)findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MenuActivity.this, LoginActivity.class);
                MenuActivity.this.startActivity(myIntent);
                Storage.getInstance().userID = -1;
                Storage.getInstance().firstname = null;
                Storage.getInstance().lastname = null;
                Storage.getInstance().email = null;
                Storage.getInstance().loginTime = 0;
                MenuActivity.this.finish();
            }
        });

        ProgressDialog x = new ProgressDialog(this);
        x.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        x.setMax(10);
        x.setProgress(4);
        x.show();

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
}
