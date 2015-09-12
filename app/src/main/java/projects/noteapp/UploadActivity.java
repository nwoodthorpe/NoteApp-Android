package projects.noteapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class UploadActivity extends ActionBarActivity {
    Button selectButton;
    Button addButton;
    TextView titleBox;
    TextView descriptionBox;
    TextView tagsBox;
    ScrollViewExtended tagsView;
    int REQUEST_CODE;
    ArrayList<String> imagePaths = new ArrayList<>();
    ArrayList<String> base64Images = new ArrayList<>();
    TextView tagsText;
    Button uploadButton;
    String formattedTags = "";

    public void uploadSuccess(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(UploadActivity.this);
        builder1.setMessage("Note Uploaded Successfully!");
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent myIntent = new Intent(UploadActivity.this, MenuActivity.class);
                        UploadActivity.this.startActivity(myIntent);
                        UploadActivity.this.finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void updateStatus(String message){
        uploadButton.setText("Status: " + message);
    }

    public String[] parseImages(String image){
        ArrayList<String> list = new ArrayList<>();
        int CHUNK_SIZE = 1000000; //1mb
        int counter = 0;
        int stringLength = image.length();
        while(true){
            if(stringLength >= (counter+1) * CHUNK_SIZE){
                list.add(image.substring(counter * CHUNK_SIZE, (counter + 1) * CHUNK_SIZE));
                counter++;
            }else{
                list.add(image.substring(counter * CHUNK_SIZE, stringLength));
                return list.toArray(new String[list.size()]);
            }
        }
    }

    public void sendImages(ArrayList<String> rawData) {
        NetworkThread runner = new NetworkThread();
        runner.execute();
    }

    public void processImages(ArrayList<String> paths) {
        base64Images = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            File imgFile = new File(paths.get(i));
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.URL_SAFE);
                base64Images.add(encoded);
            }
        }
        if (base64Images.size() > 0) {
            System.out.println("Images selected successfully");
            selectButton.setText(base64Images.size() + " Images Selected");
        } else {
            System.out.println("Error 105");
        }
    }

    public void onUploadClick(View v) {
        boolean error = false;
        uploadButton.setClickable(false);
        uploadButton.setText("Confirming Form Validity...");
        if (titleBox.getText().toString() == null || titleBox.getText().toString().equals("")) {
            error = true;
            titleBox.setError("Please enter a title!");
        }
        if (descriptionBox.getText().toString() == null || descriptionBox.getText().toString().equals("")) {
            error = true;
            descriptionBox.setError("Please enter a title!");
        }
        if (selectButton.getText().toString().equals("No Images Selected") || selectButton.getText().toString().equals(null)) {
            error = true;
            selectButton.setError("Please select an image!");
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
            tagsBox.setError("Please enter atleast 1 tag!");
        }else {
            for (int i = 1; i < tagsList.size(); i++) {
                formattedTags += tagsList.get(i);
                if (i != tagsList.size() - 1) {
                    formattedTags += "&";
                }
            }
        }
        if (!error) {
            System.out.println("NO ERRORS, RUNNING PROCESSING");
            updateStatus("Encoding Images...");
            processImages(imagePaths);
            sendImages(base64Images);
        }
    }


    public void onSelectClick(View v) {
        PhotoPickerIntent intent = new PhotoPickerIntent(UploadActivity.this);
        intent.setPhotoCount(20);
        intent.setShowCamera(true);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void onAddClick(View v) {
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
        setContentView(R.layout.activity_upload);
        selectButton = (Button) findViewById(R.id.selectButton);
        addButton = (Button) findViewById(R.id.addButton);
        titleBox = (TextView) findViewById(R.id.titleBox);
        descriptionBox = (TextView) findViewById(R.id.descriptionBox);
        tagsBox = (TextView) findViewById(R.id.tagsBox);
        tagsView = (ScrollViewExtended) findViewById(R.id.tagsScroll);
        uploadButton = (Button) findViewById(R.id.uploadButton);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectClick(v);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onAddClick(v);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onUploadClick(v);
            }
        });
        tagsText = new TextView(this);
        tagsText.setTextSize(20);
        tagsText.setText("TAGS");
        tagsView.addView(tagsText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                imagePaths = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                selectButton.setText(imagePaths.size() + " Images Selected");
                selectButton.setError(null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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
                String imageData = "";
                for (int i = 0; i < base64Images.size(); i++) {
                    imageData += base64Images.get(i) + "+";
                }
                System.out.println("NUM OF IMAGES: " + Integer.toString(base64Images.size()));

                String[] imageChunks = parseImages(imageData);
                String data = URLEncoder.encode("title", "UTF-8")
                        + "=" + URLEncoder.encode(titleBox.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("description", "UTF-8")
                        + "=" + URLEncoder.encode(descriptionBox.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("tags", "UTF-8")
                        + "=" + URLEncoder.encode(formattedTags, "UTF-8");
                data += "&" + URLEncoder.encode("userID", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                data += "&" + URLEncoder.encode("numImages", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(base64Images.size()), "UTF-8");
                data += "&" + URLEncoder.encode("size", "UTF-8")
                        + "=" + URLEncoder.encode(Integer.toString(imageData.length()), "UTF-8");
                data += "&" + URLEncoder.encode("key", "UTF-8")
                        + "=" + URLEncoder.encode("UPLOADIMAGE", "UTF-8");

                URL url = new URL("http://www.nwoodthorpe.com/note/app.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                resp = rd.readLine();
                System.out.println(resp);

                wr.close();
                rd.close();

                if(resp.equals("success")) {
                    onProgressUpdate("Connected to Server, Initiating Upload...");
                    double numChunks = imageChunks.length;
                    DecimalFormat progressFormat = new DecimalFormat("###");
                    for (int i = 0; i < numChunks; i++) {
                        data = URLEncoder.encode("title", "UTF-8")
                                + "=" + URLEncoder.encode(titleBox.getText().toString(), "UTF-8");
                        data += "&" + URLEncoder.encode("userID", "UTF-8")
                                + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                        data += "&" + URLEncoder.encode("data", "UTF-8")
                                + "=" + URLEncoder.encode(imageChunks[i], "UTF-8");
                        data += "&" + URLEncoder.encode("key", "UTF-8")
                                + "=" + URLEncoder.encode("UPLOADING", "UTF-8");

                        URLConnection newConn = url.openConnection();
                        newConn.setDoOutput(true);
                        OutputStreamWriter wr2 = new OutputStreamWriter(newConn.getOutputStream());
                        wr2.write(data);
                        wr2.flush();

                        BufferedReader rd2 = new BufferedReader(new InputStreamReader(newConn.getInputStream()));
                        resp = rd2.readLine();


                        if(!resp.equals("success")){
                            System.out.println("DATA TRANSFER ERROR");
                            onProgressUpdate("Uploading failed! Data Transfer Error!");
                            System.out.println(resp);
                            return("Fail");
                        }else{
                            onProgressUpdate("Uploading... Progress:  " + progressFormat.format(((i + 1) / numChunks) * 100.0) + "%");
                        }
                        wr.close();
                        rd.close();
                    }
                    onProgressUpdate("Upload Complete, Finalizing...");

                    data = URLEncoder.encode("title", "UTF-8")
                            + "=" + URLEncoder.encode(titleBox.getText().toString(), "UTF-8");
                    data += "&" + URLEncoder.encode("userID", "UTF-8")
                            + "=" + URLEncoder.encode(Integer.toString(Storage.getInstance().userID), "UTF-8");
                    data += "&" + URLEncoder.encode("key", "UTF-8")
                            + "=" + URLEncoder.encode("FINALIZE", "UTF-8");

                    URLConnection newConn = url.openConnection();
                    newConn.setDoOutput(true);
                    OutputStreamWriter wr2 = new OutputStreamWriter(newConn.getOutputStream());
                    wr2.write(data);
                    wr2.flush();

                    BufferedReader rd2 = new BufferedReader(new InputStreamReader(newConn.getInputStream()));
                    resp = rd2.readLine();
                    System.out.println(resp);


                }else{
                    System.out.println("ERROR: " + resp);
                }


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
            switch(resp){
                case "SUCCESS":
                    uploadSuccess();
                    break;
                case "ERROR":
                    System.out.println("ERROR!");
                    uploadButton.setText("Upload");
                    uploadButton.setClickable(true);
                    break;
                case "TITLE_ERROR":
                    titleBox.setError("You have used this title before! Please choose a unique title.");
                    uploadButton.setText("Upload");
                    uploadButton.setClickable(true);
                    System.out.println("TITLE ERROR");
                    break;
                default:
                    System.out.println("UNKNOWN ERROR!");
                    uploadButton.setText("Upload");
                    uploadButton.setClickable(true);
                    break;
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
            final String message = text[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateStatus(message);
                }
            });
        }
    }
}

