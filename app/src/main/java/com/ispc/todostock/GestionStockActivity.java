package com.ispc.todostock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

public class GestionStockActivity extends AppCompatActivity {

    private Toolbar toolbarStock;
    private int sucursalIdActual = 1;
    private TextView tvSucursalFiltro;
    private RecyclerView rvStock;
    private StockAdapter adapter;
    private List<StockItem> todosLosProductosMock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_stock);

        // 1. Vinculación del Toolbar
        toolbarStock = findViewById(R.id.toolbarStock);

        // 2. Click en el icono de navegación (flecha_atras)
        toolbarStock.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSucursalFiltro = findViewById(R.id.tvSucursalFiltro);
        rvStock = findViewById(R.id.rvStock);

        // 1. Recibir sucursal_id y nombre por Intent (TK15)
        String sucursalNombre = "Sucursal #" + sucursalIdActual;

        if (getIntent() != null) {
            if (getIntent().hasExtra(MenuPrincipalActivity.EXTRA_SUCURSAL_ID)) {
                sucursalIdActual = getIntent().getIntExtra(MenuPrincipalActivity.EXTRA_SUCURSAL_ID, 1);
                sucursalNombre = "Sucursal #" + sucursalIdActual;
            }
            if (getIntent().hasExtra("sucursal_nombre")) {
                sucursalNombre = getIntent().getStringExtra("sucursal_nombre");
            }
        }

        tvSucursalFiltro.setText("Mostrando inventario de: " + sucursalNombre);

        // 2. Configurar RecyclerView con LayoutManager
        rvStock.setLayoutManager(new LinearLayoutManager(this));

        // 3. Inicializar datos mock
        poblarDatosMockGlobales();

        // 4. Filtrar según la sucursal activa
        List<StockItem> productosFiltrados = filtrarPorSucursal(sucursalIdActual);

        // 5. Asignar Adapter
        adapter = new StockAdapter(this, productosFiltrados);
        rvStock.setAdapter(adapter);
    }

    private void poblarDatosMockGlobales() {
        todosLosProductosMock = new ArrayList<>();

        // Productos para Sucursal 1 (Centro)
        todosLosProductosMock.add(new StockItem(1, 1, "Lama aluminio 43mm BLANCO", "LAM-43-BLA", 25, 8));
        todosLosProductosMock.add(new StockItem(2, 1, "Lama aluminio 43mm NEGRO", "LAM-43-NEG", 4, 8));    // ¡Bajo stock!
        todosLosProductosMock.add(new StockItem(3, 1, "Lama aluminio 43mm PLATA", "LAM-43-PLA", 15, 6));
        todosLosProductosMock.add(new StockItem(4, 1, "Lama aluminio 43mm BRONCE", "LAM-43-BRO", 10, 5));

        todosLosProductosMock.add(new StockItem(5, 1, "Guía embutir recambio BLANCO", "GUI-EMB-BLA", 30, 10));
        todosLosProductosMock.add(new StockItem(6, 1, "Guía embutir recambio NEGRO", "GUI-EMB-NEG", 6, 10));   // ¡Bajo stock!
        todosLosProductosMock.add(new StockItem(7, 1, "Guía embutir recambio PLATA", "GUI-EMB-PLA", 18, 8));
        todosLosProductosMock.add(new StockItem(8, 1, "Guía embutir recambio BRONCE", "GUI-EMB-BRO", 12, 6));

        todosLosProductosMock.add(new StockItem(9, 1, "Guía mosquitero BLANCO", "GUI-MOS-BLA", 22, 8));
        todosLosProductosMock.add(new StockItem(10, 1, "Guía mosquitero NEGRO", "GUI-MOS-NEG", 5, 8));         // ¡Bajo stock!
        todosLosProductosMock.add(new StockItem(11, 1, "Guía mosquitero PLATA", "GUI-MOS-PLA", 16, 6));
        todosLosProductosMock.add(new StockItem(12, 1, "Guía mosquitero BRONCE", "GUI-MOS-BRO", 11, 5));

        todosLosProductosMock.add(new StockItem(13, 1, "Cabezal mosquitero BLANCO", "CAB-MOS-BLA", 20, 5));
        todosLosProductosMock.add(new StockItem(14, 1, "Cabezal mosquitero NEGRO", "CAB-MOS-NEG", 3, 5));      // ¡Bajo stock!
        todosLosProductosMock.add(new StockItem(15, 1, "Cabezal mosquitero PLATA", "CAB-MOS-PLA", 14, 5));
        todosLosProductosMock.add(new StockItem(16, 1, "Cabezal mosquitero BRONCE", "CAB-MOS-BRO", 9, 4));

// Productos para Sucursal 2 (Nueva Córdoba)
        todosLosProductosMock.add(new StockItem(17, 2, "Lama aluminio 43mm BLANCO", "LAM-43-BLA", 5, 8));      // ¡Bajo stock!
        todosLosProductosMock.add(new StockItem(18, 2, "Lama aluminio 43mm NEGRO", "LAM-43-NEG", 22, 8));
        todosLosProductosMock.add(new StockItem(19, 2, "Guía embutir recambio PLATA", "GUI-EMB-PLA", 7, 8));   // ¡Bajo stock!
        todosLosProductosMock.add(new StockItem(20, 2, "Guía mosquitero BLANCO", "GUI-MOS-BLA", 9, 8));        // ¡Bajo stock!
        todosLosProductosMock.add(new StockItem(21, 2, "Cabezal mosquitero BRONCE", "CAB-MOS-BRO", 16, 4));
    }

    private List<StockItem> filtrarPorSucursal(int idSucursal) {
        List<StockItem> filtrados = new ArrayList<>();
        for (StockItem item : todosLosProductosMock) {
            if (item.getSucursalId() == idSucursal) {
                filtrados.add(item);
            }
        }
        return filtrados;
    }

    }

