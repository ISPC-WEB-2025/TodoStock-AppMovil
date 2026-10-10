package com.ispc.todostock;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.ispc.todostock.sesion.SesionManager;

public class CargaProductoActivity extends AppCompatActivity {

    private EditText etNombreProducto, etDescripcion, etPrecio, etCodigo;
    private TextInputLayout tilNombreProducto, tilDescripcion, tilPrecio, tilCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TK48: defensa en profundidad — si un Empleado llega aquí por alguna vía directa
        // (sin pasar por el menú), la pantalla se cierra antes de mostrarse.
        // El menú ya oculta este acceso (aplicarVisibilidadPorRol en MenuPrincipalActivity),
        // pero esta capa adicional previene escalada de privilegios.
        if (!SesionManager.getInstance(this).esAdmin()) {
            Toast.makeText(this, getString(R.string.menu_error_acceso_denegado), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_carga_producto);

        Toolbar toolbar = findViewById(R.id.toolbarMenu);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etCodigo = findViewById(R.id.etCodigo);

        tilNombreProducto = findViewById(R.id.tilNombreProducto);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        tilPrecio = findViewById(R.id.tilPrecio);
        tilCodigo = findViewById(R.id.tilCodigo);

        findViewById(R.id.btnGuardar).setOnClickListener(v -> validarYGuardar());
    }

    private void validarYGuardar() {
        limpiarErrores();

        String nombre = etNombreProducto.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();


        boolean todoValido = true;

        if (nombre.isEmpty()) {
            tilNombreProducto.setError(getString(R.string.carga_producto_error_nombre));
            todoValido = false;
        }
        if (descripcion.isEmpty()) {
            tilDescripcion.setError(getString(R.string.carga_producto_error_descripcion));
            todoValido = false;
        }
        if (precio.isEmpty()) {
            tilPrecio.setError(getString(R.string.carga_producto_error_precio));
            todoValido = false;
        }
        if (codigo.isEmpty()) {
            tilCodigo.setError(getString(R.string.carga_producto_error_codigo));
            todoValido = false;
        }

        if (!todoValido) {
            return;
        }

        // Todo OK — todavía no persiste, el alta real contra la API es TK23
        Toast.makeText(this, R.string.carga_producto_ok, Toast.LENGTH_SHORT).show();
    }

    private void limpiarErrores() {
        tilNombreProducto.setError(null);
        tilDescripcion.setError(null);
        tilPrecio.setError(null);
        tilCodigo.setError(null);
    }
}