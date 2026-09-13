package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private Button btnCatalogo;
    private Button btnClientes;
    private Button btnContacto;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Enlazar vistas
        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnCatalogo = findViewById(R.id.btnCatalogo);
        btnClientes = findViewById(R.id.btnClientes);
        btnContacto = findViewById(R.id.btnContacto);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // 2. Recuperar el dato enviado desde LoginActivity
        if (getIntent() != null && getIntent().hasExtra("EXTRA_USUARIO")) {
            String nombreUsuario = getIntent().getStringExtra("EXTRA_USUARIO");
            tvBienvenida.setText("¡Bienvenido/a, " + nombreUsuario + "!");
        }

        // 3. Navegación a Catálogo
        btnCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Descomentar cuando exista CatalogoActivity
                /*
                Intent intent = new Intent(MainActivity.this, CatalogoActivity.class);
                startActivity(intent);
                */
                Toast.makeText(MainActivity.this, "Navegando a Catálogo...", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Navegación a Clientes
        btnClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Descomentar cuando exista ClientesActivity
                /*
                Intent intent = new Intent(MainActivity.this, ClientesActivity.class);
                startActivity(intent);
                */
                Toast.makeText(MainActivity.this, "Navegando a Clientes...", Toast.LENGTH_SHORT).show();
            }
        });

        // 5. Navegación a Contacto
        btnContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Descomentar cuando exista ContactoActivity
                /*
                Intent intent = new Intent(MainActivity.this, ContactoActivity.class);
                startActivity(intent);
                */
                Toast.makeText(MainActivity.this, "Navegando a Contacto...", Toast.LENGTH_SHORT).show();
            }
        });

        // 6. Cerrar Sesión (vuelve a LoginActivity y limpia el backstack)
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

