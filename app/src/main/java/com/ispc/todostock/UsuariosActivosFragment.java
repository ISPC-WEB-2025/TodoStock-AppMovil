package com.ispc.todostock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * TK37: Fragmento para el catálogo general de usuarios (lista, edición y baja).
 * Asignado a Pedro Gonzalez.
 */
public class UsuariosActivosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_usuarios_activos, container, false);
    }
}
