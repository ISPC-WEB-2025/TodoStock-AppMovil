package com.ispc.todostock;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.InventarioApiService;
import com.ispc.todostock.network.MovimientoRequest;
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

    private Spinner spinnerOrigen, spinnerDestino, spinnerProducto;
    private EditText etCantidad;
    private AppCompatButton btnConfirmar;
    private CharSequence textoBotonOriginal;

    private FrameLayout campoOrigen, campoDestino, campoProducto;
    private TextView tvErrorOrigen, tvErrorDestino, tvErrorProducto, tvErrorCantidad;

    private final List<SucursalDto> sucursales = new ArrayList<>();
    private final List<StockSucursalDto> productosDelOrigen = new ArrayList<>();

    private InventarioApiService api;
    private final List<Call<?>> llamadasEnCurso = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        // TK79: la barra superior ahora es una MaterialToolbar; la flecha de volver es su navegación.
        Toolbar toolbar = findViewById(R.id.toolbarMovimientos);
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        etCantidad = findViewById(R.id.etCantidad);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        textoBotonOriginal = btnConfirmar.getText();

        campoOrigen = findViewById(R.id.campoOrigen);
        campoDestino = findViewById(R.id.campoDestino);
        campoProducto = findViewById(R.id.campoProducto);
        tvErrorOrigen = findViewById(R.id.tvErrorOrigen);
        tvErrorDestino = findViewById(R.id.tvErrorDestino);
        tvErrorProducto = findViewById(R.id.tvErrorProducto);
        tvErrorCantidad = findViewById(R.id.tvErrorCantidad);

        api = ApiClient.create(InventarioApiService.class);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> intentarTraslado());

        // Al elegir el origen, se cargan los productos con stock en esa sucursal.
        spinnerOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int posicion, long id) {
                if (posicion > 0) {
                    ocultarError(campoOrigen, tvErrorOrigen);
                    cargarProductos(sucursales.get(posicion - 1).getId());
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

        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int posicion, long id) {
                if (posicion > 0) {
                    ocultarError(campoProducto, tvErrorProducto);
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

        cargarSucursales();
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
        sucursales.addAll(lista);

        List<String> textos = new ArrayList<>();
        textos.add(guia);
        for (SucursalDto sucursal : lista) {
            textos.add(sucursal.getNombre());
        }
        spinnerOrigen.setAdapter(crearAdapter(textos));
        spinnerDestino.setAdapter(crearAdapter(textos));
    }

    private void mostrarProductos(List<StockSucursalDto> lista, String guia) {
        productosDelOrigen.clear();
        productosDelOrigen.addAll(lista);

        List<String> textos = new ArrayList<>();
        textos.add(guia);
        for (StockSucursalDto producto : lista) {
            textos.add(getString(R.string.mov_producto_item, producto.getNombreProducto(), producto.getCantidad()));
        }
        spinnerProducto.setAdapter(crearAdapter(textos));
    }

    // ---------- Envío del traslado ----------

    /**
     * Valida el formulario y, si está bien, envía el traslado.
     * TK79: revisa todos los campos y muestra cada error debajo de su campo.
     */
    private void intentarTraslado() {
        ocultarTodosLosErrores();

        int origenPos = spinnerOrigen.getSelectedItemPosition();
        int destinoPos = spinnerDestino.getSelectedItemPosition();
        int productoPos = spinnerProducto.getSelectedItemPosition();
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

        StockSucursalDto producto = null;
        if (productoPos <= 0 || productoPos > productosDelOrigen.size()) {
            mostrarErrorDeCampo(campoProducto, tvErrorProducto, getString(R.string.mov_error_producto));
            hayErrores = true;
        } else {
            producto = productosDelOrigen.get(productoPos - 1);
        }

        int cantidad = 0;
        if (cantidadTexto.isEmpty()) {
            mostrarErrorDeCampo(etCantidad, tvErrorCantidad, getString(R.string.mov_error_cantidad_vacia));
            hayErrores = true;
        } else if (!Validadores.cantidadValida(cantidadTexto)) {
            mostrarErrorDeCampo(etCantidad, tvErrorCantidad, getString(R.string.mov_error_cantidad));
            hayErrores = true;
        } else {
            cantidad = Integer.parseInt(cantidadTexto); // seguro: cantidadValida ya lo comprobó
            if (producto != null && cantidad > producto.getCantidad()) {
                mostrarErrorDeCampo(etCantidad, tvErrorCantidad,
                        getString(R.string.mov_error_supera_stock, producto.getCantidad()));
                hayErrores = true;
            }
        }

        if (hayErrores) {
            return;
        }

        enviarTraslado(producto, origen, destino, cantidad);
    }

    private void enviarTraslado(StockSucursalDto producto, SucursalDto origen, SucursalDto destino, int cantidad) {
        mostrarEnviando(true);

        MovimientoRequest datos = MovimientoRequest.traslado(
                producto.getIdProducto(), origen.getId(), destino.getId(), cantidad);

        Call<ResponseBody> llamada = api.registrarMovimiento(datos);
        llamadasEnCurso.add(llamada);
        llamada.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> respuesta) {
                llamadasEnCurso.remove(call);
                if (isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);

                if (respuesta.isSuccessful()) {
                    mostrarTrasladoExitoso(producto, origen, destino, cantidad);
                } else if (respuesta.code() == 401) {
                    mostrarSesionVencida();
                } else if (respuesta.code() == 403) {
                    mostrarError(getString(R.string.mov_error_permiso));
                } else if (respuesta.code() == 400) {
                    mostrarErrorDeTraslado(respuesta, origen);
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

    /** 400: si el backend informa el stock disponible, se muestra; si no, mensaje genérico. */
    private void mostrarErrorDeTraslado(Response<ResponseBody> respuesta, SucursalDto origen) {
        int disponible = -1;
        try (ResponseBody cuerpo = respuesta.errorBody()) {
            if (cuerpo != null) {
                disponible = new JSONObject(cuerpo.string()).optInt("stock_disponible", -1);
            }
        } catch (Exception e) {
            disponible = -1;
        }

        if (disponible >= 0) {
            mostrarError(getString(R.string.mov_error_stock_servidor, disponible));
            cargarProductos(origen.getId()); // el stock cambió: se actualiza la lista
        } else {
            mostrarError(getString(R.string.mov_error_generico));
        }
    }

    private void mostrarTrasladoExitoso(StockSucursalDto producto, SucursalDto origen, SucursalDto destino, int cantidad) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.mov_exito_titulo)
                .setMessage(getString(R.string.mov_exito_mensaje, cantidad,
                        producto.getNombreProducto(), origen.getNombre(), destino.getNombre()))
                .setPositiveButton(R.string.mov_aceptar, null)
                .show();

        // Se deja listo para otro traslado desde el mismo origen, con el stock actualizado.
        etCantidad.setText("");
        ocultarError(etCantidad, tvErrorCantidad);
        cargarProductos(origen.getId());
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
        ocultarError(campoProducto, tvErrorProducto);
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
