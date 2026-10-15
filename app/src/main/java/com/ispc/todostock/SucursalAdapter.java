package com.ispc.todostock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SucursalAdapter extends RecyclerView.Adapter<SucursalAdapter.SucursalViewHolder> {

    private List<Sucursal> listaSucursales;
    private OnSucursalClickListener listener;

    public interface OnSucursalClickListener {
        void onSucursalClick(Sucursal sucursal);
    }

    public SucursalAdapter(List<Sucursal> listaSucursales, OnSucursalClickListener listener) {
        this.listaSucursales = listaSucursales;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SucursalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sucursal, parent, false);
        return new SucursalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SucursalViewHolder holder, int position) {
        Sucursal sucursal = listaSucursales.get(position);
        holder.tvNombre.setText(sucursal.getNombre());
        holder.tvDireccion.setText(sucursal.getDireccion());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSucursalClick(sucursal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaSucursales.size();
    }

    public static class SucursalViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvDireccion;

        public SucursalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreSucursal);
            tvDireccion = itemView.findViewById(R.id.tvDireccionSucursal);
        }
    }
}