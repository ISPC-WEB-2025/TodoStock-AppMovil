package com.ispc.todostock;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MovimientosActivity extends AppCompatActivity {

    private ImageButton btnAtras;
    private Spinner spinnerOrigen, spinnerDestino, spinnerProducto;
    private EditText etCantidad;
    private AppCompatButton btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        // 1. Vinculación de los componentes con la clase R
        btnAtras = findViewById(R.id.btnAtras);
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        etCantidad = findViewById(R.id.etCantidad);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        // 2. Configurar el botón de retroceso
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Definición de datos mock
        String[] sucursalesMock = {"Seleccione sucursal", "Sucursal Centro", "Sucursal Norte", "Sucursal Sur"};
        String[] productosMock = {"Seleccione producto", "Lama aluminio 43mm BLANCO", "Lama aluminio 43mm NEGRO", "Lama aluminio 43mm PLATA", "Lama aluminio 43mm BRONCE", "Guía embutir recambio BLANCO", "Guía embutir recambio NEGRO", "Guía embutir recambio PLATA", "Guía embutir recambio BRONCE", "Cabezal mosquitero BLANCO", "Cabezal mosquitero NEGRO", "Cabezal mosquitero PLATA", "Cabezal mosquitero BRONCE", "Guía mosquitero BLANCO","Guía mosquitero NEGRO", "Guía mosquitero PLATA", "Guía mosquitero BRONCE"};

        // Creación de adaptadores para sucursales
        ArrayAdapter<String> adapterSucursales = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                sucursalesMock
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
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

        // Creación de adaptadores para productos
        ArrayAdapter<String> adapterProductos = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                productosMock
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
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

        // 3. Configurar las validaciones del botón Confirmar (TK26)
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cantidadStr = etCantidad.getText().toString().trim();
                int origenPos = spinnerOrigen.getSelectedItemPosition();
                int destinoPos = spinnerDestino.getSelectedItemPosition();
                int productoPos = spinnerProducto.getSelectedItemPosition();

                // Validar que se haya seleccionado un producto
                if (productoPos == 0) {
                    Toast.makeText(MovimientosActivity.this, "Por favor seleccione un producto", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que se hayan seleccionado las sucursales
                if (origenPos == 0 || destinoPos == 0) {
                    Toast.makeText(MovimientosActivity.this, "Debe seleccionar origen y destino", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que el origen y el destino no sean iguales (TK26)
                if (origenPos == destinoPos) {
                    Toast.makeText(MovimientosActivity.this, "El origen y el destino no pueden ser iguales", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar campo de cantidad vacío
                if (cantidadStr.isEmpty()) {
                    Toast.makeText(MovimientosActivity.this, "Por favor ingrese una cantidad", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidad = Integer.parseInt(cantidadStr);

                // Validar que la cantidad sea mayor a cero (TK26)
                if (cantidad <= 0) {
                    Toast.makeText(MovimientosActivity.this, "La cantidad debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Si pasa todas las validaciones
                Toast.makeText(MovimientosActivity.this, "Movimiento validado con éxito", Toast.LENGTH_SHORT).show();
            }
        });
    }
}