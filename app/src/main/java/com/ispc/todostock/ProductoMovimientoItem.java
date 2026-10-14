package com.ispc.todostock;

import androidx.annotation.NonNull;

import com.ispc.todostock.network.ProductoDto;
import com.ispc.todostock.network.StockSucursalDto;

/**
 * Representa un producto seleccionable en el desplegable de Movimientos.
 *
 * Puede originarse de un StockSucursalDto (traslados, ventas y mermas)
 * o de un ProductoDto del catálogo (ingreso de mercadería a Casa Central).
 */
public class ProductoMovimientoItem {

    private final int idProducto;
    private final String nombre;
    private final String codigo;
    private final int stockDisponible;
    private final StockSucursalDto stockDto;
    private final ProductoDto catalogoDto;
    private final String textoMostrar;

    public ProductoMovimientoItem(StockSucursalDto dto) {
        this.idProducto = dto.getIdProducto();
        this.nombre = dto.getNombreProducto();
        this.codigo = dto.getCodigoProducto();
        this.stockDisponible = dto.getCantidad();
        this.stockDto = dto;
        this.catalogoDto = null;
        String cod = (codigo != null && !codigo.trim().isEmpty()) ? " [" + codigo.trim() + "]" : "";
        this.textoMostrar = nombre + cod + " (" + stockDisponible + " disp.)";
    }

    public ProductoMovimientoItem(ProductoDto dto) {
        this.idProducto = dto.getIdProducto();
        this.nombre = dto.getNombre();
        this.codigo = dto.getCodigo();
        this.stockDisponible = -1;
        this.stockDto = null;
        this.catalogoDto = dto;
        String cod = (codigo != null && !codigo.trim().isEmpty()) ? " [" + codigo.trim() + "]" : "";
        this.textoMostrar = nombre + cod;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public StockSucursalDto getStockDto() {
        return stockDto;
    }

    public ProductoDto getCatalogoDto() {
        return catalogoDto;
    }

    public String getTextoMostrar() {
        return textoMostrar;
    }

    @NonNull
    @Override
    public String toString() {
        return textoMostrar;
    }
}
