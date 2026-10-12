package com.ispc.todostock;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * TK109 / TK37: Contenedor principal de Gestión de Usuarios.
 * Integra el catálogo de usuarios activos (Pedro - TK37),
 * el formulario de carga directa y la aprobación de pendientes (Miguel - TK109).
 */
public class GestionUsuariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        MaterialToolbar toolbar = findViewById(R.id.toolbarUsuarios);
        toolbar.setNavigationOnClickListener(v -> finish());

        TabLayout tabLayout = findViewById(R.id.tabLayoutUsuarios);
        ViewPager2 viewPager = findViewById(R.id.viewPagerUsuarios);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new UsuariosActivosFragment(); // TK37 (Pedro)
                    case 1:
                        return new CargarUsuarioFragment();   // TK109 (Miguel)
                    case 2:
                        return new PendientesFragment();      // TK109 (Miguel)
                    default:
                        return new UsuariosActivosFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.usuarios_tab_activos);
                    break;
                case 1:
                    tab.setText(R.string.usuarios_tab_cargar);
                    break;
                case 2:
                    tab.setText(R.string.usuarios_tab_pendientes);
                    break;
            }
        }).attach();
    }
}
