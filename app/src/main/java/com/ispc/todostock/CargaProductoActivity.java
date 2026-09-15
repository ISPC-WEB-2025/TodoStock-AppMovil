package com.ispc.todostock;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CargaProductoActivity extends AppCompatActivity {

    private EditText etNombreProducto, etDescripcion, etPrecio, etCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_producto);

        Toolbar toolbar = findViewById(R.id.toolbarMenu);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etCodigo = findViewById(R.id.etCodigo);

        findViewById(R.id.btnGuardar).setOnClickListener(v -> validarYGuardar());
    }

    private void validarYGuardar() {
        String nombre = etNombreProducto.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreProducto.setError("El nombre es obligatorio");
            return;
        }
        if (descripcion.isEmpty()) {
            etDescripcion.setError("La descripción es obligatoria");
            return;
        }
        if (precio.isEmpty()) {
            etPrecio.setError("El precio es obligatorio");
            return;
        }
        if (codigo.isEmpty()) {
            etCodigo.setError("El código es obligatorio");
            return;
        }

        // Todo OK — en Sprint 1 no persiste, solo confirma
        Toast.makeText(this, "Producto válido, listo para guardar", Toast.LENGTH_SHORT).show();
    }
}