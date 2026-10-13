package com.ispc.todostock;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Información de la app.
 *
 * Muestra el logo, el nombre, la versión, un slogan y el pie "ToDo Stock 2026".
 * La versión no está escrita a mano: se lee de la app instalada (versionName del
 * build.gradle), así que si cambia en el futuro esta pantalla se actualiza sola.
 */
public class InfoAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_app);
        Toolbar toolbar = findViewById(R.id.toolbarInfoApp);
        toolbar.setNavigationOnClickListener(v -> finish());

        mostrarVersion();
    }

    private void mostrarVersion() {
        TextView tvVersion = findViewById(R.id.tvVersionApp);
        try {
            String version = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            tvVersion.setText(getString(R.string.info_app_version, version));
        } catch (PackageManager.NameNotFoundException e) {
            tvVersion.setVisibility(View.GONE);
        }
    }
}