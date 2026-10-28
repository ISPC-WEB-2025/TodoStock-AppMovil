package com.ispc.todostock;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.ispc.todostock.validacion.Validadores;

/**
 * Aplica máscara automática dd/MM/yyyy sobre un campo de fecha y realiza
 * validación temprana al completar los 10 dígitos o al perder el foco.
 */
public class MascaraFechaWatcher implements TextWatcher {

    private final EditText editText;
    private final TextInputLayout textInputLayout;
    private final String errorFormato;
    private final String errorEdad;

    private boolean actualizando = false;
    private String textoAnterior = "";

    public MascaraFechaWatcher(EditText editText, TextInputLayout textInputLayout, String errorFormato, String errorEdad) {
        this.editText = editText;
        this.textInputLayout = textInputLayout;
        this.errorFormato = errorFormato;
        this.errorEdad = errorEdad;
    }

    public static MascaraFechaWatcher aplicar(EditText editText, TextInputLayout textInputLayout, String errorFormato, String errorEdad) {
        MascaraFechaWatcher watcher = new MascaraFechaWatcher(editText, textInputLayout, errorFormato, errorEdad);
        editText.addTextChangedListener(watcher);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                watcher.validar();
            }
        });
        return watcher;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        textoAnterior = s != null ? s.toString() : "";
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (textInputLayout != null && textInputLayout.getError() != null) {
            textInputLayout.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (actualizando) {
            return;
        }

        String texto = s != null ? s.toString() : "";
        String resultado = formatear(texto, textoAnterior);

        if (!resultado.equals(texto)) {
            actualizando = true;
            editText.setText(resultado);
            editText.setSelection(resultado.length());
            actualizando = false;
        }

        if (resultado.length() == 10) {
            validar();
        }
    }

    /**
     * Algoritmo puro de formateo de fecha (dd/MM/yyyy) reutilizable y testeable en JUnit.
     */
    public static String formatear(String textoActual, String textoAnterior) {
        if (textoActual == null) {
            return "";
        }
        if (textoAnterior == null) {
            textoAnterior = "";
        }

        boolean borrando = textoActual.length() < textoAnterior.length();
        String soloNumeros = textoActual.replaceAll("[^\\d]", "");

        if (borrando && textoAnterior.endsWith("/") && !textoActual.endsWith("/")) {
            if (soloNumeros.length() > 0) {
                soloNumeros = soloNumeros.substring(0, soloNumeros.length() - 1);
            }
        }

        if (soloNumeros.length() > 8) {
            soloNumeros = soloNumeros.substring(0, 8);
        }

        StringBuilder formateado = new StringBuilder();
        if (soloNumeros.length() <= 2) {
            formateado.append(soloNumeros);
            if (soloNumeros.length() == 2 && !borrando) {
                formateado.append("/");
            }
        } else if (soloNumeros.length() <= 4) {
            formateado.append(soloNumeros.substring(0, 2)).append("/").append(soloNumeros.substring(2));
            if (soloNumeros.length() == 4 && !borrando) {
                formateado.append("/");
            }
        } else {
            formateado.append(soloNumeros.substring(0, 2)).append("/")
                    .append(soloNumeros.substring(2, 4)).append("/")
                    .append(soloNumeros.substring(4));
        }

        return formateado.toString();
    }

    public boolean validar() {
        if (textInputLayout == null) {
            return true;
        }
        String texto = editText.getText() != null ? editText.getText().toString().trim() : "";
        if (texto.isEmpty()) {
            textInputLayout.setError(null);
            return true;
        }
        if (!Validadores.fechaNacimientoValida(texto)) {
            textInputLayout.setError(errorFormato);
            return false;
        }
        if (!Validadores.esMayorDeEdad(texto)) {
            textInputLayout.setError(errorEdad);
            return false;
        }
        textInputLayout.setError(null);
        return true;
    }
}
