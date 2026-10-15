package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.PerfilApiService;
import com.ispc.todostock.network.PerfilResponse;
import com.ispc.todostock.sesion.SesionManager;
import com.ispc.todostock.validacion.Validadores;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** TK95 - Muestra los datos del usuario autenticado (GET /api/usuarios/me/). */
public class PerfilActivity extends AppCompatActivity {

    public static final String EXTRA_NOMBRE = "perfil_nombre";
    public static final String EXTRA_DNI = "perfil_dni";
    public static final String EXTRA_FECHA = "perfil_fecha"; // dd/MM/yyyy

    private TextView tvNombre, tvEmail, tvDni, tvFecha, tvRol, tvError;
    private ProgressBar progreso;
    private Button btnEditar;

    private Call<PerfilResponse> llamadaEnCurso;
    private PerfilResponse perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarPerfil);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvNombre = findViewById(R.id.tvPerfilNombre);
        tvEmail = findViewById(R.id.tvPerfilEmail);
        tvDni = findViewById(R.id.tvPerfilDni);
        tvFecha = findViewById(R.id.tvPerfilFecha);
        tvRol = findViewById(R.id.tvPerfilRol);
        tvError = findViewById(R.id.tvErrorPerfil);
        progreso = findViewById(R.id.progresoPerfil);
        btnEditar = findViewById(R.id.btnEditarPerfil);

        btnEditar.setOnClickListener(v -> {
            if (perfil == null) return;
            Intent intent = new Intent(this, EditarPerfilActivity.class);
            intent.putExtra(EXTRA_NOMBRE, perfil.nombre);
            intent.putExtra(EXTRA_DNI, perfil.dni);
            intent.putExtra(EXTRA_FECHA, Validadores.fechaDesdeApi(perfil.fechaNacimiento));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfil();
    }

    @Override
    protected void onDestroy() {
        if (llamadaEnCurso != null) llamadaEnCurso.cancel();
        super.onDestroy();
    }

    private void cargarPerfil() {
        SesionManager sesion = SesionManager.getInstance(this);
        if (!sesion.haySesion()) {
            irALogin();
            return;
        }
        String bearer = "Bearer " + sesion.getAccessToken();
        tvError.setVisibility(View.GONE);
        progreso.setVisibility(View.VISIBLE);

        llamadaEnCurso = ApiClient.create(PerfilApiService.class).obtenerPerfil(bearer);
        llamadaEnCurso.enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(@NonNull Call<PerfilResponse> call,
                                   @NonNull Response<PerfilResponse> respuesta) {
                if (isFinishing() || isDestroyed()) return;
                progreso.setVisibility(View.GONE);

                if (respuesta.isSuccessful() && respuesta.body() != null) {
                    mostrar(respuesta.body());
                } else if (respuesta.code() == 401) {
                    irALogin();
                } else {
                    mostrarError(getString(R.string.perfil_error_cargar));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PerfilResponse> call, @NonNull Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                progreso.setVisibility(View.GONE);
                mostrarError(getString(R.string.perfil_error_conexion));
            }
        });
    }

    private void mostrar(PerfilResponse p) {
        perfil = p;
        tvNombre.setText(p.nombre);
        tvEmail.setText(p.email);
        tvDni.setText(p.dni);
        tvFecha.setText(Validadores.fechaDesdeApi(p.fechaNacimiento));
        tvRol.setText(p.rol != null ? p.rol.nombre : getString(R.string.perfil_sin_rol));
        btnEditar.setEnabled(true);
    }

    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
    }

    private void irALogin() {
        SesionManager.getInstance(this).cerrarSesion();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}