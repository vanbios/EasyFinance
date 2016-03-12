package com.androidcollider.easyfin.requests;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ReqUpdateRates {

    public static void getNewRates() {
        final String TAG_STRING_REQ = "get_last_rates";
        final String URL = "http://api.minfin.com.ua/summary/f362f94f90fe9d841a98280b9098297ce4d574fa/";

        StringRequest req = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG_STRING_REQ, response);
                        ArrayList<Rates> ratesList = new ArrayList<>();
                        String[] currencyArray = AppController.getContext().getResources().getStringArray(R.array.json_rates_array);
                        int[] idArray = new int[] {3, 7, 11, 15};

                        try {
                            for (int i=0; i<currencyArray.length; i++) {
                                String cur = currencyArray[i];
                                JSONObject jsonObject = new JSONObject(response).getJSONObject(cur);

                                int id = idArray[i];
                                long date = System.currentTimeMillis();
                                double bid = jsonObject.getDouble("bid");
                                double ask = jsonObject.getDouble("ask");

                                Log.d(TAG_STRING_REQ, id + " " + date + " " + cur + " " +
                                        "bank" + " " + String.valueOf(bid) + " " + String.valueOf(ask));

                                ratesList.add(new Rates(id, date, cur, "bank", bid, ask));
                            }

                            InfoFromDB.getInstance().getDataSource().insertRates(ratesList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(req, TAG_STRING_REQ);
    }
}
