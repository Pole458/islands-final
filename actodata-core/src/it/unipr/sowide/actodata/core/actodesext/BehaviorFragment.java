package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodes.actor.Behavior;

/**
 * A {@link BehaviorFragment} that defines only a portion the state and of the
 * cases of a {@link Behavior}. Mainly used to enable design by composition in
 * behaviors.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface BehaviorFragment {
    /**
     * Defines the cases of this fragment.
     *
     * @param self the composed behavior that contains this fragment
     * @param c    the case factory
     */
    void defineFragmentCases(ActoDataBaseBehavior self, ActoDataCaseFactory c);
}
