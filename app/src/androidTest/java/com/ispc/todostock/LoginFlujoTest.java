package com.ispc.todostock;

import static org.junit.Assert.assertTrue;

import android.app.Activity;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.ispc.todostock.sesion.SesionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

/**
 * Pruebas instrumentadas de UI con Espresso para el flujo de Login hacia Menu Principal.
 * Utiliza el patron Screen Object (Page Object Model) sin invocar onView en el test
 * y sincroniza la peticion HTTP mediante IdlingResource sin usar Thread.sleep().
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFlujoTest {

    // Credenciales leidas de forma segura desde secrets.properties via BuildConfig
    private static final String EMAIL_VALIDO = BuildConfig.TEST_USER_EMAIL;
    private static final String PASSWORD_VALIDO = BuildConfig.TEST_USER_PASSWORD;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    private LoginActivity loginActivity;
    private IdlingResource loginIdlingResource;

    @BeforeClass
    public static void setUpClass() {
        // Habilita verificaciones de accesibilidad en cada accion de Espresso
        AccessibilityChecks.enable();
    }

    @Before
    public void setUp() {
        // Asegura que no haya sesion previa residual antes de iniciar la prueba
        SesionManager.getInstance(ApplicationProvider.getApplicationContext()).cerrarSesion();

        activityRule.getScenario().onActivity(activity -> loginActivity = activity);

        loginIdlingResource = new IdlingResource() {
            private ResourceCallback callback;

            @Override
            public String getName() {
                return "LoginNetworkIdlingResource";
            }

            @Override
            public boolean isIdleNow() {
                boolean cargandoRed = loginActivity != null
                        && !loginActivity.isFinishing()
                        && !loginActivity.findViewById(R.id.btnLogin).isEnabled();

                boolean abriendoMenu = loginActivity != null
                        && (loginActivity.isFinishing() || loginActivity.isDestroyed())
                        && !hayMenuEnResumen();

                boolean idle = !cargandoRed && !abriendoMenu;
                if (idle && callback != null) {
                    callback.onTransitionToIdle();
                }
                return idle;
            }

            @Override
            public void registerIdleTransitionCallback(ResourceCallback callback) {
                this.callback = callback;
            }
        };
        IdlingRegistry.getInstance().register(loginIdlingResource);
    }

    @After
    public void tearDown() {
        if (loginIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(loginIdlingResource);
        }
    }

    private boolean hayMenuEnResumen() {
        Collection<Activity> actividades = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED);
        for (Activity activity : actividades) {
            if (activity instanceof MenuPrincipalActivity) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida que al ingresar credenciales validas, la app se autentica contra
     * el backend y navega correctamente hacia el Menu Principal.
     */
    @Test
    public void credencialesValidas_navegaAMenuPrincipal() {
        // Arrange
        assertTrue(
                "Configurar TEST_USER_EMAIL y TEST_USER_PASSWORD en secrets.properties antes de correr este test",
                EMAIL_VALIDO != null && !EMAIL_VALIDO.trim().isEmpty()
                        && PASSWORD_VALIDO != null && !PASSWORD_VALIDO.trim().isEmpty()
        );
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.verificarVisible();

        // Act
        MenuPrincipalScreen menuScreen = loginScreen
                .ingresarEmail(EMAIL_VALIDO)
                .ingresarPassword(PASSWORD_VALIDO)
                .clickIngresar();

        // Assert
        menuScreen.verificarVisible();
        menuScreen.verificarSesionIniciada();
    }

    /**
     * Valida que con campos vacios no se navega y la pantalla de Login permanece visible.
     */
    @Test
    public void camposVacios_permaneceEnLogin() {
        // Arrange
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.verificarVisible();

        // Act
        loginScreen.clickIngresar();

        // Assert
        loginScreen.verificarVisible();
    }
}
