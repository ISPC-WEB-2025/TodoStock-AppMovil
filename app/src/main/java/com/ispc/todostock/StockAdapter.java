package com.ispc.todostock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        // Destacar si está bajo stock.
        // TK79: colores de la app (antes estaban escritos a mano y no tenían contraste).
        // Sobre el fondo de la tarjeta: color_errores 6.88, color_botones 6.09, color_texto 15.64.
        if (item.getCantidad() <= 0) {
            int rojo = ContextCompat.getColor(context, R.color.color_errores);
            holder.tvEstado.setText("Sin Stock");
            holder.tvEstado.setTextColor(rojo);
            holder.tvCantidad.setTextColor(rojo);
        } else if (item.esBajoStock()) {
            int rojo = ContextCompat.getColor(context, R.color.color_errores);
            holder.tvEstado.setText("¡Stock Bajo!");
            holder.tvEstado.setTextColor(rojo);
            holder.tvCantidad.setTextColor(rojo);
        } else {
            holder.tvEstado.setText("Disponible");
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.color_botones));
            holder.tvCantidad.setTextColor(ContextCompat.getColor(context, R.color.color_texto));
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
