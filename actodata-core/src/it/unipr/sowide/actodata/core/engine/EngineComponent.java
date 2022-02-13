package it.unipr.sowide.actodata.core.engine;

import it.unipr.sowide.actodata.core.actodesext.BehaviorFragment;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;

import java.util.Set;

/**
 * Identifies the concept of "engine component", i.e. a behavior fragment
 * implementing a specific set of abilities of an {@link Engine}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface EngineComponent extends BehaviorFragment {
    /**
     * Returns a set of {@link ControllerExtension} classes that the controller
     * controlling an engine using this component should implement in order to
     * fully take advantage of the component.
     *
     * @return the set of controller extensions required
     */
    Set<Class<? extends ControllerExtension>> getControllerRequirements();
}
