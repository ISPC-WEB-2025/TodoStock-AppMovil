package com.ispc.todostock.repositorio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ispc.todostock.network.AuthApiService;
import com.ispc.todostock.network.LoginResponse;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TK64 [AUT-NET-02] - AuthRepository contra MockWebServer, caso 200:
 * el JSON se convierte en objetos y login() devuelve EXITO con los datos del usuario.
 *
 * El test arma su propio Retrofit con la URL de MockWebServer (no usa ApiClient)
 * y no sale a la red real.
 */
public class AuthRepositoryLoginTest {

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

    /** Login correcto: devuelve EXITO con los datos del usuario y pide la URL correcta. */
    @Test
    public void login_respuesta200_devuelveExitoConLosDatosDelUsuario() throws Exception {
        // Arrange
        String json = "{\"id\":7,\"nombre\":\"Admin Test\",\"email\":\"admin@test.com\","
                + "\"access\":\"token-de-acceso\",\"refresh\":\"token-de-refresco\","
                + "\"token\":\"token-de-acceso\",\"es_admin\":true,"
                + "\"es_empleado\":false,\"is_superuser\":false}";
        servidor.enqueue(new MockResponse().setResponseCode(200).setBody(json));

        // Act
        ResultadoLogin resultado = repositorio.login("admin@test.com", "Prueba123!");
        RecordedRequest pedido = servidor.takeRequest();

        // Assert: la app pidio POST a la URL de login con el email
        assertEquals("POST", pedido.getMethod());
        assertEquals("/api/usuarios/login/", pedido.getPath());
        assertTrue(pedido.getBody().readUtf8().contains("\"email\":\"admin@test.com\""));

        // Assert: el resultado es EXITO con los datos del usuario
        assertEquals(ResultadoLogin.Tipo.EXITO, resultado.getTipo());
        assertTrue(resultado.esExito());

        LoginResponse datos = resultado.getDatos();
        assertNotNull(datos);
        assertEquals(7, datos.getId());
        assertEquals("Admin Test", datos.getNombre());
        assertEquals("admin@test.com", datos.getEmail());
        assertEquals("token-de-acceso", datos.getAccess());
        assertEquals("token-de-refresco", datos.getRefresh());
        assertTrue(datos.isEsAdmin());
        assertFalse(datos.isEsEmpleado());
    }
}