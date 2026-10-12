package com.ispc.todostock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ispc.todostock.network.UsuarioDto;

import java.util.ArrayList;
import java.util.List;

/**
 * TK109: Adaptador para mostrar la lista de solicitudes de registro pendientes de aprobación.
 */
public class UsuarioPendienteAdapter extends RecyclerView.Adapter<UsuarioPendienteAdapter.ViewHolder> {

    public interface OnAccionPendienteListener {
        void onAprobar(UsuarioDto usuario);
        void onRechazar(UsuarioDto usuario);
    }

    private final List<UsuarioDto> items = new ArrayList<>();
    private final OnAccionPendienteListener listener;

    public UsuarioPendienteAdapter(OnAccionPendienteListener listener) {
        this.listener = listener;
    }

    public void setUsuarios(List<UsuarioDto> nuevosUsuarios) {
        items.clear();
        if (nuevosUsuarios != null) {
            items.addAll(nuevosUsuarios);
        }
        notifyDataSetChanged();
    }

    public void removerUsuario(int idUsuario) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).id == idUsuario) {
                items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public int getItemCountReal() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_pendiente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsuarioDto usuario = items.get(position);
        holder.tvNombre.setText(usuario.nombre != null ? usuario.nombre : "Sin nombre");
        holder.tvEmail.setText(usuario.email != null ? usuario.email : "");
        holder.tvDni.setText(usuario.dni != null ? "DNI: " + usuario.dni : "");

        holder.btnAprobar.setOnClickListener(v -> {
            if (listener != null) listener.onAprobar(usuario);
        });

        holder.btnRechazar.setOnClickListener(v -> {
            if (listener != null) listener.onRechazar(usuario);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNombre;
        final TextView tvEmail;
        final TextView tvDni;
        final MaterialButton btnAprobar;
        final MaterialButton btnRechazar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePendiente);
            tvEmail = itemView.findViewById(R.id.tvEmailPendiente);
            tvDni = itemView.findViewById(R.id.tvDniPendiente);
            btnAprobar = itemView.findViewById(R.id.btnAprobar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}
