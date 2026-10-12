package com.ispc.todostock;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.CrearUsuarioRequest;
import com.ispc.todostock.network.RolDto;
import com.ispc.todostock.network.UsuarioApiService;
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
 * TK109: Formulario para dar de alta a un usuario nuevo con rol asignado por el Administrador.
 */
public class CargarUsuarioFragment extends Fragment {

    private TextInputLayout tilNombre, tilEmail, tilDni, tilFdn, tilRol, tilPassword;
    private TextInputEditText etNombre, etEmail, etDni, etFdn, etPassword;
    private AutoCompleteTextView actvRol;
    private ProgressBar progressBar;
    private MaterialButton btnCrear;

    private final List<RolDto> rolesDisponibles = new ArrayList<>();
    private RolDto rolSeleccionado;
    private UsuarioApiService usuarioApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cargar_usuario, container, false);

        tilNombre = view.findViewById(R.id.tilNombre);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilDni = view.findViewById(R.id.tilDni);
        tilFdn = view.findViewById(R.id.tilFdn);
        tilRol = view.findViewById(R.id.tilRol);
        tilPassword = view.findViewById(R.id.tilPassword);

        etNombre = view.findViewById(R.id.etNombre);
        etEmail = view.findViewById(R.id.etEmail);
        etDni = view.findViewById(R.id.etDni);
        etFdn = view.findViewById(R.id.etFdn);
        etPassword = view.findViewById(R.id.etPassword);
        actvRol = view.findViewById(R.id.actvRol);

        progressBar = view.findViewById(R.id.progressBarCargar);
        btnCrear = view.findViewById(R.id.btnCrearUsuario);

        usuarioApi = ApiClient.create(UsuarioApiService.class);

        configurarLimpiezaErrores();
        cargarRoles();

        btnCrear.setOnClickListener(v -> validarYCrear());

        return view;
    }

    private void configurarLimpiezaErrores() {
        agregarWatcher(etNombre, tilNombre);
        agregarWatcher(etEmail, tilEmail);
        agregarWatcher(etDni, tilDni);
        agregarWatcher(etFdn, tilFdn);
        agregarWatcher(etPassword, tilPassword);
    }

    private void agregarWatcher(TextInputEditText et, TextInputLayout til) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarRoles() {
        boolean esSuperadmin = SesionManager.getInstance(requireContext()).esSuperadmin();
        usuarioApi.obtenerRoles().enqueue(new Callback<List<RolDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RolDto>> call, @NonNull Response<List<RolDto>> response) {
                if (!isAdded()) return;
                rolesDisponibles.clear();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    procesarRolesApi(response.body(), esSuperadmin);
                } else {
                    cargarRolesPorDefecto(esSuperadmin);
                }
                configurarAdapterRoles();
            }

            @Override
            public void onFailure(@NonNull Call<List<RolDto>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                cargarRolesPorDefecto(esSuperadmin);
                configurarAdapterRoles();
            }
        });
    }

    private void procesarRolesApi(List<RolDto> rolesApi, boolean esSuperadmin) {
        rolesDisponibles.clear();

        // 1. Rol Empleado (Vendedor) - obligatorio para todos los admins
        int idVendedor = 2;
        for (RolDto r : rolesApi) {
            String nom = r.nombre != null ? r.nombre.toUpperCase() : "";
            if (nom.contains("VENT") || nom.contains("EMP")) {
                idVendedor = r.id;
                break;
            }
        }
        rolesDisponibles.add(new RolDto(idVendedor, "Empleado (Vendedor)", "Rol operativo"));

        // 2. Rol Administrador - solo si quien opera es Superadmin
        if (esSuperadmin) {
            int idAdmin = 1;
            for (RolDto r : rolesApi) {
                String nom = r.nombre != null ? r.nombre.toUpperCase() : "";
                if (nom.contains("ADMIN")) {
                    idAdmin = r.id;
                    break;
                }
            }
            rolesDisponibles.add(new RolDto(idAdmin, "Administrador", "Control total del sistema"));
        }
    }

    private void cargarRolesPorDefecto(boolean esSuperadmin) {
        rolesDisponibles.clear();
        rolesDisponibles.add(new RolDto(2, "Empleado (Vendedor)", "Rol operativo"));
        if (esSuperadmin) {
            rolesDisponibles.add(new RolDto(1, "Administrador", "Control total del sistema"));
        }
    }

    private void configurarAdapterRoles() {
        List<String> nombresRoles = new ArrayList<>();
        for (RolDto r : rolesDisponibles) {
            nombresRoles.add(r.nombre);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, nombresRoles);
        actvRol.setAdapter(adapter);

        if (!rolesDisponibles.isEmpty()) {
            rolSeleccionado = rolesDisponibles.get(0);
            actvRol.setText(rolSeleccionado.nombre, false);
        }

        actvRol.setOnItemClickListener((parent, view, position, id) -> {
            rolSeleccionado = rolesDisponibles.get(position);
            tilRol.setError(null);
        });
    }

    private void validarYCrear() {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String dni = etDni.getText() != null ? etDni.getText().toString().trim() : "";
        String fdn = etFdn.getText() != null ? etFdn.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        boolean valido = true;

        if (!Validadores.nombreValido(nombre)) {
            tilNombre.setError("Mínimo " + Validadores.NOMBRE_MIN + " caracteres");
            valido = false;
        }

        if (!Validadores.emailValido(email)) {
            tilEmail.setError("Formato de correo inválido");
            valido = false;
        }

        if (!Validadores.dniValido(dni)) {
            tilDni.setError("Debe tener 7 u 8 números");
            valido = false;
        }

        if (!Validadores.fechaNacimientoValida(fdn)) {
            tilFdn.setError("Fecha inválida (dd/MM/yyyy)");
            valido = false;
        } else if (!Validadores.esMayorDeEdad(fdn)) {
            tilFdn.setError("Debe ser mayor de " + Validadores.EDAD_MINIMA + " años");
            valido = false;
        }

        if (rolSeleccionado == null) {
            tilRol.setError("Debe seleccionar un rol");
            valido = false;
        }

        if (!Validadores.passwordValida(password)) {
            tilPassword.setError("Mínimo " + Validadores.PASSWORD_MIN + " caracteres, letra, número y símbolo");
            valido = false;
        }

        if (!valido) return;

        String fechaApi = Validadores.fechaAFormatoApi(fdn);
        CrearUsuarioRequest request = new CrearUsuarioRequest(nombre, email, dni, fechaApi, password, rolSeleccionado.id);

        setEnProgreso(true);

        usuarioApi.crearUsuario(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!isAdded()) return;
                setEnProgreso(false);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.usuarios_creado_ok, Toast.LENGTH_LONG).show();
                    limpiarCampos();
                } else {
                    manejarErrorServidor(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                setEnProgreso(false);
                Toast.makeText(requireContext(), R.string.usuarios_error_red, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void manejarErrorServidor(Response<ResponseBody> response) {
        try {
            String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
            JSONObject json = new JSONObject(errorJson);

            boolean campoAfectado = false;
            if (json.has("email")) {
                tilEmail.setError(json.getJSONArray("email").getString(0));
                campoAfectado = true;
            }
            if (json.has("dni")) {
                tilDni.setError(json.getJSONArray("dni").getString(0));
                campoAfectado = true;
            }
            if (json.has("nombre")) {
                tilNombre.setError(json.getJSONArray("nombre").getString(0));
                campoAfectado = true;
            }
            if (json.has("fecha_nacimiento")) {
                tilFdn.setError(json.getJSONArray("fecha_nacimiento").getString(0));
                campoAfectado = true;
            }
            if (json.has("rol_id")) {
                tilRol.setError(json.getJSONArray("rol_id").getString(0));
                campoAfectado = true;
            }
            if (json.has("password")) {
                tilPassword.setError(json.getJSONArray("password").getString(0));
                campoAfectado = true;
            }

            if (json.has("error")) {
                Toast.makeText(requireContext(), json.getString("error"), Toast.LENGTH_LONG).show();
            } else if (!campoAfectado) {
                Toast.makeText(requireContext(), "Error al crear usuario (" + response.code() + ")", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al procesar la solicitud (" + response.code() + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etEmail.setText("");
        etDni.setText("");
        etFdn.setText("");
        etPassword.setText("");
        etNombre.requestFocus();
    }

    private void setEnProgreso(boolean enProgreso) {
        progressBar.setVisibility(enProgreso ? View.VISIBLE : View.GONE);
        btnCrear.setEnabled(!enProgreso);
        btnCrear.setAlpha(enProgreso ? 0.6f : 1.0f);
    }
}
