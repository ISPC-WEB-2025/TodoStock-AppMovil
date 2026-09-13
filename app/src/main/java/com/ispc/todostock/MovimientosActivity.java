package com.ispc.todostock;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MovimientosActivity extends AppCompatActivity {

    private Spinner spinnerOrigen;
    private Spinner spinnerDestino;
    private Spinner spinnerProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        // Vinculación de los componentes con la clase R
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        spinnerProducto = findViewById(R.id.spinnerProducto);

        // Definición de datos mock
        String[] sucursalesMock = {"Seleccione sucursal", "Sucursal Centro", "Sucursal Norte", "Sucursal Sur"};
        String[] productosMock = {"Seleccione producto", "Lama aluminio 43mm BLANCO", "Lama aluminio 43mm NEGRO", "Lama aluminio 43mm PLATA", "Lama aluminio 43mm BRONCE", "Guía embutir recambio BLANCO", "Guía embutir recambio NEGRO", "Guía embutir recambio PLATA", "Guía embutir recambio BRONCE", "Cabezal mosquitero BLANCO", "Cabezal mosquitero NEGRO", "Cabezal mosquitero PLATA", "Cabezal mosquitero BRONCE", "Guía mosquitero BLANCO","Guía mosquitero NEGRO", "Guía mosquitero PLATA", "Guía mosquitero BRONCE"};

        // Creación de adaptadores
        ArrayAdapter<String> adapterSucursales = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                sucursalesMock
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            // Controla el aspecto cuando el spinner está CERRADO (elemento seleccionado)
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(android.graphics.Color.GRAY);
                } else {
                    textView.setTextColor(android.graphics.Color.BLACK);
                }
                return view;
            }
            // Controla el aspecto cuando el menú está ABIERTO (lista desplegable)
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    textView.setTextColor(android.graphics.Color.GRAY);
                }
                return view;
            }
        };

        ArrayAdapter<String> adapterProductos = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                productosMock
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            // Controla el aspecto cuando el spinner está CERRADO (elemento seleccionado)
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(android.graphics.Color.GRAY);
                } else {
                    textView.setTextColor(android.graphics.Color.BLACK);
                }
                return view;
            }
            // Controla el aspecto cuando el menú está ABIERTO (lista desplegable)
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    textView.setTextColor(android.graphics.Color.GRAY);
                }
                return view;
            }
        };

        // Asignación de adaptadores a los Spinners
        spinnerOrigen.setAdapter(adapterSucursales);
        spinnerDestino.setAdapter(adapterSucursales);
        spinnerProducto.setAdapter(adapterProductos);
    }
}