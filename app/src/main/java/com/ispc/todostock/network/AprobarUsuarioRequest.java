package com.ispc.todostock.network;

import com.google.gson.annotations.SerializedName;

/**
 * TK109 - Datos para aprobar una cuenta y asignarle rol:
 * PUT /api/usuarios/<id>/
 */
public class AprobarUsuarioRequest {

    @SerializedName("is_active")
    private final boolean isActive;

    @SerializedName("rol_id")
    private final int rolId;

    public AprobarUsuarioRequest(int rolId) {
        this.isActive = true;
        this.rolId = rolId;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getRolId() {
        return rolId;
    }
}
