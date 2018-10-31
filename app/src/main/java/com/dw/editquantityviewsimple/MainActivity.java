package com.dw.editquantityviewsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.loper7.editquantityview.EditQuantityView;

public class MainActivity extends AppCompatActivity {

    private EditQuantityView editQuantityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editQuantityView = findViewById(R.id.editQuantityView);

        editQuantityView.setCanInput(true);
        editQuantityView.setMax(500);
        editQuantityView.setMin(1);
        editQuantityView.setQuantity(2);
        editQuantityView.setOnQuantityChangedListener(new EditQuantityView.OnQuantityChangedListener() {
            @Override
            public void onQuantityChanged(int quantity) {
                Log.d("loper7", "quantity:" + quantity);
            }

        });
    }
}
