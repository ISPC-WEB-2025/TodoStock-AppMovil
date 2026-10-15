package com.ispc.todostock;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.CategoriaDto;
import com.ispc.todostock.network.InventarioApiService;
import com.ispc.todostock.network.ProductoDto;
import com.ispc.todostock.network.ProductoRequest;
import com.ispc.todostock.sesion.SesionManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Alta (TK23) y edición (TK39) de un producto contra la API. Solo Administrador.
 * Si el Intent trae EXTRA_ID_PRODUCTO, la pantalla funciona en modo edición.
 * Responsable: Virginia Garcia
 */
public class CargaProductoActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PRODUCTO = "id_producto";

    private EditText etNombreProducto, etDescripcion, etPrecio, etCodigo;
    private TextInputLayout tilNombreProducto, tilDescripcion, tilPrecio, tilCodigo, tilCategoria;
    private AutoCompleteTextView actvCategoria;
    private Button btnGuardar;

    private ArrayAdapter<CategoriaDto> adaptadorCategorias;
    private CategoriaDto categoriaElegida;
    private boolean categoriasCargadas = false;
    private boolean cargandoCategorias = false;

    private static final String STATE_CATEGORIA_ID = "categoria_id";
    private static final String STATE_CAMPOS_RELLENADOS = "campos_rellenados";
    private int categoriaIdPendiente = -1;

    // Modo edición (TK39)
    private int idProductoEdicion = -1;
    private boolean modoEdicion = false;
    private boolean camposRellenados = false;

    private Call<List<CategoriaDto>> llamadaCategorias;
    private Call<ProductoDto> llamadaAlta;
    private Call<ProductoDto> llamadaProducto;
    private Call<ProductoDto> llamadaEdicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TK48: defensa en profundidad — si un Empleado llega aquí por alguna vía directa
        // (sin pasar por el menú), la pantalla se cierra antes de mostrarse.
        // El menú ya oculta este acceso (aplicarVisibilidadPorRol en MenuPrincipalActivity),
        // pero esta capa adicional previene escalada de privilegios.
        if (!SesionManager.getInstance(this).esAdmin()) {
            Toast.makeText(this, getString(R.string.menu_error_acceso_denegado), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_carga_producto);

        if (savedInstanceState != null) {
            categoriaIdPendiente = savedInstanceState.getInt(STATE_CATEGORIA_ID, -1);
            camposRellenados = savedInstanceState.getBoolean(STATE_CAMPOS_RELLENADOS, false);
        }

        idProductoEdicion = getIntent().getIntExtra(EXTRA_ID_PRODUCTO, -1);
        modoEdicion = idProductoEdicion != -1;

        Toolbar toolbar = findViewById(R.id.toolbarMenu);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
            if (modoEdicion) {
                toolbar.setTitle(R.string.editar_producto_titulo);
            }
        }

        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etCodigo = findViewById(R.id.etCodigo);

        tilNombreProducto = findViewById(R.id.tilNombreProducto);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        tilPrecio = findViewById(R.id.tilPrecio);
        tilCodigo = findViewById(R.id.tilCodigo);
        tilCategoria = findViewById(R.id.tilCategoria);
        actvCategoria = findViewById(R.id.actvCategoria);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> validarYGuardar());

        if (modoEdicion) {
            TextView tvDescripcionPantalla = findViewById(R.id.tvDescripcionPantalla);
            tvDescripcionPantalla.setText(R.string.editar_producto_descripcion);
            btnGuardar.setText(R.string.editar_producto_boton_guardar);
            btnGuardar.setEnabled(false); // se habilita cuando llegan los datos del producto
        }

        configurarDesplegableCategorias();
        cargarCategorias();

        if (modoEdicion) {
            cargarProductoAEditar();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int idGuardar = categoriaElegida != null ? categoriaElegida.getIdCategoria() : categoriaIdPendiente;
        outState.putInt(STATE_CATEGORIA_ID, idGuardar);
        outState.putBoolean(STATE_CAMPOS_RELLENADOS, camposRellenados);
    }

    // ---------- Categorías ----------

    private void configurarDesplegableCategorias() {
        adaptadorCategorias = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<CategoriaDto>());
        actvCategoria.setAdapter(adaptadorCategorias);

        actvCategoria.setOnItemClickListener((parent, view, position, id) -> {
            categoriaElegida = (CategoriaDto) parent.getItemAtPosition(position);
            tilCategoria.setError(null);
        });

        // Si las categorías no se pudieron cargar, tocar el campo vuelve a pedirlas.
        actvCategoria.setOnClickListener(v -> {
            if (!categoriasCargadas) {
                cargarCategorias();
            }
        });
    }

    private void cargarCategorias() {
        if (cargandoCategorias) {
            return;
        }
        cargandoCategorias = true;

        InventarioApiService api = ApiClient.create(InventarioApiService.class);
        llamadaCategorias = api.getCategorias();
        llamadaCategorias.enqueue(new Callback<List<CategoriaDto>>() {
            @Override
            public void onResponse(Call<List<CategoriaDto>> call, Response<List<CategoriaDto>> response) {
                cargandoCategorias = false;
                if (response.isSuccessful() && response.body() != null) {
                    adaptadorCategorias.clear();
                    adaptadorCategorias.addAll(response.body());
                    categoriasCargadas = true;
                    tilCategoria.setError(null);
                    aplicarCategoriaPendiente();
                } else {
                    tilCategoria.setError(getString(R.string.carga_producto_error_categorias));
                }
            }

            @Override
            public void onFailure(Call<List<CategoriaDto>> call, Throwable t) {
                cargandoCategorias = false;
                if (call.isCanceled()) {
                    return; // la pantalla se cerró
                }
                tilCategoria.setError(getString(R.string.carga_producto_error_categorias));
            }
        });
    }

    private void aplicarCategoriaPendiente() {
        if (!categoriasCargadas || categoriaIdPendiente == -1) {
            return;
        }
        for (int i = 0; i < adaptadorCategorias.getCount(); i++) {
            CategoriaDto cat = adaptadorCategorias.getItem(i);
            if (cat != null && cat.getIdCategoria() == categoriaIdPendiente) {
                categoriaElegida = cat;
                actvCategoria.setText(cat.toString(), false);
                break;
            }
        }
        categoriaIdPendiente = -1;
    }

    // ---------- Modo edición: traer el producto ----------

    private void cargarProductoAEditar() {
        InventarioApiService api = ApiClient.create(InventarioApiService.class);
        llamadaProducto = api.getProducto(idProductoEdicion);
        llamadaProducto.enqueue(new Callback<ProductoDto>() {
            @Override
            public void onResponse(Call<ProductoDto> call, Response<ProductoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rellenarFormulario(response.body());
                } else {
                    cerrarPorErrorDeCarga();
                }
            }

            @Override
            public void onFailure(Call<ProductoDto> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                cerrarPorErrorDeCarga();
            }
        });
    }

    private void rellenarFormulario(ProductoDto producto) {
        if (!camposRellenados) {
            etNombreProducto.setText(producto.getNombre());
            etDescripcion.setText(producto.getDescripcion());
            etPrecio.setText(producto.getPrecioVenta());
            etCodigo.setText(producto.getCodigo());
            categoriaIdPendiente = producto.getIdCategoria();
            camposRellenados = true;
        }
        aplicarCategoriaPendiente();
        btnGuardar.setEnabled(true);
    }

    private void cerrarPorErrorDeCarga() {
        Toast.makeText(this, R.string.editar_producto_error_carga, Toast.LENGTH_LONG).show();
        finish();
    }

    // ---------- Validación y envío ----------

    private void validarYGuardar() {
        limpiarErrores();

        String nombre = etNombreProducto.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();

        boolean todoValido = true;

        if (nombre.isEmpty()) {
            tilNombreProducto.setError(getString(R.string.carga_producto_error_nombre));
            todoValido = false;
        }
        if (descripcion.isEmpty()) {
            tilDescripcion.setError(getString(R.string.carga_producto_error_descripcion));
            todoValido = false;
        }
        if (precio.isEmpty()) {
            tilPrecio.setError(getString(R.string.carga_producto_error_precio));
            todoValido = false;
        } else if (!esPrecioValido(precio)) {
            tilPrecio.setError(getString(R.string.carga_producto_error_precio_invalido));
            todoValido = false;
        }
        if (codigo.isEmpty()) {
            tilCodigo.setError(getString(R.string.carga_producto_error_codigo));
            todoValido = false;
        }
        if (categoriaElegida == null) {
            tilCategoria.setError(getString(R.string.carga_producto_error_categoria));
            todoValido = false;
        }

        if (!todoValido) {
            return;
        }

        if (modoEdicion) {
            enviarEdicion(nombre, descripcion, precio, codigo);
        } else {
            enviarAlta(nombre, descripcion, precio, codigo);
        }
    }

    private boolean esPrecioValido(String precio) {
        try {
            return new BigDecimal(precio).signum() >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void enviarAlta(String nombre, String descripcion, String precio, String codigo) {
        btnGuardar.setEnabled(false);

        ProductoRequest datos = ProductoRequest.nuevo(
                nombre, descripcion, codigo, precio, categoriaElegida.getIdCategoria());

        InventarioApiService api = ApiClient.create(InventarioApiService.class);
        llamadaAlta = api.crearProducto(datos);
        llamadaAlta.enqueue(new Callback<ProductoDto>() {
            @Override
            public void onResponse(Call<ProductoDto> call, Response<ProductoDto> response) {
                btnGuardar.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(CargaProductoActivity.this,
                            R.string.carga_producto_guardado, Toast.LENGTH_SHORT).show();
                    limpiarFormulario();
                } else {
                    manejarError(response.code(), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ProductoDto> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                btnGuardar.setEnabled(true);
                Toast.makeText(CargaProductoActivity.this,
                        R.string.carga_producto_error_red, Toast.LENGTH_LONG).show();
            }
        });
    }

    /** TK39: guarda los cambios del producto con PATCH y vuelve al listado. */
    private void enviarEdicion(String nombre, String descripcion, String precio, String codigo) {
        btnGuardar.setEnabled(false);

        ProductoRequest datos = ProductoRequest.edicion(
                nombre, descripcion, codigo, precio, categoriaElegida.getIdCategoria());

        InventarioApiService api = ApiClient.create(InventarioApiService.class);
        llamadaEdicion = api.actualizarProducto(idProductoEdicion, datos);
        llamadaEdicion.enqueue(new Callback<ProductoDto>() {
            @Override
            public void onResponse(Call<ProductoDto> call, Response<ProductoDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CargaProductoActivity.this,
                            R.string.editar_producto_guardado, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    btnGuardar.setEnabled(true);
                    manejarError(response.code(), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ProductoDto> call, Throwable t) {
                if (call.isCanceled()) {
                    return; // la pantalla se cerró
                }
                btnGuardar.setEnabled(true);
                Toast.makeText(CargaProductoActivity.this,
                        R.string.carga_producto_error_red, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------- Errores del servidor ----------

    private void manejarError(int codigoHttp, ResponseBody cuerpo) {
        if (codigoHttp == 400) {
            // Datos inválidos (por ejemplo, código duplicado): se muestra debajo de cada campo.
            if (!mostrarErroresDeCampos(cuerpo)) {
                mostrarErrorGenerico(codigoHttp);
            }
        } else if (codigoHttp == 401) {
            Toast.makeText(this, R.string.carga_producto_error_sesion, Toast.LENGTH_LONG).show();
        } else if (codigoHttp == 403) {
            Toast.makeText(this, R.string.carga_producto_error_permiso, Toast.LENGTH_LONG).show();
        } else {
            mostrarErrorGenerico(codigoHttp);
        }
    }

    private void mostrarErrorGenerico(int codigoHttp) {
        Toast.makeText(this,
                getString(R.string.carga_producto_error_generico, codigoHttp),
                Toast.LENGTH_LONG).show();
    }

    private boolean mostrarErroresDeCampos(ResponseBody cuerpo) {
        if (cuerpo == null) {
            return false;
        }
        try {
            JsonObject raiz = new Gson().fromJson(cuerpo.string(), JsonObject.class);
            if (raiz == null) {
                return false;
            }
            JsonObject campos = (raiz.has("detalle") && raiz.get("detalle").isJsonObject())
                    ? raiz.getAsJsonObject("detalle")
                    : raiz;

            boolean alguno = false;
            alguno |= mostrarErrorDeCampo(tilNombreProducto, campos, "nombre");
            alguno |= mostrarErrorDeCampo(tilDescripcion, campos, "descripcion");
            alguno |= mostrarErrorDeCampo(tilPrecio, campos, "precio_venta");
            alguno |= mostrarErrorDeCampo(tilCodigo, campos, "codigo");
            alguno |= mostrarErrorDeCampo(tilCategoria, campos, "id_cat");
            return alguno;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    private boolean mostrarErrorDeCampo(TextInputLayout campoVisual, JsonObject campos, String clave) {
        JsonElement valor = campos.get(clave);
        if (valor == null) {
            return false;
        }
        String mensaje = null;
        if (valor.isJsonArray() && valor.getAsJsonArray().size() > 0) {
            JsonElement primero = valor.getAsJsonArray().get(0);
            if (primero.isJsonPrimitive()) {
                mensaje = primero.getAsString();
            }
        } else if (valor.isJsonPrimitive()) {
            mensaje = valor.getAsString();
        }
        if (mensaje == null) {
            return false;
        }
        campoVisual.setError(mensaje);
        return true;
    }

    // ---------- Limpieza ----------

    private void limpiarFormulario() {
        etNombreProducto.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
        etCodigo.setText("");
        actvCategoria.setText("", false);
        categoriaElegida = null;
        limpiarErrores();
        etNombreProducto.requestFocus();
    }

    private void limpiarErrores() {
        tilNombreProducto.setError(null);
        tilDescripcion.setError(null);
        tilPrecio.setError(null);
        tilCodigo.setError(null);
        tilCategoria.setError(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (llamadaCategorias != null) {
            llamadaCategorias.cancel();
        }
        if (llamadaAlta != null) {
            llamadaAlta.cancel();
        }
        if (llamadaProducto != null) {
            llamadaProducto.cancel();
        }
        if (llamadaEdicion != null) {
            llamadaEdicion.cancel();
        }
    }
}