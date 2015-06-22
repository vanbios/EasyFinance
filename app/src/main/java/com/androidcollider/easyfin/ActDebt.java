package com.androidcollider.easyfin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ActDebt extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_debt);
    }





    public void goToAddDebtAct(View view) {
        Intent intent = new Intent(this, ActAddDebt.class);
        startActivity(intent);
    }

}
