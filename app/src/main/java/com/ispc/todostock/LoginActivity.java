package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.AuthApiService;
import com.ispc.todostock.network.LoginRequest;
import com.ispc.todostock.network.LoginResponse;
import com.ispc.todostock.sesion.SesionManager;
import com.ispc.todostock.validacion.Validadores;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TK05 - Inicio de sesión contra la API.
 *
 * Envía email y contraseña a POST /api/usuarios/login/. Si el backend responde 200,
 * guarda el token y los datos del usuario en SesionManager (cifrados) y abre el menú.
 * 401: email o contraseña incorrectos. 403: cuenta pendiente de aprobación.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario;   // Recibe el email (el id se mantiene para no romper la TK92)
    private EditText etPassword;
    private Button btnLogin;

    private CharSequence textoBotonOriginal;
    private Call<LoginResponse> llamadaEnCurso;

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

        btnLogin.setOnClickListener(v -> intentarLogin());
    }

    @Override
    protected void onDestroy() {
        // Si se cierra la pantalla con un pedido en curso, se cancela.
        if (llamadaEnCurso != null) {
            llamadaEnCurso.cancel();
        }
        super.onDestroy();
    }

    /** Valida los campos y, si están bien, envía el pedido al backend. */
    private void intentarLogin() {
        String email = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString();

        etUsuario.setError(null);
        etPassword.setError(null);

        boolean valido = true;
        if (!Validadores.emailValido(email)) {
            etUsuario.setError(getString(R.string.login_error_email));
            valido = false;
        }
        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.login_error_password));
            valido = false;
        }
        if (valido) {
            enviar(new LoginRequest(email, password));
        }
    }

    private void enviar(LoginRequest datos) {
        mostrarEnviando(true);

        llamadaEnCurso = ApiClient.create(AuthApiService.class).login(datos);
        llamadaEnCurso.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> respuesta) {
                if (isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);

                if (respuesta.isSuccessful() && respuesta.body() != null) {
                    iniciarSesion(respuesta.body());
                } else if (respuesta.code() == 401) {
                    etPassword.setError(getString(R.string.login_error_credenciales));
                    mostrarMensaje(getString(R.string.login_error_credenciales));
                } else if (respuesta.code() == 403) {
                    mostrarCuentaPendiente();
                } else {
                    mostrarMensaje(getString(R.string.login_error_generico));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable error) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);
                mostrarMensaje(getString(R.string.login_error_conexion));
            }
        });
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
    private void mostrarEnviando(boolean enviando) {
        btnLogin.setEnabled(!enviando);
        btnLogin.setText(enviando ? getString(R.string.login_boton_ingresando) : textoBotonOriginal);
    }
}