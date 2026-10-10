package com.ispc.todostock;

import com.ispc.todostock.network.StockSucursalDto;

public class StockItem {
    private int id;
    private int sucursalId;
    private String nombre;
    private String codigo;
    private int cantidad;
    private int stockMinimo;

    public StockItem(int id, int sucursalId, String nombre, String codigo, int cantidad, int stockMinimo) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.nombre = nombre;
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
    }

    /**
     * TK16: convierte un producto tal como lo devuelve la API en el objeto
     * que usa la pantalla de Stock.
     */
    public static StockItem desde(StockSucursalDto dto) {
        return new StockItem(
                dto.getIdProducto(),
                dto.getIdSucursal(),
                dto.getNombreProducto(),
                dto.getCodigoProducto(),
                dto.getCantidad(),
                dto.getStockMinimo());
    }

    public int getId() {
        return id;
    }

    public int getSucursalId() {
        return sucursalId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public boolean esBajoStock() {
        return cantidad <= stockMinimo;
    }
}