package com.ispc.todostock;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para AutoCompleteTextView que permite filtrar en vivo
 * por nombre de producto o por código.
 */
public class ProductoMovimientoAdapter extends ArrayAdapter<ProductoMovimientoItem> {

    private final List<ProductoMovimientoItem> listaOriginal;
    private List<ProductoMovimientoItem> listaFiltrada;

    public ProductoMovimientoAdapter(Context context, List<ProductoMovimientoItem> items) {
        super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(items));
        this.listaOriginal = new ArrayList<>(items);
        this.listaFiltrada = new ArrayList<>(items);
    }

    @Override
    public int getCount() {
        return listaFiltrada.size();
    }

    @Override
    public ProductoMovimientoItem getItem(int position) {
        return (position >= 0 && position < listaFiltrada.size()) ? listaFiltrada.get(position) : null;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.toString().trim().isEmpty()) {
                    results.values = listaOriginal;
                    results.count = listaOriginal.size();
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    List<ProductoMovimientoItem> filtrados = new ArrayList<>();
                    for (ProductoMovimientoItem item : listaOriginal) {
                        boolean coincideNombre = item.getNombre() != null && item.getNombre().toLowerCase().contains(query);
                        boolean coincideCodigo = item.getCodigo() != null && item.getCodigo().toLowerCase().contains(query);
                        if (coincideNombre || coincideCodigo) {
                            filtrados.add(item);
                        }
                    }
                    results.values = filtrados;
                    results.count = filtrados.size();
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.values != null) {
                    listaFiltrada = (List<ProductoMovimientoItem>) results.values;
                } else {
                    listaFiltrada = new ArrayList<>();
                }
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return resultValue instanceof ProductoMovimientoItem
                        ? ((ProductoMovimientoItem) resultValue).getTextoMostrar()
                        : super.convertResultToString(resultValue);
            }
        };
    }
}
