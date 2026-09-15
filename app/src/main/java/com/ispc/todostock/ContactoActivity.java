package com.ispc.todostock;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etNombre, etEmail, etMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        toolbar = findViewById(R.id.toolbarMenu);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etMensaje = findViewById(R.id.etMensaje);

        findViewById(R.id.btnEnviar).setOnClickListener(v -> {
            Toast.makeText(this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
        });
    }
}