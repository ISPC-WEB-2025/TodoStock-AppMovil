package com.ispc.todostock;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SeleccionSucursalActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvSucursales;
    private SucursalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_sucursal);

        // Toolbar
        toolbar = findViewById(R.id.toolbarMenu);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // RecyclerView
        rvSucursales = findViewById(R.id.rvSucursales);
        rvSucursales.setLayoutManager(new LinearLayoutManager(this));

        // Datos Mock
        List<Sucursal> datosMock = generarSucursalesMock();

        // Adapter
        adapter = new SucursalAdapter(datosMock, sucursal -> {
            Toast.makeText(this, "Seleccionaste: " + sucursal.getNombre(), Toast.LENGTH_SHORT).show();
        });
        rvSucursales.setAdapter(adapter);
    }

    private List<Sucursal> generarSucursalesMock() {
        List<Sucursal> lista = new ArrayList<>();
        lista.add(new Sucursal(1, "Sucursal Centro", "Av. San Martín 450"));
        lista.add(new Sucursal(2, "Sucursal Norte", "Bv. Los Álamos 1280"));
        lista.add(new Sucursal(3, "Sucursal Sur", "Ruta 8 Km 605"));
        lista.add(new Sucursal(4, "Sucursal Este", "Calle Belgrano 890"));
        lista.add(new Sucursal(5, "Sucursal Parque Industrial", "Acceso Norte Nave 12"));
        return lista;
    }
}