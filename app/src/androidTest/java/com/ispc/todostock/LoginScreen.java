package com.ispc.todostock;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Screen Object que representa la pantalla de inicio de sesion (LoginActivity).
 * Encapsula los identificadores de vista y las interacciones de usuario.
 */
public class LoginScreen {

    private final int idEmail = R.id.etUsuario;
    private final int idPassword = R.id.etPassword;
    private final int idBotonLogin = R.id.btnLogin;
    private final int idIrARegistro = R.id.tvIrARegistro;

    public LoginScreen ingresarEmail(String email) {
        onView(withId(idEmail)).perform(replaceText(email), closeSoftKeyboard());
        return this;
    }

    public LoginScreen ingresarPassword(String password) {
        onView(withId(idPassword)).perform(replaceText(password), closeSoftKeyboard());
        return this;
    }

    public MenuPrincipalScreen clickIngresar() {
        onView(withId(idBotonLogin)).perform(click());
        return new MenuPrincipalScreen();
    }

    public LoginScreen verificarVisible() {
        onView(withId(idBotonLogin)).check(matches(isDisplayed()));
        return this;
    }
}
