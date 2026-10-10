package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.InventarioApiService;
import com.ispc.todostock.network.SucursalDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionSucursalActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvSucursales;
    private SucursalAdapter adapter;
    private final List<Sucursal> listaSucursales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_sucursal);

        toolbar = findViewById(R.id.toolbarMenu);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvSucursales = findViewById(R.id.rvSucursales);
        rvSucursales.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adapter con la lista en memoria
        adapter = new SucursalAdapter(listaSucursales, sucursal -> {
            Intent intent = new Intent(SeleccionSucursalActivity.this, GestionStockActivity.class);
            intent.putExtra(MenuPrincipalActivity.EXTRA_SUCURSAL_ID, sucursal.getId());
            intent.putExtra("sucursal_nombre", sucursal.getNombre());
            startActivity(intent);
        });
        rvSucursales.setAdapter(adapter);

        //datos reales desde la API
        cargarSucursalesDesdeApi();
    }

    private void cargarSucursalesDesdeApi() {
        InventarioApiService apiService = ApiClient.create(InventarioApiService.class);
        Call<List<SucursalDto>> call = apiService.getSucursales();

        call.enqueue(new Callback<List<SucursalDto>>() {
            @Override
            public void onResponse(Call<List<SucursalDto>> call, Response<List<SucursalDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaSucursales.clear();
                    for (SucursalDto dto : response.body()) {
                        listaSucursales.add(new Sucursal(dto.getId(), dto.getNombre(), dto.getDireccion()));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SeleccionSucursalActivity.this,
                            "Error al obtener sucursales (código " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SucursalDto>> call, Throwable t) {
                Toast.makeText(SeleccionSucursalActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}