package com.ispc.todostock.network;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TK64 [AUT-NET-01] - Integración de red: parseo de respuestas correctas.
 *
 * Usa MockWebServer: no sale a la red real. Se arma un Retrofit propio
 * apuntando al servidor falso (no se usa ApiClient, que apunta al backend real).
 */
public class ParseoRespuestasTest {

    private MockWebServer servidor;
    private InventarioApiService api;

    @Before
    public void setUp() throws Exception {
        servidor = new MockWebServer();
        servidor.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(servidor.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(InventarioApiService.class);
    }

    @After
    public void tearDown() throws Exception {
        servidor.shutdown();
    }

    /** El JSON del backend se convierte en una lista de SucursalDto. */
    @Test
    public void sucursales_respuestaCorrecta_seParseaLaLista() throws Exception {
        // Arrange
        String json = "["
                + "{\"id_suc\":1,\"nombre\":\"Fábrica Principal\","
                + "\"direccion\":\"Calle Industrial 100\",\"es_central\":true,"
                + "\"total_articulos\":13,\"articulos_con_stock\":13,"
                + "\"articulos_sin_stock\":0,\"articulos_alerta\":2},"
                + "{\"id_suc\":2,\"nombre\":\"Depósito Zona Sur\","
                + "\"direccion\":\"Av. Sabattini 3200\",\"es_central\":false,"
                + "\"total_articulos\":13,\"articulos_con_stock\":3,"
                + "\"articulos_sin_stock\":10,\"articulos_alerta\":0}"
                + "]";
        servidor.enqueue(new MockResponse().setResponseCode(200).setBody(json));

        // Act
        Response<List<SucursalDto>> respuesta = api.getSucursales().execute();

        // Assert
        Assert.assertTrue(respuesta.isSuccessful());
        List<SucursalDto> sucursales = respuesta.body();
        Assert.assertNotNull(sucursales);
        Assert.assertEquals(2, sucursales.size());

        Assert.assertEquals(1, sucursales.get(0).getId());
        Assert.assertEquals("Fábrica Principal", sucursales.get(0).getNombre());
        Assert.assertEquals("Calle Industrial 100", sucursales.get(0).getDireccion());
        Assert.assertTrue(sucursales.get(0).isEsCentral());

        Assert.assertEquals(2, sucursales.get(1).getId());
        Assert.assertFalse(sucursales.get(1).isEsCentral());
    }

    /** El JSON de inventario por sucursal se convierte en StockSucursalDto y la URL se arma bien. */
    @Test
    public void stock_respuestaCorrecta_seParseaLaListaYSeArmaLaUrl() throws Exception {
        // Arrange
        String json = "["
                + "{\"id_stock\":1,\"cantidad_stock\":150,\"stock_min\":50,"
                + "\"id_art\":1,\"id_suc\":1,"
                + "\"nombre_producto\":\"Perfil de aluminio 45mm\","
                + "\"nombre_sucursal\":\"Fábrica Principal\","
                + "\"codigo_producto\":\"ALU-45\",\"precio_venta\":\"12500.00\"}"
                + "]";
        servidor.enqueue(new MockResponse().setResponseCode(200).setBody(json));

        // Act
        Response<List<StockSucursalDto>> respuesta = api.getStockDeSucursal(1, true).execute();
        RecordedRequest pedido = servidor.takeRequest();

        // Assert: la app pidió la URL correcta
        Assert.assertEquals("/api/inventario/sucursales/1/inventario/?solo_con_stock=true",
                pedido.getPath());

        // Assert: la respuesta se parseó bien
        Assert.assertTrue(respuesta.isSuccessful());
        List<StockSucursalDto> stock = respuesta.body();
        Assert.assertNotNull(stock);
        Assert.assertEquals(1, stock.size());

        StockSucursalDto item = stock.get(0);
        Assert.assertEquals(1, item.getIdProducto());
        Assert.assertEquals(1, item.getIdSucursal());
        Assert.assertEquals("Perfil de aluminio 45mm", item.getNombreProducto());
        Assert.assertEquals("ALU-45", item.getCodigoProducto());
        Assert.assertEquals(150, item.getCantidad());
        Assert.assertEquals(50, item.getStockMinimo());
    }
}