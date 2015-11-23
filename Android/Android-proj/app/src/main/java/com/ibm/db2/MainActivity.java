//Package
package com.ibm.db2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.gc.materialdesign.views.ButtonRectangle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * DB2 Android Demo Application
 * For assistance e-mail: wlodarek@ca.ibm.com
 */
public class MainActivity extends Activity {

    //Change this variable to your Bluemix's application name!
    String bluemixAppName = "db2server";

    EditText userInput;
    ButtonRectangle addItem;
    ListView items;
    ProgressBar progressBar;

    ArrayList<String> itemsList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter = null;

    SwipeDismissListViewTouchListener touchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set content
        setContentView(R.layout.activity_main);

        //views
        userInput = (EditText) findViewById(R.id.addText);
        addItem = (ButtonRectangle) findViewById(R.id.addButton);
        items = (ListView) findViewById(R.id.itemList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initial loading of program = progressbar visible
        progressBar.setVisibility(View.VISIBLE);

        //Array adapter stuff tilllll
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                itemsList);

        //Swipe delete listener
        touchListener =
                new SwipeDismissListViewTouchListener(
                        items,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    userInput.setEnabled(false);
                                    addItem.setEnabled(false);

                                    new deleteItem().execute(arrayAdapter.getItem(position));

                                    arrayAdapter.remove(arrayAdapter.getItem(position));
                                }
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }
                );

        //Give the ListView the listener
        items.setOnTouchListener(touchListener);
        items.setOnScrollListener(touchListener.makeScrollListener());

        //Set the ListView's adapter
        items.setAdapter(arrayAdapter);

        //Get the current items from DB2
        new getItems().execute();

        //Set the onClick & onEdit listeners
        addItemOnClickListener();
        onEditChangedListener();
    }

    public void addItemOnClickListener() {
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean exists = false;

                //If the item is already in the list
                for (String s : itemsList) {
                    if (s.toLowerCase().equals(userInput.getText().toString().toLowerCase())) {
                        exists = true;
                    }
                }

                //If it isn't in the list, add it to DB2 and show it
                if (exists == false) {
                    progressBar.setVisibility(View.VISIBLE);
                    userInput.setEnabled(false);
                    addItem.setEnabled(false);

                    new addItem().execute(userInput.getText().toString());
                }
            }
        });
        addItem.setEnabled(false);
    }

    public void onEditChangedListener() {
        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Checks if users input is between 1 and 20 characters, since in DB2
                //our item column is varchar(20)
                if (userInput.getText().length() < 1 || userInput.getText().length() > 20) {
                    addItem.setEnabled(false);
                } else {
                    addItem.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userInput.setEnabled(false);
    }

    private class getItems extends AsyncTask<String, String,String> {
        protected String doInBackground(String... urls) {

            try {
                //Retrieves the items on the applications startup and puts them in the ListView
                URL url = new URL("http://" + bluemixAppName + ".mybluemix.net/items");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String line = "";

                if (in != null) {
                    while ((line = br.readLine()) != null) {
                        if (line.length() > 0 && line.length() < 21) {
                            itemsList.add(line.trim());
                        }
                    }
                }

                urlConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            arrayAdapter.notifyDataSetChanged();

            userInput.setEnabled(true);
        }

    }

    private class addItem extends AsyncTask<String, String,String> {
        protected String doInBackground(String... urls) {

            try {
                //Adds an item to DB2
                URL url = new URL("http://" + bluemixAppName + ".mybluemix.net/addItem?item=" +
                        urls[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String line = "";

                if (in != null) {
                    while ((line = br.readLine()) != null) {

                        System.out.println(line);

                    }
                }

                urlConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            itemsList.add(userInput.getText().toString());
            arrayAdapter.notifyDataSetChanged();
            userInput.setText("");

            userInput.setEnabled(true);
        }
    }

    private class deleteItem extends AsyncTask<String, String,String> {
        protected String doInBackground(String... urls) {

            try {
                //Deletes an item from DB2
                URL url = new URL("http://" + bluemixAppName + ".mybluemix.net/deleteItem?item=" +
                        urls[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String line = "";

                if (in != null) {
                    while ((line = br.readLine()) != null) {

                        System.out.println(line);

                    }
                }

                urlConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            userInput.setEnabled(true);
        }
    }
}

