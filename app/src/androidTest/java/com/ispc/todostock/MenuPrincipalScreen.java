package com.ispc.todostock;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Screen Object que representa la pantalla principal (MenuPrincipalActivity).
 * Encapsula los identificadores de vista y las verificaciones del menu.
 */
public class MenuPrincipalScreen {

    private final int idToolbar = R.id.toolbarMenu;
    private final int idBotonCerrarSesion = R.id.btnCerrarSesion;
    private final int idRolActivo = R.id.tvRolActivo;

    public MenuPrincipalScreen verificarVisible() {
        onView(withId(idToolbar)).check(matches(isDisplayed()));
        onView(withId(idBotonCerrarSesion)).check(matches(isDisplayed()));
        return this;
    }

    public MenuPrincipalScreen verificarSesionIniciada() {
        onView(withId(idRolActivo)).check(matches(isDisplayed()));
        return this;
    }
}
