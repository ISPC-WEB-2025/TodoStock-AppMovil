package com.ispc.todostock;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.InventarioApiService;
import com.ispc.todostock.network.StockSucursalDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TK16: stock real de la sucursal elegida, pedido a la API.
 * Responsable: Virginia Garcia
 */
public class GestionStockActivity extends AppCompatActivity {

    private static final int SIN_SUCURSAL = -1;

    private int sucursalIdActual = SIN_SUCURSAL;

    private RecyclerView rvStock;
    private ProgressBar pbStock;
    private View llMensajeStock;
    private TextView tvMensajeStock;
    private MaterialButton btnReintentarStock;
    private StockAdapter adapter;

    private Call<List<StockSucursalDto>> llamadaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_stock);

        Toolbar toolbarStock = findViewById(R.id.toolbarStock);
        toolbarStock.setNavigationOnClickListener(v -> finish());

        TextView tvSucursalFiltro = findViewById(R.id.tvSucursalFiltro);
        rvStock = findViewById(R.id.rvStock);
        pbStock = findViewById(R.id.pbStock);
        llMensajeStock = findViewById(R.id.llMensajeStock);
        tvMensajeStock = findViewById(R.id.tvMensajeStock);
        btnReintentarStock = findViewById(R.id.btnReintentarStock);

        // Sucursal elegida en la pantalla anterior (llega por Intent)
        String sucursalNombre = null;
        if (getIntent() != null) {
            sucursalIdActual = getIntent().getIntExtra(MenuPrincipalActivity.EXTRA_SUCURSAL_ID, SIN_SUCURSAL);
            sucursalNombre = getIntent().getStringExtra("sucursal_nombre");
        }
        if (sucursalNombre == null) {
            sucursalNombre = "Sucursal #" + sucursalIdActual;
        }
        tvSucursalFiltro.setText("Mostrando inventario de: " + sucursalNombre);

        rvStock.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StockAdapter(this, new ArrayList<>());
        rvStock.setAdapter(adapter);

        btnReintentarStock.setOnClickListener(v -> cargarStock());

        if (sucursalIdActual == SIN_SUCURSAL) {
            mostrarMensaje("No se indicó la sucursal. Volvé atrás y elegí una.", false);
        } else {
            cargarStock();
        }
    }

    private void cargarStock() {
        mostrarCargando();

        InventarioApiService api = ApiClient.create(InventarioApiService.class);
        llamadaActual = api.getStockDeSucursal(sucursalIdActual, false);
        llamadaActual.enqueue(new Callback<List<StockSucursalDto>>() {
            @Override
            public void onResponse(Call<List<StockSucursalDto>> call, Response<List<StockSucursalDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<StockItem> items = new ArrayList<>();
                    for (StockSucursalDto dto : response.body()) {
                        if (dto.getCantidad() > 0 && !BajaDeProducto.estaInactivo(dto.getNombreProducto())) {
                            items.add(StockItem.desde(dto));
                        }
                    }
                    if (items.isEmpty()) {
                        mostrarMensaje("Esta sucursal no tiene productos con stock disponible.", false);
                    } else {
                        adapter.actualizarLista(items);
                        mostrarLista();
                    }
                } else if (response.code() == 401) {
                    mostrarMensaje("Tu sesión venció. Volvé a iniciar sesión.", false);
                } else {
                    mostrarMensaje("No se pudo cargar el stock (código " + response.code() + ").", true);
                }
            }

            @Override
            public void onFailure(Call<List<StockSucursalDto>> call, Throwable t) {
                if (call.isCanceled()) {
                    return; // la pantalla se cerró, no hay nada que mostrar
                }
                mostrarMensaje("No hay conexión con el servidor. Revisá tu internet e intentá de nuevo.", true);
            }
        });
    }

    private void mostrarCargando() {
        pbStock.setVisibility(View.VISIBLE);
        llMensajeStock.setVisibility(View.GONE);
        rvStock.setVisibility(View.GONE);
    }

    private void mostrarLista() {
        pbStock.setVisibility(View.GONE);
        llMensajeStock.setVisibility(View.GONE);
        rvStock.setVisibility(View.VISIBLE);
    }

    private void mostrarMensaje(String mensaje, boolean conReintento) {
        pbStock.setVisibility(View.GONE);
        rvStock.setVisibility(View.GONE);
        tvMensajeStock.setText(mensaje);
        btnReintentarStock.setVisibility(conReintento ? View.VISIBLE : View.GONE);
        llMensajeStock.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (llamadaActual != null) {
            llamadaActual.cancel();
        }
    }
}