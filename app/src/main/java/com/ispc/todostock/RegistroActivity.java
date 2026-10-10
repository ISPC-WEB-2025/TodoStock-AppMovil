package com.ispc.todostock;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.RegistroApiService;
import com.ispc.todostock.network.RegistroRequest;
import com.ispc.todostock.validacion.Validadores;
import com.ispc.todostock.validacion.Validadores.RequisitoPassword;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TK34 - Registro de usuario.
 *
 * Valida todos los campos en la app antes de enviar (el backend no valida el
 * formato del DNI) y envía los datos a POST /api/usuarios/registro/.
 * La cuenta creada queda pendiente hasta que un Administrador le asigne un rol.
 */
public class RegistroActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilEmail, tilDni, tilFecha, tilPassword, tilConfirmar;
    private EditText etNombre, etEmail, etDni, etFecha, etPassword, etConfirmar;
    private CheckBox cbTerminos;
    private ColorStateList colorCasillaOriginal;
    private TextView tvErrorTerminos, tvErrorGeneral;
    private Button btnRegistrarme;

    private Call<ResponseBody> llamadaEnCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        tilNombre = findViewById(R.id.tilNombre);
        tilEmail = findViewById(R.id.tilEmail);
        tilDni = findViewById(R.id.tilDni);
        tilFecha = findViewById(R.id.tilFecha);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etDni = findViewById(R.id.etDni);
        etFecha = findViewById(R.id.etFecha);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmar);
        cbTerminos = findViewById(R.id.cbTerminos);
        colorCasillaOriginal = CompoundButtonCompat.getButtonTintList(cbTerminos);
        tvErrorTerminos = findViewById(R.id.tvErrorTerminos);
        tvErrorGeneral = findViewById(R.id.tvErrorGeneral);
        btnRegistrarme = findViewById(R.id.btnRegistrarme);

        // Flecha de la barra superior: vuelve a Login.
        Toolbar toolbar = findViewById(R.id.toolbarRegistro);
        toolbar.setNavigationOnClickListener(v -> finish());

        etFecha.setOnClickListener(v -> mostrarSelectorDeFecha());
        // TK52: abre la pantalla con los términos y la política de privacidad.
        findViewById(R.id.tvVerTerminos).setOnClickListener(v ->
                startActivity(new Intent(this, TerminosActivity.class)));
        btnRegistrarme.setOnClickListener(v -> intentarRegistro());

        // TK52: al marcar la casilla se va el error de los términos.
        cbTerminos.setOnCheckedChangeListener((casilla, marcada) -> {
            if (marcada) mostrarErrorTerminos(false);
        });
    }

    @Override
    protected void onDestroy() {
        // Si se cierra la pantalla con una solicitud en curso, se cancela.
        if (llamadaEnCurso != null) {
            llamadaEnCurso.cancel();
        }
        super.onDestroy();
    }

    private void mostrarSelectorDeFecha() {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog dialogo = new DatePickerDialog(
                this,
                (view, anio, mes, dia) -> etFecha.setText(
                        String.format(Locale.US, "%02d/%02d/%04d", dia, mes + 1, anio)),
                hoy.get(Calendar.YEAR),
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH));
        // No se pueden elegir fechas futuras.
        dialogo.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialogo.show();
    }

    private void intentarRegistro() {
        limpiarErrores();

        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmacion = etConfirmar.getText().toString();

        boolean todoValido = true;

        if (!Validadores.nombreValido(nombre)) {
            tilNombre.setError(getString(R.string.registro_error_nombre, Validadores.NOMBRE_MIN));
            todoValido = false;
        }
        if (!Validadores.emailValido(email)) {
            tilEmail.setError(getString(R.string.registro_error_email));
            todoValido = false;
        }
        if (!Validadores.dniValido(dni)) {
            tilDni.setError(getString(R.string.registro_error_dni));
            todoValido = false;
        }
        if (!Validadores.fechaNacimientoValida(fecha)) {
            tilFecha.setError(getString(R.string.registro_error_fecha));
            todoValido = false;
        } else if (!Validadores.esMayorDeEdad(fecha)) {
            // #221: edad mínima para registrarse.
            tilFecha.setError(getString(R.string.registro_error_edad, Validadores.EDAD_MINIMA));
            todoValido = false;
        }
        Set<RequisitoPassword> faltantes = Validadores.requisitosFaltantes(password);
        if (!faltantes.isEmpty()) {
            tilPassword.setError(mensajeDePassword(faltantes));
            todoValido = false;
        }
        if (!Validadores.passwordsCoinciden(password, confirmacion)) {
            tilConfirmar.setError(getString(R.string.registro_error_confirmacion));
            todoValido = false;
        }
        if (!cbTerminos.isChecked()) {
            mostrarErrorTerminos(true);
            todoValido = false;
        }

        // Si algo está mal, no se envía nada al backend.
        if (!todoValido) {
            return;
        }

        enviar(new RegistroRequest(
                nombre, email, dni, Validadores.fechaAFormatoApi(fecha), password));
    }

    private String mensajeDePassword(Set<RequisitoPassword> faltantes) {
        List<String> partes = new ArrayList<>();
        if (faltantes.contains(RequisitoPassword.LONGITUD)) {
            partes.add(getString(R.string.registro_password_longitud, Validadores.PASSWORD_MIN));
        }
        if (faltantes.contains(RequisitoPassword.LETRA)) {
            partes.add(getString(R.string.registro_password_letra));
        }
        if (faltantes.contains(RequisitoPassword.NUMERO)) {
            partes.add(getString(R.string.registro_password_numero));
        }
        if (faltantes.contains(RequisitoPassword.ESPECIAL)) {
            partes.add(getString(R.string.registro_password_especial));
        }
        // Arma una frase: "tener al menos 9 caracteres, incluir un número e incluir un carácter especial"
        StringBuilder lista = new StringBuilder();
        for (int i = 0; i < partes.size(); i++) {
            if (i > 0) {
                lista.append(i == partes.size() - 1
                        ? " " + getString(R.string.registro_password_y) + " "
                        : ", ");
            }
            lista.append(partes.get(i));
        }

        String sinEspacios = getString(R.string.registro_error_password_espacios);
        boolean tieneEspacios = faltantes.contains(RequisitoPassword.SIN_ESPACIOS);
        if (partes.isEmpty()) {
            return sinEspacios;
        }
        String mensaje = getString(R.string.registro_error_password, lista.toString());
        return tieneEspacios ? mensaje + " " + sinEspacios : mensaje;
    }

    private void enviar(RegistroRequest datos) {
        mostrarEnviando(true);

        llamadaEnCurso = ApiClient.create(RegistroApiService.class).registrar(datos);
        llamadaEnCurso.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> respuesta) {
                if (isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);

                if (respuesta.isSuccessful()) {
                    mostrarRegistroExitoso();
                } else if (respuesta.code() == 400) {
                    mostrarErrorDelServidor(leerCuerpoDeError(respuesta));
                } else {
                    mostrarErrorGeneral(getString(R.string.registro_error_generico));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarEnviando(false);
                mostrarErrorGeneral(getString(R.string.registro_error_conexion));
            }
        });
    }

    /** Lee el cuerpo JSON de una respuesta de error. Devuelve null si no se puede leer. */
    private JSONObject leerCuerpoDeError(Response<ResponseBody> respuesta) {
        try (ResponseBody cuerpo = respuesta.errorBody()) {
            if (cuerpo == null) return null;
            return new JSONObject(cuerpo.string());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Muestra el error 400 del backend debajo del campo que corresponde.
     * Usa "detalles" para saber qué campo falló y muestra un texto propio,
     * porque los mensajes del servidor son los genéricos de Django.
     * La app ya validó el formato del email y del DNI antes de enviar, así que
     * un error del servidor en esos campos significa que ya están registrados.
     */
    private void mostrarErrorDelServidor(JSONObject cuerpo) {
        boolean mostrado = false;
        JSONObject detalles = cuerpo == null ? null : cuerpo.optJSONObject("detalles");

        if (detalles != null && detalles.has("email")) {
            tilEmail.setError(getString(R.string.registro_error_email_registrado));
            mostrado = true;
        }
        if (detalles != null && detalles.has("dni")) {
            tilDni.setError(getString(R.string.registro_error_dni_registrado));
            mostrado = true;
        }
        if (!mostrado) {
            mostrarErrorGeneral(getString(R.string.registro_error_generico));
        }
    }

    private void mostrarRegistroExitoso() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.registro_exito_titulo)
                .setMessage(R.string.registro_exito_mensaje)
                .setCancelable(false)
                .setPositiveButton(R.string.registro_aceptar, (dialogo, cual) -> finish())
                .show();
    }

    private void mostrarErrorGeneral(String mensaje) {
        tvErrorGeneral.setText(mensaje);
        tvErrorGeneral.setVisibility(View.VISIBLE);
    }

    private void mostrarEnviando(boolean enviando) {
        btnRegistrarme.setEnabled(!enviando);
        btnRegistrarme.setText(enviando
                ? R.string.registro_boton_enviando
                : R.string.registro_boton);
    }

    /**
     * TK52: muestra u oculta el error de los términos. Con error, el cuadradito de la
     * casilla se pinta de rojo para que se vea a qué se refiere el mensaje.
     */
    private void mostrarErrorTerminos(boolean mostrar) {
        tvErrorTerminos.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        CompoundButtonCompat.setButtonTintList(cbTerminos, mostrar
                ? ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_errores))
                : colorCasillaOriginal);
    }

    private void limpiarErrores() {
        tilNombre.setError(null);
        tilEmail.setError(null);
        tilDni.setError(null);
        tilFecha.setError(null);
        tilPassword.setError(null);
        tilConfirmar.setError(null);
        mostrarErrorTerminos(false);
        tvErrorGeneral.setVisibility(View.GONE);
    }
}