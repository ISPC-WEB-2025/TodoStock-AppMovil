package com.ispc.todostock;

import android.content.Context;
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
 * TK37: Adaptador para la lista de usuarios activos de Gestión de usuarios.
 * Muestra nombre, email, DNI y rol, con las acciones Editar y Dar de baja.
 */
public class UsuarioActivoAdapter extends RecyclerView.Adapter<UsuarioActivoAdapter.ViewHolder> {

    public interface OnAccionUsuarioListener {
        void onEditar(UsuarioDto usuario);
        void onDarDeBaja(UsuarioDto usuario);
    }

    private final List<UsuarioDto> items = new ArrayList<>();
    private final OnAccionUsuarioListener listener;

    public UsuarioActivoAdapter(OnAccionUsuarioListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_activo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsuarioDto usuario = items.get(position);
        Context context = holder.itemView.getContext();

        String nombre = usuario.nombre != null && !usuario.nombre.trim().isEmpty()
                ? usuario.nombre
                : context.getString(R.string.usuarios_sin_nombre);

        holder.tvNombre.setText(nombre);
        holder.tvEmail.setText(usuario.email != null ? usuario.email : "");
        holder.tvDni.setText(usuario.dni != null ? context.getString(R.string.usuarios_item_dni, usuario.dni) : "");
        holder.tvRol.setText(usuario.rol != null && usuario.rol.nombre != null ? usuario.rol.nombre : "");

        // Para TalkBack: cada botón dice sobre qué usuario actúa.
        holder.btnEditar.setContentDescription(context.getString(R.string.usuarios_desc_editar, nombre));
        holder.btnDarDeBaja.setContentDescription(context.getString(R.string.usuarios_desc_baja, nombre));

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEditar(usuario);
        });

        holder.btnDarDeBaja.setOnClickListener(v -> {
            if (listener != null) listener.onDarDeBaja(usuario);
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
        final TextView tvRol;
        final MaterialButton btnEditar;
        final MaterialButton btnDarDeBaja;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreUsuario);
            tvEmail = itemView.findViewById(R.id.tvEmailUsuario);
            tvDni = itemView.findViewById(R.id.tvDniUsuario);
            tvRol = itemView.findViewById(R.id.tvRolUsuario);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnDarDeBaja = itemView.findViewById(R.id.btnDarDeBaja);
        }
    }
}
