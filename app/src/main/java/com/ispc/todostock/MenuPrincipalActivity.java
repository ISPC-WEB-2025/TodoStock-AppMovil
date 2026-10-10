package com.ispc.todostock;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.ispc.todostock.cuenta.BajaDeCuenta;
import com.ispc.todostock.sesion.SesionManager;

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

    // TK48: botón de Gestión de Usuarios, solo visible para Administradores.
    // TODO TK37: conectar a GestionUsuariosActivity cuando la pantalla esté implementada.
    private MaterialButton btnNavUsuarios;

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
        btnNavUsuarios = findViewById(R.id.btnNavUsuarios);

        if (nombreUsuario != null && !nombreUsuario.trim().isEmpty()) {
            tvRolActivo.setText("Sesión activa: " + nombreUsuario + " (" + rolUsuario + ")");
        } else {
            tvRolActivo.setText("Sesión activa: " + rolUsuario);
        }

        // 3. TK48: aplicar visibilidad según el perfil leído de la sesión guardada
        aplicarVisibilidadPorRol();

        // 4. Configurar navegacion hacia cada pantalla hija cumpliendo el contrato de Intent
        configurarNavegacion();
    }

    /**
     * TK48 — Muestra u oculta las opciones del menú según el perfil del usuario.
     *
     * Matriz de visibilidad:
     *   Administrador: Usuarios (*), Productos, Sucursal, Stock, Movimientos + Contacto, Baja, Cerrar sesión
     *   Empleado:                    Sucursal (-> Stock), Movimientos       + Contacto, Baja, Cerrar sesión
     *
     * (*) Botón provisional hasta que TK37 implemente GestionUsuariosActivity.
     *
     * Flujo de navegación: Para el Empleado, el acceso a Stock se da mediante "Seleccionar Sucursal"
     * para consultar el stock contextualizado de una sede real, ocultando el botón huérfano de Stock del menú.
     *
     * Fuente de verdad: SesionManager.esAdmin(), que devuelve true para ADMINISTRADOR
     * y para el superusuario de Django. VENTAS y DEPOSITO se tratan como Empleado.
     */
    private void aplicarVisibilidadPorRol() {
        boolean esAdmin = SesionManager.getInstance(this).esAdmin();

        // Opciones exclusivas del Administrador
        btnNavUsuarios.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
        btnNavCargaProducto.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
        btnNavStock.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
    }


    private void configurarNavegacion() {
        // TK48: Gestión de Usuarios — solo visible para Administradores (botón ya oculto en aplicarVisibilidadPorRol).
        // TODO TK37: reemplazar el Toast por startActivity(GestionUsuariosActivity) cuando esté implementada.
        btnNavUsuarios.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.menu_usuarios_pendiente), Toast.LENGTH_SHORT).show());

        // 1. Seleccion de Sucursal (Candelaria)
        btnNavSucursal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalActivity.this, SeleccionSucursalActivity.class);
                intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
                startActivity(intent);


            }
        });

        // 2. Gestion de Stock (Virginia)
        btnNavStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MenuPrincipalActivity.this, GestionStockActivity.class);
                intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
                intent.putExtra(EXTRA_SUCURSAL_ID, sucursalSeleccionadaId);
                startActivity(intent);

            }
        });

        // 3. Carga de Producto (Pedro)
        btnNavCargaProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalActivity.this, CargaProductoActivity.class);
                intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
                startActivity(intent);
            }
        });

        // 4. Movimientos entre sucursales (Aylen)
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

                Intent intent = new Intent(MenuPrincipalActivity.this, ContactoActivity.class);
                intent.putExtra(EXTRA_ROL_USUARIO, rolUsuario);
                startActivity(intent);
            }
        });

        // TK52: Darme de baja (la lógica está en BajaDeCuenta)
        findViewById(R.id.btnDarmeDeBaja).setOnClickListener(v -> new BajaDeCuenta(this).iniciar());

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