package com.ispc.todostock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.ispc.todostock.network.StockSucursalDto;

import org.junit.Test;

/**
 * TK61 [AUT-UNIT-02] - Tests unitarios de la regla de stock bajo y del mapper de stock.
 *
 * Corren en la JVM local, sin emulador ni red. Cada test sigue el patrón AAA
 * (Arrange, Act, Assert) y los valores de prueba salen de clases de equivalencia
 * y de valores límite de la regla "stockMinimo > 0 y cantidad <= stockMinimo"
 * (la misma que usa el backend en "articulos_alerta").
 *
 * Trazabilidad: historia US03 (lista de productos con su stock por sucursal),
 * caso TC-STK-01, tareas TK16 (mapper) y TK17 (regla de stock bajo).
 */
public class StockItemTest {

    // ---------- Regla de stock bajo (TK17) ----------

    /** Clase de equivalencia: cantidad menor al mínimo. */
    @Test
    public void esBajoStock_cantidadMenorAlMinimo_esTrue() {

        StockItem item = unItem(0, 30);

        boolean bajo = item.esBajoStock();

        assertTrue(bajo);
    }

    /** Valor límite: cantidad igual al mínimo ya cuenta como stock bajo. */
    @Test
    public void esBajoStock_cantidadIgualAlMinimo_esTrue() {
        StockItem item = unItem(5, 5);

        boolean bajo = item.esBajoStock();

        assertTrue(bajo);
    }

    /** Valor límite: un producto por encima del mínimo no es stock bajo. */
    @Test
    public void esBajoStock_cantidadMayorAlMinimo_esFalse() {
        StockItem item = unItem(6, 5);

        boolean bajo = item.esBajoStock();

        assertFalse(bajo);
    }

    /** Clase de equivalencia: sin mínimo definido (0) nunca hay alerta, ni con 0 en stock. */
    @Test
    public void esBajoStock_minimoCeroConCantidadCero_esFalse() {
        // Arrange (como los productos de Carlos Paz: cantidad 0 y mínimo 0)
        StockItem item = unItem(0, 0);

        // Act
        boolean bajo = item.esBajoStock();

        // Assert
        assertFalse(bajo);
    }

    // ---------- Mapper de la API a la pantalla (TK16) ----------

    /** Del JSON que devuelve la API al StockItem: se copian los seis campos. */
    @Test
    public void desde_jsonDeLaApi_copiaLosSeisCamposAlStockItem() {
        String json = "{\"id_art\": 7, \"id_suc\": 2,"
                + " \"nombre_producto\": \"Perfil de aluminio 60mm\","
                + " \"codigo_producto\": \"COD-001\","
                + " \"cantidad_stock\": 0, \"stock_min\": 30}";
        StockSucursalDto dto = new Gson().fromJson(json, StockSucursalDto.class);

        StockItem item = StockItem.desde(dto);

        assertEquals(7, item.getId());
        assertEquals(2, item.getSucursalId());
        assertEquals("Perfil de aluminio 60mm", item.getNombre());
        assertEquals("COD-001", item.getCodigo());
        assertEquals(0, item.getCantidad());
        assertEquals(30, item.getStockMinimo());
    }


    private static StockItem unItem(int cantidad, int stockMinimo) {
        return new StockItem(1, 1, "Producto de prueba", "COD-TEST", cantidad, stockMinimo);
    }
}