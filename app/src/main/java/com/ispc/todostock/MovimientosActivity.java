package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.InventarioApiService;
import com.ispc.todostock.network.MovimientoRequest;
import com.ispc.todostock.network.ProductoDto;
import com.ispc.todostock.network.StockSucursalDto;
import com.ispc.todostock.network.SucursalDto;
import com.ispc.todostock.sesion.SesionManager;
import com.ispc.todostock.validacion.Validadores;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TK26 / TK27 - Traslado de stock entre sucursales con datos reales.
 *
 * 1. Carga las sucursales desde la API.
 * 2. Al elegir el origen, carga los productos con stock en esa sucursal.
 * 3. Valida y envía el traslado a POST /api/inventario/movimientos/.
 *    El backend descuenta en origen y suma en destino en una sola transacción.
 *
 * En cada spinner, la posición 0 es el texto guía ("Seleccione ..."): el elemento
 * real de la posición p está en la lista, en el índice p - 1.
 *
 * TK79: los errores de validación se muestran debajo de cada campo (texto rojo fijo y
 * borde rojo), en lugar de un Toast que desaparecía y no indicaba qué campo estaba mal.
 * Cada error se borra cuando se modifica su campo.
 */
public class MovimientosActivity extends AppCompatActivity {

    public static final String EXTRA_TIPO_OPERACION = "extra_tipo_operacion";
    public static final String OPERACION_TRASLADO = "TRASLADO";
    public static final String OPERACION_VENTA = "VENTA";
    public static final String OPERACION_MERMA = "MERMA";
    public static final String OPERACION_INGRESO = "INGRESO";

    private Spinner spinnerOrigen, spinnerDestino;
    private TextInputLayout tilProducto;
    private AutoCompleteTextView actvProducto;
    private ProductoMovimientoAdapter adaptadorProductos;
    private ProductoMovimientoItem productoSeleccionado;

    private EditText etCantidad;
    private AppCompatButton btnConfirmar;
    private CharSequence textoBotonOriginal;

    private FrameLayout campoOrigen, campoDestino;
    private TextView tvEtiquetaOrigen, tvEtiquetaDestino;
    private TextView tvErrorOrigen, tvErrorDestino, tvErrorCantidad;

    private TipoMovimiento tipoMovimiento = TipoMovimiento.TRASLADO;

    private final List<SucursalDto> sucursales = new ArrayList<>();

    private InventarioApiService api;
    private final List<Call<?>> llamadasEnCurso = new ArrayList<>();

    private static final String STATE_ORIGEN_POS = "origen_pos";
    private static final String STATE_DESTINO_POS = "destino_pos";
    private static final String STATE_PRODUCTO_ID = "producto_id";
    private int origenPosPendiente = 0;
    private int destinoPosPendiente = 0;
    private int productoIdPendiente = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TIPO_OPERACION)) {
            tipoMovimiento = TipoMovimiento.desdeString(intent.getStringExtra(EXTRA_TIPO_OPERACION));
        }

        // TK79: la barra superior ahora es una MaterialToolbar; la flecha de volver es su navegación.
        Toolbar toolbar = findViewById(R.id.toolbarMovimientos);
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        tilProducto = findViewById(R.id.tilProducto);
        actvProducto = findViewById(R.id.actvProducto);
        etCantidad = findViewById(R.id.etCantidad);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        textoBotonOriginal = btnConfirmar.getText();

        campoOrigen = findViewById(R.id.campoOrigen);
        campoDestino = findViewById(R.id.campoDestino);
        tvEtiquetaOrigen = findViewById(R.id.tvEtiquetaOrigen);
        tvEtiquetaDestino = findViewById(R.id.tvEtiquetaDestino);
        tvErrorOrigen = findViewById(R.id.tvErrorOrigen);
        tvErrorDestino = findViewById(R.id.tvErrorDestino);
        tvErrorCantidad = findViewById(R.id.tvErrorCantidad);

        configurarModoOperacion(toolbar);
        configurarDesplegableProducto();

        api = ApiClient.create(InventarioApiService.class);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> intentarConfirmarMovimiento());

        // Al elegir el origen, se cargan los productos con stock en esa sucursal.
        spinnerOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int posicion, long id) {
                if (posicion > 0) {
                    ocultarError(campoOrigen, tvErrorOrigen);
                    recargarProductos(sucursales.get(posicion - 1));
                } else {
                    mostrarProductos(new ArrayList<>(), getString(R.string.mov_elija_origen));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hace falta hacer nada.
            }
        });

        // TK79: al cambiar el destino o el producto, se borra su error.
        spinnerDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int posicion, long id) {
                if (posicion > 0) {
                    ocultarError(campoDestino, tvErrorDestino);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hace falta hacer nada.
            }
        });

        // TK79: al escribir en la cantidad, se borra su error.
        etCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No hace falta hacer nada.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ocultarError(etCantidad, tvErrorCantidad);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No hace falta hacer nada.
            }
        });

        if (savedInstanceState != null) {
            origenPosPendiente = savedInstanceState.getInt(STATE_ORIGEN_POS, 0);
            destinoPosPendiente = savedInstanceState.getInt(STATE_DESTINO_POS, 0);
            productoIdPendiente = savedInstanceState.getInt(STATE_PRODUCTO_ID, -1);
        }

        cargarSucursales();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int origenPos = spinnerOrigen.getSelectedItemPosition();
        int destinoPos = spinnerDestino.getSelectedItemPosition();
        int idProd = (productoSeleccionado != null) ? productoSeleccionado.getIdProducto() : productoIdPendiente;
        outState.putInt(STATE_ORIGEN_POS, origenPos > 0 ? origenPos : origenPosPendiente);
        outState.putInt(STATE_DESTINO_POS, destinoPos > 0 ? destinoPos : destinoPosPendiente);
        outState.putInt(STATE_PRODUCTO_ID, idProd);
    }

    @Override
    protected void onDestroy() {
        // Si se cierra la pantalla con pedidos en curso, se cancelan.
        for (Call<?> llamada : llamadasEnCurso) {
            llamada.cancel();
        }
        super.onDestroy();
    }

    // ---------- Carga de datos ----------

    private void cargarSucursales() {
        mostrarSucursales(new ArrayList<>(), getString(R.string.mov_cargando));
        mostrarProductos(new ArrayList<>(), getString(R.string.mov_elija_origen));

        Call<List<SucursalDto>> llamada = api.getSucursales();
        llamadasEnCurso.add(llamada);
        llamada.enqueue(new Callback<List<SucursalDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<SucursalDto>> call,
                                   @NonNull Response<List<SucursalDto>> respuesta) {
                llamadasEnCurso.remove(call);
                if (isFinishing() || isDestroyed()) return;

                if (respuesta.isSuccessful() && respuesta.body() != null) {
                    mostrarSucursales(respuesta.body(), getString(R.string.mov_seleccione_sucursal));
                } else if (respuesta.code() == 401) {
                    mostrarSesionVencida();
                } else {
                    mostrarErrorDeCarga(getString(R.string.mov_error_carga));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SucursalDto>> call, @NonNull Throwable error) {
                llamadasEnCurso.remove(call);
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarErrorDeCarga(getString(R.string.mov_error_conexion));
            }
        });
    }

    private void cargarProductos(int idSucursal) {
        mostrarProductos(new ArrayList<>(), getString(R.string.mov_cargando));

        Call<List<StockSucursalDto>> llamada = api.getStockDeSucursal(idSucursal, true);
        llamadasEnCurso.add(llamada);
        llamada.enqueue(new Callback<List<StockSucursalDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<StockSucursalDto>> call,
                                   @NonNull Response<List<StockSucursalDto>> respuesta) {
                llamadasEnCurso.remove(call);
                if (isFinishing() || isDestroyed()) return;

                if (respuesta.isSuccessful() && respuesta.body() != null) {
                    List<StockSucursalDto> productos = respuesta.body();
                    String guia = productos.isEmpty()
                            ? getString(R.string.mov_sin_stock)
                            : getString(R.string.mov_seleccione_producto);
                    mostrarProductos(productos, guia);
                } else if (respuesta.code() == 401) {
                    mostrarSesionVencida();
                } else {
                    mostrarProductos(new ArrayList<>(), getString(R.string.mov_elija_origen));
                    // TK79: diálogo en lugar de Toast (el Toast desaparecía).
                    mostrarError(getString(R.string.mov_error_carga));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StockSucursalDto>> call, @NonNull Throwable error) {
                llamadasEnCurso.remove(call);
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarProductos(new ArrayList<>(), getString(R.string.mov_elija_origen));
                // TK79: diálogo en lugar de Toast (el Toast desaparecía).
                mostrarError(getString(R.string.mov_error_conexion));
            }
        });
    }

    private void mostrarSucursales(List<SucursalDto> lista, String guia) {
        sucursales.clear();

        List<SucursalDto> listaAMostrar = new ArrayList<>();
        if (tipoMovimiento.soloCasaCentral) {
            for (SucursalDto sucursal : lista) {
                if (sucursal.isEsCentral()) {
                    listaAMostrar.add(sucursal);
                }
            }
        } else {
            listaAMostrar.addAll(lista);
        }

        sucursales.addAll(listaAMostrar);

        List<String> textos = new ArrayList<>();
        textos.add(guia);
        int posCentral = -1;
        for (int i = 0; i < listaAMostrar.size(); i++) {
            SucursalDto sucursal = listaAMostrar.get(i);
            if (sucursal.isEsCentral()) {
                textos.add(sucursal.getNombre() + " (Casa Central)");
                posCentral = i;
            } else {
                textos.add(sucursal.getNombre());
            }
        }
        spinnerOrigen.setAdapter(crearAdapter(textos));
        spinnerDestino.setAdapter(crearAdapter(textos));
        if (!listaAMostrar.isEmpty()) {
            if (origenPosPendiente > 0 && origenPosPendiente <= listaAMostrar.size()) {
                spinnerOrigen.setSelection(origenPosPendiente);
            } else if (tipoMovimiento.soloCasaCentral && posCentral >= 0) {
                spinnerOrigen.setSelection(posCentral + 1);
            }
            if (destinoPosPendiente > 0 && destinoPosPendiente <= listaAMostrar.size()) {
                spinnerDestino.setSelection(destinoPosPendiente);
            }
            origenPosPendiente = 0;
            destinoPosPendiente = 0;
        }
    }

    private void mostrarListaProductos(List<ProductoMovimientoItem> items, String guia) {
        productoSeleccionado = null;
        actvProducto.setText("", false);

        adaptadorProductos = new ProductoMovimientoAdapter(this, items);
        actvProducto.setAdapter(adaptadorProductos);

        if (items.isEmpty()) {
            tilProducto.setHelperText(guia);
        } else {
            tilProducto.setHelperText(null);
            if (productoIdPendiente > 0) {
                for (ProductoMovimientoItem item : items) {
                    if (item.getIdProducto() == productoIdPendiente) {
                        productoSeleccionado = item;
                        actvProducto.setText(item.getTextoMostrar(), false);
                        actvProducto.setSelection(0);
                        break;
                    }
                }
                productoIdPendiente = -1;
            }
        }
    }

    private void mostrarProductos(List<StockSucursalDto> lista, String guia) {
        List<ProductoMovimientoItem> items = new ArrayList<>(lista.size());
        for (StockSucursalDto producto : lista) {
            items.add(new ProductoMovimientoItem(producto));
        }
        mostrarListaProductos(items, guia);
    }

    private void cargarCatalogoProductos() {
        mostrarProductosCatalogo(new ArrayList<>(), getString(R.string.mov_cargando));

        Call<List<ProductoDto>> llamada = api.getProductos();
        llamadasEnCurso.add(llamada);
        llamada.enqueue(new Callback<List<ProductoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductoDto>> call,
                                   @NonNull Response<List<ProductoDto>> respuesta) {
                llamadasEnCurso.remove(call);
                if (isFinishing() || isDestroyed()) return;

                if (respuesta.isSuccessful() && respuesta.body() != null) {
                    List<ProductoDto> lista = respuesta.body();
                    String guia = lista.isEmpty()
                            ? getString(R.string.mov_sin_stock)
                            : getString(R.string.mov_seleccione_producto);
                    mostrarProductosCatalogo(lista, guia);
                } else if (respuesta.code() == 401) {
                    mostrarSesionVencida();
                } else {
                    mostrarProductosCatalogo(new ArrayList<>(), getString(R.string.mov_seleccione_producto));
                    mostrarError(getString(R.string.mov_error_carga));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductoDto>> call, @NonNull Throwable error) {
                llamadasEnCurso.remove(call);
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarProductosCatalogo(new ArrayList<>(), getString(R.string.mov_seleccione_producto));
                mostrarError(getString(R.string.mov_error_conexion));
            }
        });
    }

    private void mostrarProductosCatalogo(List<ProductoDto> lista, String guia) {
        List<ProductoMovimientoItem> items = new ArrayList<>(lista.size());
        for (ProductoDto producto : lista) {
            items.add(new ProductoMovimientoItem(producto));
        }
        mostrarListaProductos(items, guia);
    }

    private void configurarModoOperacion(Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setTitle(tipoMovimiento.tituloRes);
        }
        btnConfirmar.setText(tipoMovimiento.textoBotonRes);
        textoBotonOriginal = btnConfirmar.getText();
        tvEtiquetaOrigen.setText(tipoMovimiento.etiquetaOrigenRes);

        int visibilidadDestino = tipoMovimiento.muestraDestino ? View.VISIBLE : View.GONE;
        tvEtiquetaDestino.setVisibility(visibilidadDestino);
        campoDestino.setVisibility(visibilidadDestino);
        if (!tipoMovimiento.muestraDestino) {
            tvErrorDestino.setVisibility(View.GONE);
        }
    }

    // ---------- Envío del movimiento ----------

    /**
     * Valida el formulario y, si está bien, envía el movimiento correspondiente.
     */
    private void intentarConfirmarMovimiento() {
        ocultarTodosLosErrores();

        int origenPos = spinnerOrigen.getSelectedItemPosition();
        int destinoPos = spinnerDestino.getSelectedItemPosition();
        String cantidadTexto = etCantidad.getText().toString().trim();

        boolean hayErrores = false;

        SucursalDto origen = null;
        if (origenPos <= 0 || origenPos > sucursales.size()) {
            mostrarErrorDeCampo(campoOrigen, tvErrorOrigen, getString(R.string.mov_error_origen));
            hayErrores = true;
        } else {
            origen = sucursales.get(origenPos - 1);
        }

        SucursalDto destino = null;
        if (tipoMovimiento.muestraDestino) {
            if (destinoPos <= 0 || destinoPos > sucursales.size()) {
                mostrarErrorDeCampo(campoDestino, tvErrorDestino, getString(R.string.mov_error_destino));
                hayErrores = true;
            } else {
                destino = sucursales.get(destinoPos - 1);
            }

            if (origen != null && destino != null
                    && !Validadores.origenYDestinoDistintos(origen.getId(), destino.getId())) {
                mostrarErrorDeCampo(campoDestino, tvErrorDestino, getString(R.string.mov_error_iguales));
                hayErrores = true;
            }
        }

        if (productoSeleccionado == null) {
            tilProducto.setError(getString(R.string.mov_error_producto));
            hayErrores = true;
        }

        int cantidad = 0;
        if (cantidadTexto.isEmpty()) {
            mostrarErrorDeCampo(etCantidad, tvErrorCantidad, getString(R.string.mov_error_cantidad_vacia));
            hayErrores = true;
        } else if (!Validadores.cantidadValida(cantidadTexto)) {
            mostrarErrorDeCampo(etCantidad, tvErrorCantidad, getString(R.string.mov_error_cantidad));
            hayErrores = true;
        } else {
            cantidad = Integer.parseInt(cantidadTexto);
            if (tipoMovimiento.validaStock && productoSeleccionado != null
                    && cantidad > productoSeleccionado.getStockDisponible()) {
                mostrarErrorDeCampo(etCantidad, tvErrorCantidad,
                        getString(R.string.mov_error_supera_stock, productoSeleccionado.getStockDisponible()));
                hayErrores = true;
            }
        }

        if (hayErrores) {
            return;
        }

        enviarSolicitud(productoSeleccionado, origen, destino, cantidad);
    }

    private void enviarSolicitud(ProductoMovimientoItem producto,
                                 SucursalDto origen, SucursalDto destino, int cantidad) {
        mostrarEnviando(true);

        Integer destinoId = (destino != null) ? destino.getId() : null;
        MovimientoRequest datos = tipoMovimiento.crearRequest(
                producto.getIdProducto(), origen.getId(), destinoId, cantidad);

        Call<ResponseBody> llamada = api.registrarMovimiento(datos);
        llamadasEnCurso.add(llamada);
        llamada.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> respuesta) {
                llamadasEnCurso.remove(call);
                if (isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);

                if (respuesta.isSuccessful()) {
                    mostrarOperacionExitosa(producto, origen, destino, cantidad);
                } else if (respuesta.code() == 401) {
                    mostrarSesionVencida();
                } else if (respuesta.code() == 403) {
                    mostrarError(getString(R.string.mov_error_permiso));
                } else if (respuesta.code() == 400) {
                    mostrarErrorDeSolicitud(respuesta, origen);
                } else {
                    mostrarError(getString(R.string.mov_error_generico));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                llamadasEnCurso.remove(call);
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);
                mostrarError(getString(R.string.mov_error_conexion));
            }
        });
    }

    /** 400: si el backend informa el stock disponible o un mensaje de error específico, se muestra; si no, mensaje genérico. */
    private void mostrarErrorDeSolicitud(Response<ResponseBody> respuesta, SucursalDto origen) {
        int disponible = -1;
        String mensajeServidor = null;
        try (ResponseBody cuerpo = respuesta.errorBody()) {
            if (cuerpo != null) {
                String cuerpoTexto = cuerpo.string();
                JSONObject json = new JSONObject(cuerpoTexto);
                disponible = json.optInt("stock_disponible", -1);
                if (json.has("error")) {
                    mensajeServidor = json.optString("error");
                } else if (json.has("detail")) {
                    mensajeServidor = json.optString("detail");
                }
            }
        } catch (Exception e) {
            disponible = -1;
        }

        if (disponible >= 0) {
            mostrarError(getString(R.string.mov_error_stock_servidor, disponible));
            recargarProductos(origen); // el stock cambió: se actualiza la lista respetando el tipo de movimiento
        } else if (mensajeServidor != null && !mensajeServidor.trim().isEmpty()) {
            mostrarError(mensajeServidor);
        } else {
            mostrarError(getString(R.string.mov_error_generico));
        }
    }

    private void mostrarOperacionExitosa(ProductoMovimientoItem producto,
                                         SucursalDto origen, SucursalDto destino, int cantidad) {
        String destinoNombre = (destino != null) ? destino.getNombre() : "";
        String mensaje = tipoMovimiento.construirMensajeExito(
                this, cantidad, producto.getNombre(), origen.getNombre(), destinoNombre);

        new MaterialAlertDialogBuilder(this)
                .setTitle(tipoMovimiento.tituloExitoRes)
                .setMessage(mensaje)
                .setPositiveButton(R.string.mov_aceptar, null)
                .show();

        etCantidad.setText("");
        ocultarError(etCantidad, tvErrorCantidad);

        actvProducto.setText("", false);
        productoSeleccionado = null;
        tilProducto.setError(null);

        recargarProductos(origen);
    }

    private void recargarProductos(SucursalDto origen) {
        if (tipoMovimiento.usaCatalogo) {
            cargarCatalogoProductos();
        } else if (origen != null) {
            cargarProductos(origen.getId());
        }
    }

    // ---------- Mensajes ----------

    /** TK79: muestra el error debajo del campo y le pone el borde rojo. */
    private void mostrarErrorDeCampo(View campo, TextView tvError, String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
        campo.setBackgroundResource(R.drawable.input_fondo_error);
    }

    /** TK79: oculta el error del campo y le devuelve el borde normal. */
    private void ocultarError(View campo, TextView tvError) {
        if (tvError.getVisibility() == View.VISIBLE) {
            tvError.setVisibility(View.GONE);
            campo.setBackgroundResource(R.drawable.input_fondo);
        }
    }

    private void ocultarTodosLosErrores() {
        ocultarError(campoOrigen, tvErrorOrigen);
        ocultarError(campoDestino, tvErrorDestino);
        tilProducto.setError(null);
        ocultarError(etCantidad, tvErrorCantidad);
    }

    private void mostrarError(String mensaje) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.mov_error_titulo)
                .setMessage(mensaje)
                .setPositiveButton(R.string.mov_aceptar, null)
                .show();
    }

    /** Si no se pudieron cargar las sucursales, se ofrece reintentar o volver. */
    private void mostrarErrorDeCarga(String mensaje) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton(R.string.mov_reintentar, (dialogo, boton) -> cargarSucursales())
                .setNegativeButton(R.string.mov_volver, (dialogo, boton) -> finish())
                .show();
    }

    /** 401: el token venció (dura 60 minutos). Se borra la sesión y se vuelve al Login. */
    private void mostrarSesionVencida() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.mov_sesion_vencida_titulo)
                .setMessage(R.string.mov_sesion_vencida_mensaje)
                .setCancelable(false)
                .setPositiveButton(R.string.mov_aceptar, (dialogo, boton) -> {
                    SesionManager.getInstance(this).cerrarSesion();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }

    private void mostrarEnviando(boolean enviando) {
        btnConfirmar.setEnabled(!enviando);
        btnConfirmar.setText(enviando ? getString(R.string.mov_boton_enviando) : textoBotonOriginal);
    }

    // ---------- Desplegable y Buscador de Producto ----------

    private void configurarDesplegableProducto() {
        actvProducto.setThreshold(1);
        actvProducto.setOnItemClickListener((parent, view, position, id) -> {
            productoSeleccionado = (ProductoMovimientoItem) parent.getItemAtPosition(position);
            tilProducto.setError(null);
            actvProducto.post(() -> actvProducto.setSelection(0));
        });
        actvProducto.setOnClickListener(v -> {
            if (adaptadorProductos != null && adaptadorProductos.getCount() > 0) {
                actvProducto.showDropDown();
            }
        });
        actvProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No hace falta hacer nada.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilProducto.setError(null);
                if (productoSeleccionado != null && !s.toString().equals(productoSeleccionado.getTextoMostrar())) {
                    productoSeleccionado = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No hace falta hacer nada.
            }
        });
    }

    // ---------- Spinners ----------

    /**
     * Adapter con la posición 0 deshabilitada (texto guía), como en el Sprint 1.
     * TK79: el texto guía usa texto_tenue (antes Color.GRAY, contraste 3.3) y las opciones,
     * color_texto.
     */
    private ArrayAdapter<String> crearAdapter(List<String> textos) {
        int colorGuia = ContextCompat.getColor(this, R.color.texto_tenue);
        int colorTexto = ContextCompat.getColor(this, R.color.color_texto);
        return new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, textos) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView vista = (TextView) super.getView(position, convertView, parent);
                vista.setTextColor(position == 0 ? colorGuia : colorTexto);
                return vista;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView vista = (TextView) super.getDropDownView(position, convertView, parent);
                vista.setTextColor(position == 0 ? colorGuia : colorTexto);
                return vista;
            }
        };
    }
}
