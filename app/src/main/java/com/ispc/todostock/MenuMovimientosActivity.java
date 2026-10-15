package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.ispc.todostock.sesion.SesionManager;

/**
 * Pantalla intermedia (Hub) de Movimientos de Stock.
 * Centraliza la navegación hacia los tipos de movimientos del inventario:
 * 1. Ingreso de mercadería (Entradas de stock)
 * 2. Movimientos entre sucursales (Traslados)
 * 3. Salida por venta (Salidas comerciales)
 * 4. Baja de productos (Merma / rotura)
 */
public class MenuMovimientosActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvRolActivo;
    private MaterialButton btnMovIngresos;
    private MaterialButton btnMovSucursales;
    private MaterialButton btnMovVentas;
    private MaterialButton btnMovBajas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_movimientos);

        inicializarVistas();
        aplicarPermisosPorRol();
        configurarToolbar();
        configurarNavegacion();
    }

    private void inicializarVistas() {
        toolbar = findViewById(R.id.toolbarMovimientos);
        tvRolActivo = findViewById(R.id.tvRolActivo);
        btnMovIngresos = findViewById(R.id.btnMovIngresos);
        btnMovSucursales = findViewById(R.id.btnMovSucursales);
        btnMovVentas = findViewById(R.id.btnMovVentas);
        btnMovBajas = findViewById(R.id.btnMovBajas);

        SesionManager sesion = SesionManager.getInstance(this);
        String rol = sesion.esAdmin() ? "Administrador" : "Empleado";
        String nombre = sesion.getNombre();

        if (nombre != null && !nombre.trim().isEmpty()) {
            tvRolActivo.setText("Sesión activa: " + nombre + " (" + rol + ")");
        } else {
            tvRolActivo.setText("Sesión activa: " + rol);
        }
    }

    /**
     * Aplica la matriz de permisos por rol en el menú de movimientos:
     * - Ingreso de mercadería (Entrada): exclusivo de Administradores (ADR-0005).
     * - Traslados, Ventas y Mermas: disponibles para Administrador y Empleado.
     */
    private void aplicarPermisosPorRol() {
        boolean esAdmin = SesionManager.getInstance(this).esAdmin();
        btnMovIngresos.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
    }

    private void configurarToolbar() {
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void configurarNavegacion() {
        // Ingreso de mercadería
        btnMovIngresos.setOnClickListener(v -> {
            Intent intent = new Intent(MenuMovimientosActivity.this, MovimientosActivity.class);
            intent.putExtra(MovimientosActivity.EXTRA_TIPO_OPERACION, MovimientosActivity.OPERACION_INGRESO);
            startActivity(intent);
        });

        // Movimientos entre sucursales
        btnMovSucursales.setOnClickListener(v -> {
            Intent intent = new Intent(MenuMovimientosActivity.this, MovimientosActivity.class);
            intent.putExtra(MovimientosActivity.EXTRA_TIPO_OPERACION, MovimientosActivity.OPERACION_TRASLADO);
            startActivity(intent);
        });

        // Salida por venta
        btnMovVentas.setOnClickListener(v -> {
            Intent intent = new Intent(MenuMovimientosActivity.this, MovimientosActivity.class);
            intent.putExtra(MovimientosActivity.EXTRA_TIPO_OPERACION, MovimientosActivity.OPERACION_VENTA);
            startActivity(intent);
        });

        // Mermas y roturas de stock
        btnMovBajas.setOnClickListener(v -> {
            Intent intent = new Intent(MenuMovimientosActivity.this, MovimientosActivity.class);
            intent.putExtra(MovimientosActivity.EXTRA_TIPO_OPERACION, MovimientosActivity.OPERACION_MERMA);
            startActivity(intent);
        });
    }
}
