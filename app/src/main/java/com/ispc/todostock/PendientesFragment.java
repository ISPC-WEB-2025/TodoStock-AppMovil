package com.ispc.todostock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.AprobarUsuarioRequest;
import com.ispc.todostock.network.UsuarioApiService;
import com.ispc.todostock.network.UsuarioDto;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TK109: Fragmento para listar, aprobar (asignando rol) y rechazar solicitudes de registro pendientes.
 */
public class PendientesFragment extends Fragment implements UsuarioPendienteAdapter.OnAccionPendienteListener {

    private ProgressBar progressBar;
    private TextView tvVacio;
    private RecyclerView rvPendientes;
    private UsuarioPendienteAdapter adapter;
    private UsuarioApiService usuarioApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pendientes, container, false);

        progressBar = view.findViewById(R.id.progressBarPendientes);
        tvVacio = view.findViewById(R.id.tvPendientesVacio);
        rvPendientes = view.findViewById(R.id.rvPendientes);

        usuarioApi = ApiClient.create(UsuarioApiService.class);

        adapter = new UsuarioPendienteAdapter(this);
        rvPendientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPendientes.setAdapter(adapter);

        cargarPendientes();

        return view;
    }

    public void cargarPendientes() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        usuarioApi.listarUsuarios().enqueue(new Callback<List<UsuarioDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioDto>> call, @NonNull Response<List<UsuarioDto>> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<UsuarioDto> pendientes = new ArrayList<>();
                    for (UsuarioDto u : response.body()) {
                        if (u.esPendiente()) {
                            pendientes.add(u);
                        }
                    }
                    adapter.setUsuarios(pendientes);
                    actualizarEstadoVacio();
                } else {
                    actualizarEstadoVacio();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioDto>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                actualizarEstadoVacio();
            }
        });
    }

    private void actualizarEstadoVacio() {
        if (adapter.getItemCountReal() == 0) {
            tvVacio.setVisibility(View.VISIBLE);
        } else {
            tvVacio.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAprobar(UsuarioDto usuario) {
        String[] opcionesRoles = new String[]{"Empleado (Ventas / Depósito)", "Administrador"};
        final int[] rolSeleccionadoId = {2}; // default: Empleado

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.usuarios_aprobar_titulo))
                .setSingleChoiceItems(opcionesRoles, 0, (dialog, which) -> {
                    rolSeleccionadoId[0] = (which == 0) ? 2 : 1;
                })
                .setPositiveButton(R.string.usuarios_btn_confirmar, (dialog, which) -> {
                    ejecutarAprobacion(usuario, rolSeleccionadoId[0]);
                })
                .setNegativeButton(R.string.usuarios_btn_cancelar, null)
                .show();
    }

    private void ejecutarAprobacion(UsuarioDto usuario, int rolId) {
        progressBar.setVisibility(View.VISIBLE);
        AprobarUsuarioRequest request = new AprobarUsuarioRequest(rolId);

        usuarioApi.actualizarUsuario(usuario.id, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    adapter.removerUsuario(usuario.id);
                    actualizarEstadoVacio();
                    Toast.makeText(requireContext(), R.string.usuarios_aprobado_ok, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "No se pudo aprobar la solicitud (" + response.code() + ")", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRechazar(UsuarioDto usuario) {
        String mensaje = getString(R.string.usuarios_rechazar_mensaje, usuario.nombre != null ? usuario.nombre : "este usuario");

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.usuarios_rechazar_titulo)
                .setMessage(mensaje)
                .setPositiveButton(R.string.usuarios_btn_confirmar, (dialog, which) -> {
                    ejecutarRechazo(usuario);
                })
                .setNegativeButton(R.string.usuarios_btn_cancelar, null)
                .show();
    }

    private void ejecutarRechazo(UsuarioDto usuario) {
        progressBar.setVisibility(View.VISIBLE);

        usuarioApi.desactivarUsuario(usuario.id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    adapter.removerUsuario(usuario.id);
                    actualizarEstadoVacio();
                    Toast.makeText(requireContext(), R.string.usuarios_rechazado_ok, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "No se pudo rechazar la solicitud (" + response.code() + ")", Toast.LENGTH_SHORT).show();
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
