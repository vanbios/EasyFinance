package com.androidcollider.easyfin.utils;


import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.objects.Rates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RatesParser {


    public static void postRequest() {

        final String TAG_STRING_REQ = "get_last_rates";
        final String URL = "http://560671.acolider.web.hosting-test.net/fin-u/api/api_finu.php";

        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG_STRING_REQ, response);

                        ArrayList<Rates> ratesList = new ArrayList<>();

                        String[] currencyArray = AppController.getContext().getResources().getStringArray(R.array.json_rates_array);


                        try {

                            JSONObject results = new JSONObject(response).getJSONObject("results");

                            for (String cur : currencyArray) {


                                JSONArray jsonArray = results.getJSONArray(cur);

                                for (int j = 0; j < jsonArray.length(); j++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(j);

                                    int id = jsonObject.getInt("id");
                                    Date date = DateFormatUtils.stringToDate(jsonObject.getString("date"), "yyyy-MM-dd HH:mm:ss");
                                    String currency = jsonObject.getString("currency");
                                    String rate_type = jsonObject.getString("rate_type");
                                    double bid = jsonObject.getDouble("bid");
                                    double ask = jsonObject.getDouble("ask");

                                    Log.d(TAG_STRING_REQ, id + " " + date.toString() + " " + currency + " " +
                                            rate_type + " " + String.valueOf(bid) + " " + String.valueOf(ask));

                                    Rates rates = new Rates(id, date, currency, rate_type, bid, ask);

                                    ratesList.add(rates);
                                }
                            }

                            InfoFromDB.getInstance().getDataSource().insertRates(ratesList);

                        }

                            catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("action", "get_last_rates");
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(req, TAG_STRING_REQ);
    }

}
