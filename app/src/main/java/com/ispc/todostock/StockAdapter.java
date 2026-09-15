package com.ispc.todostock;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * TK14: StockAdapter con ViewHolder para RecyclerView
 * Responsable: Virginia Garcia
 */
public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final Context context;
    private List<StockItem> listaProductos;

    public StockAdapter(Context context, List<StockItem> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    public void actualizarLista(List<StockItem> nuevaLista) {
        this.listaProductos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        StockItem item = listaProductos.get(position);

        holder.tvNombre.setText(item.getNombre());
        holder.tvCodigo.setText("Código: " + item.getCodigo());
        holder.tvCantidad.setText(item.getCantidad() + " un.");

        // Destacar si está bajo stock
        if (item.esBajoStock()) {
            holder.tvEstado.setText("¡Stock Bajo!");
            holder.tvEstado.setTextColor(Color.parseColor("#DC2626")); // color_error
            holder.tvCantidad.setTextColor(Color.parseColor("#DC2626"));
        } else {
            holder.tvEstado.setText("Disponible");
            holder.tvEstado.setTextColor(Color.parseColor("#16A34A")); // color_success
            holder.tvCantidad.setTextColor(Color.parseColor("#1E3A8A")); // color_primary
        }
    }

    @Override
    public int getItemCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvCodigo;
        TextView tvCantidad;
        TextView tvEstado;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvCodigo = itemView.findViewById(R.id.tvCodigoProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidadStock);
            tvEstado = itemView.findViewById(R.id.tvEstadoStock);
        }
    }
}