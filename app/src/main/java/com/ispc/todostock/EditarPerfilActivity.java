package com.ispc.todostock;
import com.ispc.todostock.PerfilActivity;
import com.ispc.todostock.R;
import com.ispc.todostock.sesion.SesionManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.PerfilApiService;
import com.ispc.todostock.network.PerfilResponse;
import com.ispc.todostock.network.PerfilUpdateRequest;
import com.ispc.todostock.validacion.Validadores;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** TK95 - Edición de datos propios (PATCH /api/usuarios/me/). */
public class EditarPerfilActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilDni, tilFecha;
    private EditText etNombre, etDni, etFecha;
    private TextView tvErrorGeneral;
    private Button btnGuardar;

    private Call<PerfilResponse> llamadaEnCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarEditarPerfil);
        toolbar.setNavigationOnClickListener(v -> finish());

        tilNombre = findViewById(R.id.tilNombre);
        tilDni = findViewById(R.id.tilDni);
        tilFecha = findViewById(R.id.tilFecha);
        etNombre = findViewById(R.id.etNombre);
        etDni = findViewById(R.id.etDni);
        etFecha = findViewById(R.id.etFecha);
        tvErrorGeneral = findViewById(R.id.tvErrorGeneral);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Los valores actuales llegan desde PerfilActivity; solo se precargan la primera vez.
        if (savedInstanceState == null) {
            Intent in = getIntent();
            etNombre.setText(in.getStringExtra(PerfilActivity.EXTRA_NOMBRE));
            etDni.setText(in.getStringExtra(PerfilActivity.EXTRA_DNI));
            etFecha.setText(in.getStringExtra(PerfilActivity.EXTRA_FECHA));
        }

        etFecha.setOnClickListener(v -> mostrarSelectorDeFecha());
        btnGuardar.setOnClickListener(v -> intentarGuardar());
    }

    @Override
    protected void onDestroy() {
        if (llamadaEnCurso != null) llamadaEnCurso.cancel();
        super.onDestroy();
    }

    private void mostrarSelectorDeFecha() {
        Calendar cal = Calendar.getInstance();
        String actual = etFecha.getText().toString().trim();
        if (Validadores.fechaAFormatoApi(actual) != null) {
            // actual = dd/MM/yyyy válido: se usa como fecha inicial del selector
            String[] p = actual.split("/");
            cal.set(Integer.parseInt(p[2]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[0]));
        }
        DatePickerDialog dialogo = new DatePickerDialog(this,
                (view, anio, mes, dia) -> etFecha.setText(
                        String.format(Locale.US, "%02d/%02d/%04d", dia, mes + 1, anio)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialogo.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialogo.show();
    }

    private void intentarGuardar() {
        limpiarErrores();

        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        boolean valido = true;
        if (!Validadores.nombreValido(nombre)) {
            tilNombre.setError(getString(R.string.registro_error_nombre, Validadores.NOMBRE_MIN));
            valido = false;
        }
        if (!Validadores.dniValido(dni)) {
            tilDni.setError(getString(R.string.registro_error_dni));
            valido = false;
        }
        if (!Validadores.fechaNacimientoValida(fecha)) {
            tilFecha.setError(getString(R.string.registro_error_fecha));
            valido = false;
        }
        if (!valido) return;

        SesionManager sesion = SesionManager.getInstance(this);
        if (!sesion.haySesion()) {
            mostrarErrorGeneral(getString(R.string.perfil_error_sesion));
            return;
        }
        String bearer = "Bearer " + sesion.getAccessToken();

        enviar(bearer, new PerfilUpdateRequest(nombre, dni, Validadores.fechaAFormatoApi(fecha)));
    }

    private void enviar(String bearer, PerfilUpdateRequest datos) {
        mostrarEnviando(true);
        llamadaEnCurso = ApiClient.create(PerfilApiService.class).actualizarPerfil(bearer, datos);
        llamadaEnCurso.enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(@NonNull Call<PerfilResponse> call,
                                   @NonNull Response<PerfilResponse> respuesta) {
                if (isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);

                if (respuesta.isSuccessful()) {
                    Toast.makeText(EditarPerfilActivity.this,
                            R.string.perfil_guardado, Toast.LENGTH_SHORT).show();
                    finish(); // PerfilActivity recarga en onResume
                } else if (respuesta.code() == 400) {
                    mostrarErroresDelServidor(respuesta.errorBody());
                } else if (respuesta.code() == 401) {
                    mostrarErrorGeneral(getString(R.string.perfil_error_sesion));
                } else {
                    mostrarErrorGeneral(getString(R.string.perfil_error_guardar));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PerfilResponse> call, @NonNull Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);
                mostrarErrorGeneral(getString(R.string.perfil_error_conexion));
            }
        });
    }

    /**
     * Formato del 400: {"error": "Datos inválidos", "detalle": {"dni": ["msg"], ...}}
     * Cada mensaje se muestra en el campo que corresponde.
     */
    private void mostrarErroresDelServidor(ResponseBody cuerpo) {
        try (ResponseBody body = cuerpo) {
            JSONObject detalle = new JSONObject(body.string()).optJSONObject("detalle");
            if (detalle == null) {
                mostrarErrorGeneral(getString(R.string.perfil_error_guardar));
                return;
            }
            StringBuilder otros = new StringBuilder();
            Iterator<String> campos = detalle.keys();
            while (campos.hasNext()) {
                String campo = campos.next();
                JSONArray msgs = detalle.optJSONArray(campo);
                String msg = (msgs != null && msgs.length() > 0) ? msgs.optString(0) : detalle.optString(campo);
                switch (campo) {
                    case "nombre": tilNombre.setError(msg); break;
                    case "dni": tilDni.setError(msg); break;
                    case "fecha_nacimiento": tilFecha.setError(msg); break;
                    default: otros.append(msg).append(' ');
                }
            }
            if (otros.length() > 0) mostrarErrorGeneral(otros.toString().trim());
        } catch (Exception e) {
            mostrarErrorGeneral(getString(R.string.perfil_error_guardar));
        }
    }

    private void mostrarErrorGeneral(String mensaje) {
        tvErrorGeneral.setText(mensaje);
        tvErrorGeneral.setVisibility(View.VISIBLE);
    }

    private void mostrarEnviando(boolean enviando) {
        btnGuardar.setEnabled(!enviando);
        btnGuardar.setText(enviando ? R.string.perfil_boton_guardando : R.string.perfil_boton_guardar);
    }

    private void limpiarErrores() {
        tilNombre.setError(null);
        tilDni.setError(null);
        tilFecha.setError(null);
        tvErrorGeneral.setVisibility(View.GONE);
    }
}