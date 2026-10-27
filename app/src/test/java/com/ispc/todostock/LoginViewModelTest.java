package com.ispc.todostock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.ispc.todostock.network.LoginResponse;
import com.ispc.todostock.repositorio.AuthRepository;
import com.ispc.todostock.repositorio.ResultadoLogin;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * TK62 [AUT-UNIT-02] - Tests unitarios de LoginViewModel con el repositorio simulado.
 *
 * Corren en la JVM local, sin emulador ni red. AuthRepository se reemplaza por un
 * @Mock de Mockito, así el test no depende del backend. Cada test sigue el patrón
 * AAA (Arrange, Act, Assert): verifica el estado publicado en los LiveData y, con
 * verify, que el ViewModel le pidió el login al repositorio.
 *
 * Trazabilidad: historia US01 (login), casos TC-L-02 y TC-L-01, tarea TK112
 * (AuthRepository + LoginViewModel), issue #202.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginViewModelTest {

    private static final String EMAIL = "empleado@todostock.com";
    private static final String PASSWORD = "Clave2026!";

    /** Hace que los LiveData se actualicen en el mismo hilo del test. */
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AuthRepository repositorio;

    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        // Runnable::run ejecuta el login en el mismo hilo: el resultado está listo apenas vuelve login().
        viewModel = new LoginViewModel(repositorio, Runnable::run);
    }

    /** Caso feliz: el repositorio responde EXITO y el ViewModel lo publica. */
    @Test
    public void login_credencialesValidas_publicaExito() {
        // Arrange
        LoginResponse datos = new LoginResponse();
        when(repositorio.login(EMAIL, PASSWORD)).thenReturn(ResultadoLogin.exito(datos));

        // Act
        viewModel.login(EMAIL, PASSWORD);

        // Assert

        ResultadoLogin resultado = viewModel.getResultado().getValue();
        assertTrue(resultado.esExito());
        assertSame(datos, resultado.getDatos());
        assertFalse(viewModel.getCargando().getValue());
        assertFalse(viewModel.getErrorEmail().getValue());
        assertFalse(viewModel.getErrorPassword().getValue());
        verify(repositorio).login(EMAIL, PASSWORD);
    }

    /** Caso de error: el repositorio responde 401 y el ViewModel publica CREDENCIALES_INCORRECTAS. */
    @Test
    public void login_credencialesIncorrectas_publicaError() {
        // Arrange
        when(repositorio.login(EMAIL, PASSWORD))
                .thenReturn(ResultadoLogin.error(ResultadoLogin.Tipo.CREDENCIALES_INCORRECTAS));

        // Act
        viewModel.login(EMAIL, PASSWORD);

        // Assert

        ResultadoLogin resultado = viewModel.getResultado().getValue();
        assertFalse(resultado.esExito());
        assertEquals(ResultadoLogin.Tipo.CREDENCIALES_INCORRECTAS, resultado.getTipo());
        assertNull(resultado.getDatos());
        assertFalse(viewModel.getCargando().getValue());
        verify(repositorio).login(EMAIL, PASSWORD);
    }
}
