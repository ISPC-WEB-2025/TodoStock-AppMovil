package com.ispc.todostock;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

/**
  * Contrato de extras de Intent definidos para las pantallas hijas:
 * - EXTRA_ROL_USUARIO (rol_usuario): String con el rol actual (Administrador o Vendedor)
 * - EXTRA_SUCURSAL_ID (sucursal_id): int con el ID de sucursal seleccionada (default: 1)
 * * Cada pantalla hija recibe estos extras con valores por defecto para que no fallen en Sprint 1.
 */
public class MenuPrincipalActivity extends AppCompatActivity {

    public static final String EXTRA_USUARIO = "EXTRA_USUARIO";
    public static final String EXTRA_ROL_USUARIO = "rol_usuario";
    public static final String EXTRA_SUCURSAL_ID = "sucursal_id";

    private String nombreUsuario = "";
    private String rolUsuario = "Administrador"; // Valor por defecto
    private int sucursalSeleccionadaId = 1;     // Sucursal default Centro

    private TextView tvRolActivo;
    private MaterialButton btnNavSucursal;
    private MaterialButton btnNavStock;
    private MaterialButton btnNavCargaProducto;
    private MaterialButton btnNavMovimientos;
    private MaterialButton btnNavContacto;
    private MaterialButton btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // 1. Obtener datos pasados por el Intent de Login (o mantener default)
        if (getIntent() != null) {
            if (getIntent().hasExtra(EXTRA_USUARIO)) {
                nombreUsuario = getIntent().getStringExtra(EXTRA_USUARIO);
            }
            if (getIntent().hasExtra(EXTRA_ROL_USUARIO)) {
                rolUsuario = getIntent().getStringExtra(EXTRA_ROL_USUARIO);
            }
        }

        // 2. Inicializar vistas
        tvRolActivo = findViewById(R.id.tvRolActivo);
        btnNavSucursal = findViewById(R.id.btnNavSucursal);
        btnNavStock = findViewById(R.id.btnNavStock);
        btnNavCargaProducto = findViewById(R.id.btnNavCargaProducto);
        btnNavMovimientos = findViewById(R.id.btnNavMovimientos);
        btnNavContacto = findViewById(R.id.btnNavContacto);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        if (nombreUsuario != null && !nombreUsuario.trim().isEmpty()) {
            tvRolActivo.setText("Sesión activa: " + nombreUsuario + " (" + rolUsuario + ")");
        } else {
            tvRolActivo.setText("Sesión activa: " + rolUsuario);
        }

        // 3. Configurar navegacion hacia cada pantalla hija cumpliendo el contrato de Intent
        configurarNavegacion();
    }

    private void configurarNavegacion() {
        // 1. Seleccion de Sucursal (Candelaria)
        btnNavSucursal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuPrincipalActivity.this, "Módulo Selección de Sucursal en desarrollo", Toast.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent(MenuPrincipalActivity.this, SeleccionSucursalActivity.class);
            intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
            startActivity(intent);
            */
            }
        });

        // 2. Gestion de Stock (Virginia)
        btnNavStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuPrincipalActivity.this, "Módulo Gestión de Stock en desarrollo", Toast.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent(MenuPrincipalActivity.this, GestionStockActivity.class);
            intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
            intent.putExtra(EXTRA_SUCURSAL_ID, sucursalSeleccionadaId);
            startActivity(intent);
            */
            }
        });

        // 3. Carga de Producto (Pedro)
        btnNavCargaProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuPrincipalActivity.this, "Módulo Carga de Producto en desarrollo", Toast.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent(MenuPrincipalActivity.this, CargaProductoActivity.class);
            intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
            startActivity(intent);
            */
            }
        });

        // 4. Movimientos entre Sucursales (Aylen)
        btnNavMovimientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalActivity.this, MovimientosActivity.class);
                intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
                startActivity(intent);
            }
        });

        // 5. Contacto / Soporte (Pedro)
        btnNavContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuPrincipalActivity.this, "Módulo Contacto en desarrollo", Toast.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent(MenuPrincipalActivity.this, ContactoActivity.class);
            intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
            startActivity(intent);
            */
            }
        });

        // Cerrar sesion: Vuelve a LoginActivity (asumiendo que LoginActivity ya existe)
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuPrincipalActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MenuPrincipalActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
