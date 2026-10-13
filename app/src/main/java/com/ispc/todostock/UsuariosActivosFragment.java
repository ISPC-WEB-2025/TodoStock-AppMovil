package com.ispc.todostock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.EditarUsuarioRequest;
import com.ispc.todostock.network.UsuarioApiService;
import com.ispc.todostock.network.UsuarioDto;
import com.ispc.todostock.sesion.SesionManager;
import com.ispc.todostock.validacion.Validadores;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TK37: Pestaña "Usuarios" de Gestión de usuarios (US08).
 *
 * Lista los usuarios activos y permite editar sus datos o darlos de baja con un
 * diálogo de confirmación (baja lógica en el backend: is_active = false).
 *
 * - Solo muestra usuarios que no están pendientes (UsuarioDto.esPendiente()): los pendientes
 *   se gestionan en la pestaña Pendientes (TK109) y los dados de baja dejan de aparecer.
 * - Un Administrador no ve a otros Administradores; solo el Super Administrador los gestiona (ADR-0008).
 * - Solo el Super Administrador puede cambiar el rol; el backend también lo valida.
 */
public class UsuariosActivosFragment extends Fragment implements UsuarioActivoAdapter.OnAccionUsuarioListener {

    // Mismos ids de rol que usa PendientesFragment (TK109).
    private static final int ROL_ID_ADMINISTRADOR = 1;
    private static final int ROL_ID_EMPLEADO = 2;

    private ProgressBar progressBar;
    private TextView tvVacio;
    private UsuarioActivoAdapter adapter;
    private UsuarioApiService usuarioApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios_activos, container, false);

        progressBar = view.findViewById(R.id.progressBarUsuarios);
        tvVacio = view.findViewById(R.id.tvUsuariosVacio);
        RecyclerView rvUsuarios = view.findViewById(R.id.rvUsuarios);

        usuarioApi = ApiClient.create(UsuarioApiService.class);

        adapter = new UsuarioActivoAdapter(this);
        rvUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvUsuarios.setAdapter(adapter);

        return view;
    }

    /**
     * Se recarga cada vez que la pestaña queda visible, así un usuario aprobado
     * en Pendientes o creado en Cargar usuario aparece sin salir de la pantalla.
     */
    @Override
    public void onResume() {
        super.onResume();
        cargarUsuarios();
    }

    // ---------- Lista ----------

    public void cargarUsuarios() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        usuarioApi.listarUsuarios().enqueue(new Callback<List<UsuarioDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioDto>> call, @NonNull Response<List<UsuarioDto>> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    adapter.setUsuarios(filtrarUsuarios(response.body()));
                } else {
                    Toast.makeText(requireContext(), getString(R.string.usuarios_error_cargar, response.code()), Toast.LENGTH_SHORT).show();
                }
                actualizarEstadoVacio();
            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioDto>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), R.string.usuarios_error_red, Toast.LENGTH_SHORT).show();
                actualizarEstadoVacio();
            }
        });
    }

    /** Deja solo los usuarios activos que quien opera puede gestionar. */
    private List<UsuarioDto> filtrarUsuarios(List<UsuarioDto> todos) {
        boolean operadorEsSuperadmin = SesionManager.getInstance(requireContext()).esSuperadmin();
        List<UsuarioDto> activos = new ArrayList<>();
        for (UsuarioDto u : todos) {
            if (u.esPendiente()) {
                continue;
            }
            // Un administrador estándar no puede gestionar cuentas de otros administradores (ADR-0008).
            if (!operadorEsSuperadmin && esCuentaAdmin(u)) {
                continue;
            }
            activos.add(u);
        }
        return activos;
    }

    /** Mismo criterio que PendientesFragment (TK109) para reconocer una cuenta de administrador. */
    private static boolean esCuentaAdmin(UsuarioDto u) {
        return u.isSuperuser || u.esAdmin
                || (u.rol != null && u.rol.nombre != null && u.rol.nombre.toUpperCase().contains("ADMIN"));
    }

    private void actualizarEstadoVacio() {
        tvVacio.setVisibility(adapter.getItemCountReal() == 0 ? View.VISIBLE : View.GONE);
    }

    // ---------- Editar ----------

    @Override
    public void onEditar(UsuarioDto usuario) {
        View vista = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_usuario, null);

        TextInputLayout tilNombre = vista.findViewById(R.id.tilEditarNombre);
        TextInputLayout tilEmail = vista.findViewById(R.id.tilEditarEmail);
        TextInputLayout tilDni = vista.findViewById(R.id.tilEditarDni);
        TextInputLayout tilRol = vista.findViewById(R.id.tilEditarRol);
        TextInputEditText etNombre = vista.findViewById(R.id.etEditarNombre);
        TextInputEditText etEmail = vista.findViewById(R.id.etEditarEmail);
        TextInputEditText etDni = vista.findViewById(R.id.etEditarDni);
        AutoCompleteTextView actvRol = vista.findViewById(R.id.actvEditarRol);

        etNombre.setText(usuario.nombre);
        etEmail.setText(usuario.email);
        etDni.setText(usuario.dni);

        // El rol solo se envía si el Super Administrador elige uno; si no, queda como estaba.
        final Integer[] rolElegido = {null};
        if (SesionManager.getInstance(requireContext()).esSuperadmin()) {
            String[] opciones = {getString(R.string.usuarios_rol_empleado), getString(R.string.usuarios_rol_administrador)};
            actvRol.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, opciones));
            actvRol.setText(esCuentaAdmin(usuario) ? opciones[1] : opciones[0], false);
            actvRol.setOnItemClickListener((parent, v, position, id) ->
                    rolElegido[0] = position == 1 ? ROL_ID_ADMINISTRADOR : ROL_ID_EMPLEADO);
            tilRol.setVisibility(View.VISIBLE);
        }

        AlertDialog dialogo = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.usuarios_editar_titulo)
                .setView(vista)
                // El listener de Guardar se asigna después de show() para que el diálogo no se cierre si hay errores.
                .setPositiveButton(R.string.usuarios_btn_guardar, null)
                .setNegativeButton(R.string.usuarios_btn_cancelar, null)
                .show();

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = texto(etNombre);
            String email = texto(etEmail);
            String dni = texto(etDni);

            tilNombre.setError(null);
            tilEmail.setError(null);
            tilDni.setError(null);

            boolean valido = true;
            if (!Validadores.nombreValido(nombre)) {
                tilNombre.setError(getString(R.string.usuarios_error_nombre, Validadores.NOMBRE_MIN));
                valido = false;
            }
            if (!Validadores.emailValido(email)) {
                tilEmail.setError(getString(R.string.usuarios_error_email));
                valido = false;
            }
            if (!Validadores.dniValido(dni)) {
                tilDni.setError(getString(R.string.usuarios_error_dni));
                valido = false;
            }
            if (!valido) return;

            EditarUsuarioRequest request = new EditarUsuarioRequest(nombre, email, dni, rolElegido[0]);
            guardarCambios(usuario, request, dialogo, tilNombre, tilEmail, tilDni);
        });
    }

    private void guardarCambios(UsuarioDto usuario, EditarUsuarioRequest request, AlertDialog dialogo,
                                TextInputLayout tilNombre, TextInputLayout tilEmail, TextInputLayout tilDni) {
        progressBar.setVisibility(View.VISIBLE);
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        usuarioApi.editarUsuario(usuario.id, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                if (response.isSuccessful()) {
                    dialogo.dismiss();
                    Toast.makeText(requireContext(), R.string.usuarios_editado_ok, Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                } else {
                    mostrarErrorEdicion(response, tilNombre, tilEmail, tilDni);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                Toast.makeText(requireContext(), R.string.usuarios_error_red, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Muestra el error del backend: los de un campo (por ejemplo, email o DNI ya registrados)
     * debajo del campo, y los demás (rol o permisos) en un Toast.
     */
    private void mostrarErrorEdicion(Response<ResponseBody> response,
                                     TextInputLayout tilNombre, TextInputLayout tilEmail, TextInputLayout tilDni) {
        String mensaje = getString(R.string.usuarios_error_editar, response.code());
        try {
            JSONObject json = new JSONObject(response.errorBody() != null ? response.errorBody().string() : "");
            boolean campoAfectado = false;
            if (json.has("nombre")) {
                tilNombre.setError(json.getJSONArray("nombre").getString(0));
                campoAfectado = true;
            }
            if (json.has("email")) {
                tilEmail.setError(json.getJSONArray("email").getString(0));
                campoAfectado = true;
            }
            if (json.has("dni")) {
                tilDni.setError(json.getJSONArray("dni").getString(0));
                campoAfectado = true;
            }
            if (campoAfectado) return;

            if (json.has("rol_id")) {
                mensaje = json.getJSONArray("rol_id").getString(0);
            } else if (json.has("detail")) {
                mensaje = json.getString("detail");
            } else if (json.has("error")) {
                mensaje = json.getString("error");
            }
        } catch (Exception ignored) {
        }
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() != null ? campo.getText().toString().trim() : "";
    }

    // ---------- Dar de baja ----------

    @Override
    public void onDarDeBaja(UsuarioDto usuario) {
        String nombre = usuario.nombre != null && !usuario.nombre.trim().isEmpty()
                ? usuario.nombre
                : getString(R.string.usuarios_sin_nombre);

        // Si se cancela, no se envía nada y la lista queda igual.
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.usuarios_baja_titulo)
                .setMessage(getString(R.string.usuarios_baja_mensaje, nombre))
                .setPositiveButton(R.string.usuarios_btn_confirmar, (dialog, which) -> ejecutarBaja(usuario))
                .setNegativeButton(R.string.usuarios_btn_cancelar, null)
                .show();
    }

    private void ejecutarBaja(UsuarioDto usuario) {
        progressBar.setVisibility(View.VISIBLE);

        usuarioApi.desactivarUsuario(usuario.id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    adapter.removerUsuario(usuario.id);
                    actualizarEstadoVacio();
                    Toast.makeText(requireContext(), R.string.usuarios_baja_ok, Toast.LENGTH_SHORT).show();
                } else {
                    String mensaje = getString(R.string.usuarios_error_baja, response.code());
                    try {
                        JSONObject json = new JSONObject(response.errorBody() != null ? response.errorBody().string() : "");
                        if (json.has("detail")) {
                            mensaje = json.getString("detail");
                        } else if (json.has("error")) {
                            mensaje = json.getString("error");
                        }
                    } catch (Exception ignored) {
                    }
                    Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), R.string.usuarios_error_red, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
