package com.ispc.todostock;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ispc.todostock.network.ApiClient;
import com.ispc.todostock.network.AuthApiService;
import com.ispc.todostock.repositorio.AuthRepository;
import com.ispc.todostock.repositorio.ResultadoLogin;
import com.ispc.todostock.validacion.Validadores;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TK112 - Lógica de la pantalla de Login.
 *
 * Valida los campos, pide el login al repositorio en segundo plano y publica el
 * estado para que LoginActivity lo muestre. No usa nada de la pantalla (ni Context
 * ni vistas), así se puede testear con JUnit y Mockito (AUT-UNIT-02).
 *
 * Qué publica:
 *   - getCargando():       true mientras se espera la respuesta del backend.
 *   - getErrorEmail():     true si el email no tiene un formato válido.
 *   - getErrorPassword():  true si la contraseña está vacía.
 *   - getResultado():      el ResultadoLogin cuando llega la respuesta (null si no hay ninguno pendiente).
 */
public class LoginViewModel extends ViewModel {

    private final AuthRepository repositorio;
    private final Executor executor;
    private final ExecutorService executorPropio; // solo si lo creó este ViewModel, para cerrarlo al final

    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> errorEmail = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> errorPassword = new MutableLiveData<>(false);
    private final MutableLiveData<ResultadoLogin> resultado = new MutableLiveData<>();

    /** Constructor que usa la app: el login se hace en un hilo aparte. */
    public LoginViewModel(AuthRepository repositorio) {
        this(repositorio, Executors.newSingleThreadExecutor());
    }

    /**
     * Constructor para los tests: se le puede pasar un Executor que ejecute en el
     * mismo hilo (por ejemplo, Runnable::run), así el resultado está listo apenas
     * vuelve login().
     */
    public LoginViewModel(AuthRepository repositorio, Executor executor) {
        this.repositorio = repositorio;
        this.executor = executor;
        this.executorPropio = executor instanceof ExecutorService ? (ExecutorService) executor : null;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<Boolean> getErrorEmail() {
        return errorEmail;
    }

    public LiveData<Boolean> getErrorPassword() {
        return errorPassword;
    }

    public LiveData<ResultadoLogin> getResultado() {
        return resultado;
    }

    /**
     * Valida los campos y, si están bien, pide el login al repositorio.
     * Si algún campo es inválido, no se llama al repositorio.
     */
    public void login(String email, String password) {
        // Si ya hay un pedido en curso, no se envía otro.
        if (Boolean.TRUE.equals(cargando.getValue())) {
            return;
        }

        String emailLimpio = email == null ? "" : email.trim();
        String passwordIngresada = password == null ? "" : password;

        boolean emailOk = Validadores.emailValido(emailLimpio);
        boolean passwordOk = !passwordIngresada.isEmpty();

        errorEmail.setValue(!emailOk);
        errorPassword.setValue(!passwordOk);
        if (!emailOk || !passwordOk) {
            return;
        }

        cargando.setValue(true);
        executor.execute(() -> {
            ResultadoLogin respuesta = repositorio.login(emailLimpio, passwordIngresada);
            cargando.postValue(false);
            resultado.postValue(respuesta);
        });
    }

    /**
     * La pantalla lo llama después de mostrar el resultado, para que no se vuelva
     * a mostrar (por ejemplo, al girar el celular).
     */
    public void resultadoMostrado() {
        resultado.setValue(null);
    }

    @Override
    protected void onCleared() {
        if (executorPropio != null) {
            executorPropio.shutdown();
        }
        super.onCleared();
    }

    /**
     * Crea el LoginViewModel con su repositorio real. Se usa en LoginActivity:
     *     new ViewModelProvider(this, LoginViewModel.FACTORY).get(LoginViewModel.class)
     */
    public static final ViewModelProvider.Factory FACTORY = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            AuthRepository repositorio = new AuthRepository(ApiClient.create(AuthApiService.class));
            return (T) new LoginViewModel(repositorio);
        }
    };
}