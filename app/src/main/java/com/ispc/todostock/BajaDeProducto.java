package com.ispc.todostock;

/**
 * TK39 - Baja lógica de productos hecha desde la app.
 *
 * El backend no tiene un campo "activo" ni permite borrar productos con stock, así que
 * un producto dado de baja se marca con el prefijo "[INACTIVO] " en el nombre (PATCH).
 * Esta clase concentra esa regla para que el listado, la edición y los tests usen la misma.
 */
public final class BajaDeProducto {

    private static final String MARCA = "[INACTIVO]";
    private static final String PREFIJO = MARCA + " ";

    private BajaDeProducto() {

    }

    public static boolean estaInactivo(String nombre) {
        return nombre != null && nombre.startsWith(MARCA);
    }
    public static String nombreSinMarca(String nombre) {
        if (nombre == null) {
            return "";
        }
        if (estaInactivo(nombre)) {
            return nombre.substring(MARCA.length()).trim();
        }
        return nombre.trim();
    }

    public static String marcarInactivo(String nombre) {
        return PREFIJO + nombreSinMarca(nombre);
    }

    public static String reactivar(String nombre) {
        return nombreSinMarca(nombre);
    }
}