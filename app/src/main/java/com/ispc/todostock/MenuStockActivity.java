package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.ispc.todostock.sesion.SesionManager;

/**
 * Pantalla intermedia (Hub) de Gestión de Productos.
 * Centraliza la navegación hacia:
 * 1. Listado de productos, con edición y baja (ProductosActivity, TK39)
 * 2. Cargar nuevo producto (CargaProductoActivity, exclusivo para Administrador)
 *
 * El stock por sucursal se consulta desde "Seleccionar sucursal".
 */
public class MenuStockActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvRolActivo;
    private MaterialButton btnStockConsultar;
    private MaterialButton btnStockCargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_stock);

        inicializarVistas();
        configurarToolbar();
        aplicarPermisosPorRol();
        configurarNavegacion();
    }

    private void inicializarVistas() {
        toolbar = findViewById(R.id.toolbarStock);
        tvRolActivo = findViewById(R.id.tvRolActivo);
        btnStockConsultar = findViewById(R.id.btnStockConsultar);
        btnStockCargar = findViewById(R.id.btnStockCargar);

        SesionManager sesion = SesionManager.getInstance(this);
        String rol = sesion.esAdmin() ? "Administrador" : "Empleado";
        String nombre = sesion.getNombre();

        if (nombre != null && !nombre.trim().isEmpty()) {
            tvRolActivo.setText("Sesión activa: " + nombre + " (" + rol + ")");
        } else {
            tvRolActivo.setText("Sesión activa: " + rol);
        }
    }

    private void configurarToolbar() {
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void aplicarPermisosPorRol() {
        boolean esAdmin = SesionManager.getInstance(this).esAdmin();
        btnStockCargar.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
    }

    private void configurarNavegacion() {
        btnStockConsultar.setOnClickListener(v ->
                startActivity(new Intent(MenuStockActivity.this, ProductosActivity.class)));

        btnStockCargar.setOnClickListener(v ->
                startActivity(new Intent(MenuStockActivity.this, CargaProductoActivity.class)));
    }
}