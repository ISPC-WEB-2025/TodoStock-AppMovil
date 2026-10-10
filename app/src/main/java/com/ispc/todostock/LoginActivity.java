package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ispc.todostock.network.LoginResponse;
import com.ispc.todostock.repositorio.ResultadoLogin;
import com.ispc.todostock.sesion.SesionManager;

/**
 * TK05 - Inicio de sesión contra la API.
 *
 * Envía email y contraseña a POST /api/usuarios/login/. Si el backend responde 200,
 * guarda el token y los datos del usuario en SesionManager (cifrados) y abre el menú.
 * 401: email o contraseña incorrectos. 403: cuenta pendiente de aprobación.
 *
 * TK112: la validación y el pedido al backend están en LoginViewModel (y este usa
 * AuthRepository). Esta pantalla solo lee los campos, muestra lo que publica el
 * ViewModel, guarda la sesión y abre el menú.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario;   // Recibe el email (el id se mantiene para no romper la TK92)
    private EditText etPassword;
    private Button btnLogin;

    private CharSequence textoBotonOriginal;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textoBotonOriginal = btnLogin.getText();

        // TK34: enlace a la pantalla de Registro
        findViewById(R.id.tvIrARegistro).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class)));

        // TK112: el ViewModel sobrevive al giro de pantalla, así el pedido en curso no se pierde.
        viewModel = new ViewModelProvider(this, LoginViewModel.FACTORY).get(LoginViewModel.class);

        viewModel.getCargando().observe(this, this::mostrarEnviando);
        viewModel.getErrorEmail().observe(this, error ->
                etUsuario.setError(Boolean.TRUE.equals(error) ? getString(R.string.login_error_email) : null));
        viewModel.getErrorPassword().observe(this, error ->
                etPassword.setError(Boolean.TRUE.equals(error) ? getString(R.string.login_error_password) : null));
        viewModel.getResultado().observe(this, this::mostrarResultado);

        btnLogin.setOnClickListener(v -> viewModel.login(
                etUsuario.getText().toString(),
                etPassword.getText().toString()));
    }

    /** Muestra lo que respondió el backend. */
    private void mostrarResultado(ResultadoLogin resultado) {
        if (resultado == null) return;   // No hay ningún resultado pendiente de mostrar.
        viewModel.resultadoMostrado();

        switch (resultado.getTipo()) {
            case EXITO:
                iniciarSesion(resultado.getDatos());
                break;
            case CREDENCIALES_INCORRECTAS:
                etPassword.setError(getString(R.string.login_error_credenciales));
                mostrarMensaje(getString(R.string.login_error_credenciales));
                break;
            case CUENTA_PENDIENTE:
                mostrarCuentaPendiente();
                break;
            case SIN_CONEXION:
                mostrarMensaje(getString(R.string.login_error_conexion));
                break;
            case ERROR_SERVIDOR:
            default:
                mostrarMensaje(getString(R.string.login_error_generico));
                break;
        }
    }

    /** Guarda la sesión y abre el menú principal con el nombre y el rol del usuario. */
    private void iniciarSesion(LoginResponse datos) {
        SesionManager.getInstance(this).guardarSesion(datos);

        String rol = getString(datos.isEsAdmin() ? R.string.login_rol_admin : R.string.login_rol_empleado);

        Intent intent = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
        intent.putExtra(MenuPrincipalActivity.EXTRA_USUARIO, datos.getNombre());
        intent.putExtra(MenuPrincipalActivity.EXTRA_ROL_USUARIO, rol);
        startActivity(intent);

        // Cierra el Login para que "Atrás" desde el menú no vuelva acá.
        finish();
    }

    private void mostrarCuentaPendiente() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.login_pendiente_titulo)
                .setMessage(R.string.login_pendiente_mensaje)
                .setPositiveButton(R.string.login_aceptar, null)
                .show();
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    /** Mientras se espera la respuesta, el botón queda deshabilitado para no enviar dos veces. */
    private void mostrarEnviando(Boolean enviando) {
        boolean activo = Boolean.TRUE.equals(enviando);
        btnLogin.setEnabled(!activo);
        btnLogin.setText(activo ? getString(R.string.login_boton_ingresando) : textoBotonOriginal);
    }
}