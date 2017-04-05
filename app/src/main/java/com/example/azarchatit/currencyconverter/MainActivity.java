package com.example.azarchatit.currencyconverter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.azarchatit.currencyconverter.CurrencyDB.currency_icon;
import static com.example.azarchatit.currencyconverter.CurrencyDB.currency_longname;
import static com.example.azarchatit.currencyconverter.CurrencyDB.currency_name;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = "";
    private String mCurrencyDataJSON;
    private String updateTime;
    private String updateRate;
    private Integer updatebase;
    private String[] params = {"EUR", "EUR"};
    private int choiceField = 1;
    private Double rate = null;


    private EditText fieldA, fieldB;
    private Spinner spin1, spin2;
    private Button buttonConverter;
    private TextView lastRate, baseCurrency, dateOfUpdate, currencyA, currencyB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //function to initialize components
        init();

        CurrencyAdapter customAdapter = new CurrencyAdapter(getApplicationContext(), currency_icon, currency_name);
        //spinners
        spin1.setOnItemSelectedListener(this);
        spin1.setAdapter(customAdapter);
        spin2.setOnItemSelectedListener(this);
        spin2.setAdapter(customAdapter);


        //button listener
        View.OnClickListener listenerButton = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked");
                //field_A.getText()
                operateCurrency(choiceField);
                baseCurrency.setText(updatebase);
                lastRate.setText(updateRate);
                dateOfUpdate.setText(updateTime);

            }
        };


        buttonConverter.setOnClickListener(listenerButton);

        /**
         * The EditText whatchers
         */

        fieldB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                choiceField = 2;

            }
        });
        fieldA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                choiceField = 1;


            }
        });
    }

    /**
     * Function to initialize components body
     */

    private void init() {
        spin1 = (Spinner) findViewById(R.id.SpinnerCurrency1);
        spin2 = (Spinner) findViewById(R.id.SpinnerCurrency2);
        buttonConverter = (Button) findViewById(R.id.button1);
        fieldA = (EditText) findViewById(R.id.EditTextCurrencyA);
        fieldB = (EditText) findViewById(R.id.EditTextCurrencyB);
        lastRate = (TextView) findViewById(R.id.TextView02);
        baseCurrency = (TextView) findViewById(R.id.TextView01);
        dateOfUpdate = (TextView) findViewById(R.id.TextView03);
        currencyA = (TextView) findViewById(R.id.textCurrencyA);
        currencyB = (TextView) findViewById(R.id.textCurrencyB);
    }

    /**
     * Calculate Currency based on the variable choicefield
     */


    private void operateCurrency(int choix) {
        //Log.d(TAG, "operateCurrency: "+rate.toString());
        if (choix == 1) {
            String value1 = fieldA.getText().toString();
            try {
                Double doubleValue1 = Double.valueOf(value1);
                Double result1 = doubleValue1 * rate;
                DecimalFormat df = new DecimalFormat("#.#####");
                fieldB.setText(df.format(result1).toString());

            } catch (NumberFormatException e) {
                fieldB.setText(" ");
            }
        }
        if (choix == 2) {
            String value2 = fieldB.getText().toString();
            try {

                Double doubleValue2 = Double.valueOf(value2);
                Double result2 = doubleValue2 * (1 / rate);
                DecimalFormat df = new DecimalFormat("#.#####");
                fieldA.setText(df.format(result2).toString());

            } catch (NumberFormatException e) {
                fieldA.setText(" ");

            }
        }
    }


    /**
     * To retrieve a list of currency exchange rates based on the provided base currency
     */
    private void getCurrency(String base, final String currency) {
        // API/URL (with base parameter) provided by fixer.io
        String currencyUrl = "http://api.fixer.io/latest?base=" + base;

        if (isNetworkAvailable()) {
            //Initialize client object
            OkHttpClient client = new OkHttpClient();
            //Initialize request object
            Request request = new Request.Builder()
                    .url(currencyUrl)
                    .build();
            //Initialize call object to send the request
            Call call = client.newCall(request);
            /*
                enqueue() method supports asynchronous processing (process in background thread)

            */
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Opps! Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        // Get the data in JSON format from the response object
                        mCurrencyDataJSON = response.body().string();
                        // To check the status of the response object
                        if (response.isSuccessful()) {
                            // To execute certain action in the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "run: " + mCurrencyDataJSON);

                                }
                            });
                            // Create a new JSONObject based on the response JSON data
                            JSONObject dailyCurrency = new JSONObject(mCurrencyDataJSON);
                            //Getting the base from the json file
                            String base = dailyCurrency.getString("base");
                            //Getting the time from the json file
                            String time = dailyCurrency.getString("date");
                            updateTime = time;
                            //Getting the Rates json object
                            JSONObject data = dailyCurrency.getJSONObject("rates");
                            //check if the base is the same as the currency
                            if (currency.equals(base)) {
                                rate = 1.0;
                                updateRate = rate.toString();
                                //else get the currency rate based on the selected currency
                            } else {
                                String currencyRate = data.getString(currency);
                                Log.d(TAG, "onResponse: " + currencyRate);

                                //Getting the Rate currency from the json file
                                rate = Double.valueOf(currencyRate);
                                updateRate = currencyRate;

                            }

                        } else {
                            // To execute certain action in the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Opps! Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Device is currently offline", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * To check if the device is currently connected to the Internet
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    /**
     * Getting the selected Currency in the spinnes
     */

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId()) {
            case R.id.SpinnerCurrency1:

                Log.d(TAG, "onItemSelected0:" + currency_name[i]);
                params[0] = currency_name[i];
                updatebase = currency_longname[i];
                currencyA.setText(updatebase);

                break;
            case R.id.SpinnerCurrency2:

                Log.d(TAG, "onItemSelected1: " + currency_name[i]);
                params[1] = currency_name[i];
                currencyB.setText(currency_longname[i]);

                break;
        }
        getCurrency(params[0], params[1]);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrency(params[0], params[1]);
    }
}
