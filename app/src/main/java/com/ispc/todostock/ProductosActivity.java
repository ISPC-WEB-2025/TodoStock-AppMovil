package com.ispc.todostock;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.InventarioApiService;
import com.ispc.todostock.network.ProductoDto;
import com.ispc.todostock.network.ProductoRequest;
import com.ispc.todostock.sesion.SesionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TK39: listado de productos con búsqueda, edición y baja. Solo Administrador.
 * La baja es lógica: el backend no permite borrar productos con stock, así que el producto
 * se marca con el prefijo "[INACTIVO] " en el nombre (ver BajaDeProducto).
 * Responsable: Virginia Garcia
 */
public class ProductosActivity extends AppCompatActivity {

    private TextInputEditText etBuscar;
    private RecyclerView rvProductos;
    private ProgressBar pbProductos;
    private View llMensajeProductos;
    private TextView tvMensajeProductos;
    private MaterialButton btnReintentarProductos;
    private ProductoAdapter adapter;

    private boolean listaCargada = false;
    private Call<List<ProductoDto>> llamadaActual;

    // Baja / reactivación
    private Call<ProductoDto> llamadaAccion;
    private boolean accionEnCurso = false;
    private AlertDialog dialogoBaja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TK48: defensa en profundidad, igual que en CargaProductoActivity.
        if (!SesionManager.getInstance(this).esAdmin()) {
            Toast.makeText(this, getString(R.string.menu_error_acceso_denegado), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_productos);

        Toolbar toolbar = findViewById(R.id.toolbarProductos);
        toolbar.setNavigationOnClickListener(v -> finish());

        etBuscar = findViewById(R.id.etBuscar);
        rvProductos = findViewById(R.id.rvProductos);
        pbProductos = findViewById(R.id.pbProductos);
        llMensajeProductos = findViewById(R.id.llMensajeProductos);
        tvMensajeProductos = findViewById(R.id.tvMensajeProductos);
        btnReintentarProductos = findViewById(R.id.btnReintentarProductos);

        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(this, new ProductoAdapter.OnProductoAccionListener() {
            @Override
            public void onEditar(ProductoDto producto) {
                android.content.Intent intent =
                        new android.content.Intent(ProductosActivity.this, CargaProductoActivity.class);
                intent.putExtra(CargaProductoActivity.EXTRA_ID_PRODUCTO, producto.getIdProducto());
                startActivity(intent);
            }

            @Override
            public void onDarDeBaja(ProductoDto producto) {
                confirmarBaja(producto);
            }

            @Override
            public void onReactivar(ProductoDto producto) {
                confirmarReactivacion(producto);
            }
        });
        rvProductos.setAdapter(adapter);

        btnReintentarProductos.setOnClickListener(v -> cargarProductos());

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filtrar(s.toString());
                if (listaCargada) {
                    mostrarSegunEstado();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            cargarProductos();
        }
    }

    private void cargarProductos() {
        mostrarCargando();

        if (llamadaActual != null) {
            llamadaActual.cancel();
        }
        InventarioApiService api = ApiClient.create(InventarioApiService.class);
        llamadaActual = api.getProductos();
        llamadaActual.enqueue(new Callback<List<ProductoDto>>() {
            @Override
            public void onResponse(Call<List<ProductoDto>> call, Response<List<ProductoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setProductos(response.body());
                    listaCargada = true;
                    mostrarSegunEstado();
                } else if (response.code() == 401) {
                    mostrarMensaje(getString(R.string.productos_error_sesion), false);
                } else if (response.code() == 403) {
                    mostrarMensaje(getString(R.string.productos_error_permiso), false);
                } else {
                    mostrarMensaje(getString(R.string.productos_error_generico, response.code()), true);
                }
            }

            @Override
            public void onFailure(Call<List<ProductoDto>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                mostrarMensaje(getString(R.string.productos_error_red), true);
            }
        });
    }

    // ---------- Baja y reactivación ----------

    private void confirmarBaja(ProductoDto producto) {
        if (accionEnCurso) {
            return;
        }
        dialogoBaja = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.productos_baja_titulo)
                .setMessage(getString(R.string.productos_baja_mensaje, producto.getNombre()))
                .setNegativeButton(R.string.productos_baja_cancelar, null)
                .setPositiveButton(R.string.productos_baja_confirmar, (dialogo, cual) ->
                        cambiarNombre(producto, BajaDeProducto.marcarInactivo(producto.getNombre()),
                                R.string.productos_baja_ok))
                .show();
    }
    private void confirmarReactivacion(ProductoDto producto) {
        if (accionEnCurso) {
            return;
        }
        String nombreSinMarca = BajaDeProducto.nombreSinMarca(producto.getNombre());
        dialogoBaja = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.productos_reactivar_titulo)
                .setMessage(getString(R.string.productos_reactivar_mensaje, nombreSinMarca))
                .setNegativeButton(R.string.productos_baja_cancelar, null)
                .setPositiveButton(R.string.productos_reactivar_confirmar, (dialogo, cual) ->
                        cambiarNombre(producto, BajaDeProducto.reactivar(producto.getNombre()),
                                R.string.productos_reactivar_ok))
                .show();
    }

    private void cambiarNombre(ProductoDto producto, String nuevoNombre, int mensajeOk) {
        if (accionEnCurso) {
            return;
        }
        accionEnCurso = true;

        InventarioApiService api = ApiClient.create(InventarioApiService.class);
        llamadaAccion = api.actualizarProducto(producto.getIdProducto(),
                ProductoRequest.soloNombre(nuevoNombre));
        llamadaAccion.enqueue(new Callback<ProductoDto>() {
            @Override
            public void onResponse(Call<ProductoDto> call, Response<ProductoDto> response) {
                accionEnCurso = false;
                if (response.isSuccessful()) {
                    Toast.makeText(ProductosActivity.this, mensajeOk, Toast.LENGTH_SHORT).show();
                    cargarProductos(); // el producto se reubica solo en la lista
                } else if (response.code() == 401) {
                    Toast.makeText(ProductosActivity.this,
                            R.string.productos_error_sesion, Toast.LENGTH_LONG).show();
                } else if (response.code() == 403) {
                    Toast.makeText(ProductosActivity.this,
                            R.string.productos_error_permiso, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProductosActivity.this,
                            getString(R.string.productos_accion_error_generico, response.code()),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProductoDto> call, Throwable t) {
                if (call.isCanceled()) {
                    return; // la pantalla se cerró
                }
                accionEnCurso = false;
                Toast.makeText(ProductosActivity.this,
                        R.string.productos_accion_error_red, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------- Estados de la pantalla ----------

    private void mostrarSegunEstado() {
        if (adapter.getItemCount() > 0) {
            mostrarLista();
        } else if (adapter.getCantidadTotal() == 0) {
            mostrarMensaje(getString(R.string.productos_vacio), false);
        } else {
            mostrarMensaje(getString(R.string.productos_sin_resultados), false);
        }
    }

    private void mostrarCargando() {
        pbProductos.setVisibility(View.VISIBLE);
        llMensajeProductos.setVisibility(View.GONE);
        rvProductos.setVisibility(View.GONE);
    }

    private void mostrarLista() {
        pbProductos.setVisibility(View.GONE);
        llMensajeProductos.setVisibility(View.GONE);
        rvProductos.setVisibility(View.VISIBLE);
    }

    private void mostrarMensaje(String mensaje, boolean conReintento) {
        pbProductos.setVisibility(View.GONE);
        rvProductos.setVisibility(View.GONE);
        tvMensajeProductos.setText(mensaje);
        btnReintentarProductos.setVisibility(conReintento ? View.VISIBLE : View.GONE);
        llMensajeProductos.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogoBaja != null && dialogoBaja.isShowing()) {
            dialogoBaja.dismiss();
        }
        if (llamadaActual != null) {
            llamadaActual.cancel();
        }
        if (llamadaAccion != null) {
            llamadaAccion.cancel();
        }
    }
}