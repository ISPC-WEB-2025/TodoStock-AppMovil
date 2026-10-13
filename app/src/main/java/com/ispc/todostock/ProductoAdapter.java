package com.ispc.todostock;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ispc.todostock.network.ProductoDto;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.NumberFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * TK39: listado de productos del catálogo, con búsqueda y acciones por tarjeta.
 * Los productos activos van primero (por nombre) y los inactivos al final.
 * Responsable: Virginia Garcia
 */
public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    /** Lo que puede hacer el usuario con un producto. */
    public interface OnProductoAccionListener {
        void onEditar(ProductoDto producto);

        void onDarDeBaja(ProductoDto producto);

        void onReactivar(ProductoDto producto);
    }

    private final Context context;
    private final OnProductoAccionListener listener;
    private final int[] coloresAvatar;
    private final NumberFormat formatoPrecio =
            NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
    private final Collator collator = Collator.getInstance(new Locale("es", "AR"));

    private final List<ProductoDto> todos = new ArrayList<>();
    private final List<ProductoDto> visibles = new ArrayList<>();
    private String textoBusqueda = "";

    public ProductoAdapter(Context context, OnProductoAccionListener listener) {
        this.context = context;
        this.listener = listener;
        this.collator.setStrength(Collator.PRIMARY); // ignora mayúsculas y acentos al ordenar
        this.coloresAvatar = new int[]{
                ContextCompat.getColor(context, R.color.avatar_producto_1),
                ContextCompat.getColor(context, R.color.avatar_producto_2),
                ContextCompat.getColor(context, R.color.avatar_producto_3),
                ContextCompat.getColor(context, R.color.avatar_producto_4),
                ContextCompat.getColor(context, R.color.avatar_producto_5)
        };
    }


    public void setProductos(List<ProductoDto> productos) {
        todos.clear();
        todos.addAll(productos);
        Collections.sort(todos, (a, b) -> {
            boolean inactivoA = BajaDeProducto.estaInactivo(a.getNombre());
            boolean inactivoB = BajaDeProducto.estaInactivo(b.getNombre());
            if (inactivoA != inactivoB) {
                return inactivoA ? 1 : -1;
            }
            return collator.compare(
                    BajaDeProducto.nombreSinMarca(a.getNombre()),
                    BajaDeProducto.nombreSinMarca(b.getNombre()));
        });
        aplicarFiltro();
        notifyDataSetChanged();
    }


    public void filtrar(String texto) {
        textoBusqueda = normalizar(texto);
        aplicarFiltro();
        notifyDataSetChanged();
    }


    public int getCantidadTotal() {
        return todos.size();
    }

    private void aplicarFiltro() {
        visibles.clear();
        for (ProductoDto producto : todos) {
            if (textoBusqueda.isEmpty() || coincide(producto)) {
                visibles.add(producto);
            }
        }
    }

    private boolean coincide(ProductoDto producto) {
        return normalizar(BajaDeProducto.nombreSinMarca(producto.getNombre())).contains(textoBusqueda)
                || normalizar(producto.getCodigo()).contains(textoBusqueda);
    }


    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT).trim();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        final ProductoDto producto = visibles.get(position);
        boolean inactivo = BajaDeProducto.estaInactivo(producto.getNombre());
        String nombre = BajaDeProducto.nombreSinMarca(producto.getNombre());

        // Tarjeta completa en gris cuando el producto está dado de baja.
        ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context,
                inactivo ? R.color.fondo_producto_inactivo : R.color.gris_fondo_input));

        int colorPrincipal = ContextCompat.getColor(context,
                inactivo ? R.color.texto_producto_inactivo : R.color.color_texto);
        int colorSecundario = ContextCompat.getColor(context,
                inactivo ? R.color.texto_producto_inactivo : R.color.texto_tenue);

        holder.tvInicial.setText(inicialDe(nombre));
        int colorAvatar = inactivo
                ? ContextCompat.getColor(context, R.color.texto_tenue)
                : coloresAvatar[Math.abs(producto.getIdCategoria()) % coloresAvatar.length];
        holder.tvInicial.setBackgroundTintList(ColorStateList.valueOf(colorAvatar));

        holder.tvNombre.setText(nombre);
        holder.tvNombre.setTextColor(colorPrincipal);

        String categoria = producto.getCategoria() != null ? producto.getCategoria().getNombre() : null;
        if (categoria == null || categoria.trim().isEmpty()) {
            holder.tvCategoria.setVisibility(View.GONE);
        } else {
            holder.tvCategoria.setText(categoria);
            holder.tvCategoria.setTextColor(colorSecundario);
            holder.tvCategoria.setVisibility(View.VISIBLE);
        }

        holder.tvCodigo.setText(context.getString(R.string.productos_codigo, producto.getCodigo()));
        holder.tvCodigo.setTextColor(colorSecundario);
        holder.tvPrecio.setText(formatearPrecio(producto.getPrecioVenta()));
        holder.tvPrecio.setTextColor(colorPrincipal);
        holder.tvInactivo.setVisibility(inactivo ? View.VISIBLE : View.GONE);

        holder.btnEditar.setVisibility(inactivo ? View.GONE : View.VISIBLE);
        holder.btnBaja.setVisibility(inactivo ? View.GONE : View.VISIBLE);
        holder.btnReactivar.setVisibility(inactivo ? View.VISIBLE : View.GONE);

        holder.btnEditar.setContentDescription(context.getString(R.string.productos_cd_editar, nombre));
        holder.btnBaja.setContentDescription(context.getString(R.string.productos_cd_baja, nombre));
        holder.btnReactivar.setContentDescription(context.getString(R.string.productos_cd_reactivar, nombre));

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(producto);
            }
        });
        holder.btnBaja.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDarDeBaja(producto);
            }
        });
        holder.btnReactivar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReactivar(producto);
            }
        });
    }

    private String inicialDe(String nombre) {
        return nombre.isEmpty() ? "?" : nombre.substring(0, 1).toUpperCase(Locale.getDefault());
    }

    private String formatearPrecio(String precio) {
        try {
            return formatoPrecio.format(new BigDecimal(precio));
        } catch (RuntimeException e) {
            return precio != null ? "$ " + precio : "";
        }
    }

    @Override
    public int getItemCount() {
        return visibles.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        final TextView tvInicial;
        final TextView tvNombre;
        final TextView tvCategoria;
        final TextView tvCodigo;
        final TextView tvPrecio;
        final TextView tvInactivo;
        final MaterialButton btnEditar;
        final MaterialButton btnBaja;
        final MaterialButton btnReactivar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInicial = itemView.findViewById(R.id.tvProdInicial);
            tvNombre = itemView.findViewById(R.id.tvProdNombre);
            tvCategoria = itemView.findViewById(R.id.tvProdCategoria);
            tvCodigo = itemView.findViewById(R.id.tvProdCodigo);
            tvPrecio = itemView.findViewById(R.id.tvProdPrecio);
            tvInactivo = itemView.findViewById(R.id.tvProdInactivo);
            btnEditar = itemView.findViewById(R.id.btnProdEditar);
            btnBaja = itemView.findViewById(R.id.btnProdBaja);
            btnReactivar = itemView.findViewById(R.id.btnProdReactivar);
        }
    }
}