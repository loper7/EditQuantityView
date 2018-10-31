package com.dw.editquantityviewsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.loper7.editquantityview.EditQuantityView;

public class MainActivity extends AppCompatActivity {

    private EditQuantityView editQuantityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editQuantityView=findViewById(R.id.editQuantityView);

        editQuantityView.setCanInput(true);
    }
}
