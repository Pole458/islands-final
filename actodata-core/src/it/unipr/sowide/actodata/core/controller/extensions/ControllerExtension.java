package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.controller.Controller;

/**
 * A controller extension is a Java Interface that a controller implements in
 * order to get some sort of "extension methods" (i.e. the ones available
 * natively in languages like Kotlin or C#) in the scope of the controller
 * specialization, that might be useful to perform some specific operations.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see Controller
 */
public interface ControllerExtension {
    /**
     * Method used by default method of instances extending
     * {@link ControllerExtension} to perform controller-specific operations
     *
     * @return the controller using the extension
     */
    Controller getController();
}
