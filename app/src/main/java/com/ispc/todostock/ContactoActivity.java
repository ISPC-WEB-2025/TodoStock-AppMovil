package com.ispc.todostock;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.ContactoApiService;
import com.ispc.todostock.network.ContactoRequest;
import com.ispc.todostock.validacion.Validadores;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etNombre, etEmail, etAsunto, etMensaje;
    private TextInputLayout tilNombre, tilEmail, tilAsunto, tilMensaje;
    private Button btnEnviar;

    private Call<ResponseBody> llamadaEnCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        toolbar = findViewById(R.id.toolbarMenu);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etAsunto = findViewById(R.id.etAsunto);
        etMensaje = findViewById(R.id.etMensaje);

        tilNombre = findViewById(R.id.tilNombre);
        tilEmail = findViewById(R.id.tilEmail);
        tilAsunto = findViewById(R.id.tilAsunto);
        tilMensaje = findViewById(R.id.tilMensaje);

        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(v -> validarYEnviar());
    }

    @Override
    protected void onDestroy() {
        // Si se cierra la pantalla con un envío en curso, se cancela.
        if (llamadaEnCurso != null) {
            llamadaEnCurso.cancel();
        }
        super.onDestroy();
    }

    private void validarYEnviar() {
        limpiarErrores();

        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String asunto = etAsunto.getText().toString().trim();
        String mensaje = etMensaje.getText().toString().trim();

        // TK42: se valida antes de enviar, así no se llama a la API con datos incompletos.
        boolean todoValido = true;

        if (nombre.isEmpty()) {
            tilNombre.setError(getString(R.string.contacto_error_nombre));
            todoValido = false;
        }
        if (!Validadores.emailValido(email)) {
            tilEmail.setError(getString(R.string.contacto_error_email));
            todoValido = false;
        }
        if (asunto.isEmpty()) {
            tilAsunto.setError(getString(R.string.contacto_error_asunto));
            todoValido = false;
        }
        if (mensaje.isEmpty()) {
            tilMensaje.setError(getString(R.string.contacto_error_mensaje));
            todoValido = false;
        }

        if (!todoValido) {
            return;
        }


        String mensajeConNombre = getString(R.string.contacto_mensaje_con_nombre, nombre, mensaje);
        enviar(new ContactoRequest(email, asunto, mensajeConNombre));
    }

    private void enviar(ContactoRequest datos) {
        mostrarEnviando(true);

        llamadaEnCurso = ApiClient.create(ContactoApiService.class).enviar(datos);
        llamadaEnCurso.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> respuesta) {
                if (isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);

                if (respuesta.isSuccessful()) {
                    // La confirmación se muestra solo si el backend respondió bien.
                    Toast.makeText(ContactoActivity.this, R.string.contacto_exito, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String error = leerMensajeDeError(respuesta);
                    Toast.makeText(ContactoActivity.this,
                            error != null ? error : getString(R.string.contacto_error_generico),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);
                Toast.makeText(ContactoActivity.this, R.string.contacto_error_conexion, Toast.LENGTH_LONG).show();
            }
        });
    }


    private String leerMensajeDeError(Response<ResponseBody> respuesta) {
        try (ResponseBody cuerpo = respuesta.errorBody()) {
            if (cuerpo == null) return null;
            String mensaje = new JSONObject(cuerpo.string()).optString("error", "");
            return mensaje.isEmpty() ? null : mensaje;
        } catch (Exception e) {
            return null;
        }
    }

    private void mostrarEnviando(boolean enviando) {
        btnEnviar.setEnabled(!enviando);
        btnEnviar.setText(enviando
                ? R.string.contacto_boton_enviando
                : R.string.contacto_boton_enviar);
    }

    private void limpiarErrores() {
        tilNombre.setError(null);
        tilEmail.setError(null);
        tilAsunto.setError(null);
        tilMensaje.setError(null);
    }
}