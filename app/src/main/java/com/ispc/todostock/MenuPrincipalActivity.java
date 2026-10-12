package com.ispc.todostock;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    private MaterialButton btnNavMovimientos;
    private MaterialButton btnNavContacto;
    private MaterialButton btnCerrarSesion;
    private MaterialButton btnNavPerfil;


    // TK48: botón de Gestión de Usuarios, solo visible para Administradores.
    // TODO TK37: conectar a GestionUsuariosActivity cuando la pantalla esté implementada.
    private MaterialButton btnNavUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // 1. Obtener datos de la sesión guardada
        SesionManager sesion = SesionManager.getInstance(this);
        String nombreSesion = sesion.getNombre();
        if (nombreSesion != null && !nombreSesion.trim().isEmpty()) {
            nombreUsuario = nombreSesion;
        } else if (getIntent() != null && getIntent().hasExtra(EXTRA_USUARIO)) {
            nombreUsuario = getIntent().getStringExtra(EXTRA_USUARIO);
        }
        rolUsuario = sesion.esAdmin() ? "Administrador" : "Empleado";

        // 2. Inicializar vistas
        tvRolActivo = findViewById(R.id.tvRolActivo);
        btnNavSucursal = findViewById(R.id.btnNavSucursal);
        btnNavStock = findViewById(R.id.btnNavStock);
        btnNavMovimientos = findViewById(R.id.btnNavMovimientos);
        btnNavContacto = findViewById(R.id.btnNavContacto);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnNavUsuarios = findViewById(R.id.btnNavUsuarios);
        btnNavPerfil = findViewById(R.id.btnNavPerfil);

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
        btnNavStock.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
    }


    private void configurarNavegacion() {
        // Gestión de usuarios: aviso provisional hasta la integración de la pantalla de gestión
        btnNavUsuarios.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.menu_usuarios_pendiente), Toast.LENGTH_SHORT).show());

        // Seleccion de Sucursal
        btnNavSucursal.setOnClickListener(v ->
                startActivity(new Intent(MenuPrincipalActivity.this, SeleccionSucursalActivity.class)));

        // Gestión de Stock (pantalla intermedia / Hub de stock)
        btnNavStock.setOnClickListener(v ->
                startActivity(new Intent(MenuPrincipalActivity.this, MenuStockActivity.class)));

        // Movimientos (pantalla intermedia / Hub de movimientos)
        btnNavMovimientos.setOnClickListener(v ->
                startActivity(new Intent(MenuPrincipalActivity.this, MenuMovimientosActivity.class)));

        // Contacto y Soporte
        btnNavContacto.setOnClickListener(v ->
                startActivity(new Intent(MenuPrincipalActivity.this, ContactoActivity.class)));
        // Ver perfil
        btnNavPerfil.setOnClickListener(v ->
                startActivity(new Intent(MenuPrincipalActivity.this, PerfilActivity.class)));


        // Darme de baja (delegado en BajaDeCuenta)
        findViewById(R.id.btnDarmeDeBaja).setOnClickListener(v -> new BajaDeCuenta(this).iniciar());

        // Cerrar sesión con confirmación y borrado de sesión
        btnCerrarSesion.setOnClickListener(v -> mostrarDialogoConfirmacionCerrarSesion());
    }

    /**
     * Solicita confirmación al usuario antes de cerrar sesión.
     * Al confirmar, elimina el token de EncryptedSharedPreferences (SesionManager)
     * y redirige a LoginActivity limpiando el stack de navegación.
     */
    private void mostrarDialogoConfirmacionCerrarSesion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.cerrar_sesion_titulo)
                .setMessage(R.string.cerrar_sesion_mensaje)
                .setNegativeButton(R.string.cerrar_sesion_cancelar, null)
                .setPositiveButton(R.string.cerrar_sesion_confirmar, (dialog, which) -> {
                    SesionManager.getInstance(this).cerrarSesion();

                    Intent intent = new Intent(MenuPrincipalActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}