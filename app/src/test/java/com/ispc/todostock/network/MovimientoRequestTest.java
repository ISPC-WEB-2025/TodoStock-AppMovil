package com.ispc.todostock.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.ispc.todostock.validacion.Validadores;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitarios locales (JVM) para construcción y serialización de MovimientoRequest.
 *
 * Cumplen con las pautas de testing del Sprint 2 y trazabilidad con la historia US06 (#60)
 * y la tarea de pruebas #TK118.
 * Estructurados bajo el patrón AAA (Arrange, Act, Assert).
 */
public class MovimientoRequestTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new Gson();
    }

    @Test
    public void traslado_generaPayloadCorrectoConOrigenYDestino() {
        // Arrange
        int idProducto = 10;
        int idOrigen = 1;
        int idDestino = 2;
        int cantidad = 5;

        // Act
        MovimientoRequest request = MovimientoRequest.traslado(idProducto, idOrigen, idDestino, cantidad);
        String json = gson.toJson(request);

        // Assert
        assertEquals(MovimientoRequest.TIPO_TRASLADO, request.getTipo());
        assertEquals(idProducto, request.getIdProducto());
        assertEquals(idOrigen, request.getIdSucursalOrigen());
        assertEquals(Integer.valueOf(idDestino), request.getIdSucursalDestino());
        assertEquals(cantidad, request.getCantidad());
        assertNull(request.getMotivo());

        assertTrue(json.contains("\"tipo\":\"Traslado\""));
        assertTrue(json.contains("\"id_art\":10"));
        assertTrue(json.contains("\"id_suc\":1"));
        assertTrue(json.contains("\"id_suc_destino\":2"));
        assertTrue(json.contains("\"cantidad\":5"));
    }

    @Test
    public void ingreso_generaPayloadEntradaSinDestino() {
        // Arrange
        int idProducto = 15;
        int idSucursal = 1; // Casa Central
        int cantidad = 20;

        // Act
        MovimientoRequest request = MovimientoRequest.ingreso(idProducto, idSucursal, cantidad);
        String json = gson.toJson(request);

        // Assert
        assertEquals(MovimientoRequest.TIPO_ENTRADA, request.getTipo());
        assertEquals(idProducto, request.getIdProducto());
        assertEquals(idSucursal, request.getIdSucursalOrigen());
        assertNull(request.getIdSucursalDestino());
        assertEquals(cantidad, request.getCantidad());
        assertEquals("Ingreso de mercadería", request.getMotivo());

        assertTrue(json.contains("\"tipo\":\"Entrada\""));
        assertTrue(json.contains("\"id_art\":15"));
        assertTrue(json.contains("\"id_suc\":1"));
        assertFalse(json.contains("\"id_suc_destino\""));
        assertTrue(json.contains("\"motivo\":\"Ingreso de mercadería\""));
    }

    @Test
    public void venta_generaPayloadSalidaSinDestinoYMotivoVenta() {
        // Arrange
        int idProducto = 8;
        int idSucursal = 2;
        int cantidad = 3;

        // Act
        MovimientoRequest request = MovimientoRequest.venta(idProducto, idSucursal, cantidad);
        String json = gson.toJson(request);

        // Assert
        assertEquals(MovimientoRequest.TIPO_SALIDA, request.getTipo());
        assertEquals(idProducto, request.getIdProducto());
        assertEquals(idSucursal, request.getIdSucursalOrigen());
        assertNull(request.getIdSucursalDestino());
        assertEquals(cantidad, request.getCantidad());
        assertEquals("Venta en mostrador", request.getMotivo());

        assertTrue(json.contains("\"tipo\":\"Salida\""));
        assertTrue(json.contains("\"id_art\":8"));
        assertTrue(json.contains("\"id_suc\":2"));
        assertFalse(json.contains("\"id_suc_destino\""));
        assertTrue(json.contains("\"motivo\":\"Venta en mostrador\""));
    }

    @Test
    public void merma_generaPayloadSalidaSinDestinoYMotivoMerma() {
        // Arrange
        int idProducto = 8;
        int idSucursal = 2;
        int cantidad = 1;

        // Act
        MovimientoRequest request = MovimientoRequest.merma(idProducto, idSucursal, cantidad);
        String json = gson.toJson(request);

        // Assert
        assertEquals(MovimientoRequest.TIPO_SALIDA, request.getTipo());
        assertEquals(idProducto, request.getIdProducto());
        assertEquals(idSucursal, request.getIdSucursalOrigen());
        assertNull(request.getIdSucursalDestino());
        assertEquals(cantidad, request.getCantidad());
        assertEquals("Merma o rotura de stock", request.getMotivo());

        assertTrue(json.contains("\"tipo\":\"Salida\""));
        assertTrue(json.contains("\"id_art\":8"));
        assertTrue(json.contains("\"id_suc\":2"));
        assertFalse(json.contains("\"id_suc_destino\""));
        assertTrue(json.contains("\"motivo\":\"Merma o rotura de stock\""));
    }

    @Test
    public void cantidadValida_valoresLimite_comportamientoEsperado() {
        // Arrange & Act & Assert
        assertFalse(Validadores.cantidadValida("0"));
        assertFalse(Validadores.cantidadValida("-1"));
        assertFalse(Validadores.cantidadValida(""));
        assertFalse(Validadores.cantidadValida("abc"));
        assertTrue(Validadores.cantidadValida("1"));
        assertTrue(Validadores.cantidadValida("100"));
    }
}
