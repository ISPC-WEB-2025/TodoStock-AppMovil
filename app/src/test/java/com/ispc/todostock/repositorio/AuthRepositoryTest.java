package com.ispc.todostock.repositorio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ispc.todostock.network.AuthApiService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TK65 [AUT-NET-02] - AuthRepository contra MockWebServer, caso 500: la app no se rompe.
 * (Los tests extra cubren TK64 y TK65 del AUT-NET-01 #205.)
 *
 * Cada test arma su propio Retrofit con la URL de MockWebServer: no se usa ApiClient
 * (que apunta al backend real) y ningun test sale a la red.
 */
public class AuthRepositoryTest {

    private MockWebServer servidor;
    private AuthRepository repositorio;

    @Before
    public void setUp() throws Exception {
        servidor = new MockWebServer();
        servidor.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(servidor.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        repositorio = new AuthRepository(retrofit.create(AuthApiService.class));
    }

    @After
    public void tearDown() throws Exception {
        servidor.shutdown();
    }

    // ---------- Caso 500 (los 2 tests que pide el ticket #118) ----------

    /**
     * El 500 de Django suele venir como HTML, no como JSON: no debe romper la app.
     */
    @Test
    public void login_error500ConCuerpoHtml_devuelveErrorServidor() {
        // Arrange
        servidor.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("<html><body>Server Error (500)</body></html>"));

        // Act
        ResultadoLogin resultado = repositorio.login("test@test.com", "Prueba123!");

        // Assert
        assertEquals(ResultadoLogin.Tipo.ERROR_SERVIDOR, resultado.getTipo());
        assertFalse(resultado.esExito());
        assertNull(resultado.getDatos());
    }

    /**
     * Un 500 sin cuerpo tampoco debe romper la app.
     */
    @Test
    public void login_error500SinCuerpo_devuelveErrorServidor() {
        // Arrange
        servidor.enqueue(new MockResponse().setResponseCode(500));

        // Act
        ResultadoLogin resultado = repositorio.login("test@test.com", "Prueba123!");

        // Assert
        assertEquals(ResultadoLogin.Tipo.ERROR_SERVIDOR, resultado.getTipo());
        assertNull(resultado.getDatos());
    }
}