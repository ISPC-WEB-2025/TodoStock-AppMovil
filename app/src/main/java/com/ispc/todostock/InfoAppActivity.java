package com.ispc.todostock;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Información de la app.
 *
 * Muestra el logo, el nombre, la versión, un slogan y el pie "ToDo Stock 2026".
 * La versión no está escrita a mano: sale de BuildConfig.VERSION_NAME (versionName del
 * build.gradle), así que si cambia en el futuro esta pantalla se actualiza sola.
 */
public class InfoAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_app);
        Toolbar toolbar = findViewById(R.id.toolbarInfoApp);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvVersion = findViewById(R.id.tvVersionApp);
        tvVersion.setText(getString(R.string.info_app_version, BuildConfig.VERSION_NAME));
    }
}