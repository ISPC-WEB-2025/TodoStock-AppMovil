package com.ispc.todostock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

public class GestionStockActivity extends AppCompatActivity {

    private Toolbar toolbarStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_stock);

        // 1. Vinculación del Toolbar
        toolbarStock = findViewById(R.id.toolbarStock);

        // 2. Click en el icono de navegación (flecha_atras)
        toolbarStock.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
