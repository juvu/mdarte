package org.andromda.metafacades.uml14;

import java.util.Collection;
import java.util.Iterator;

import org.omg.uml.behavioralelements.statemachines.State;
import org.omg.uml.behavioralelements.statemachines.Transition;


/**
 * MetafacadeLogic implementation.
 *
 * @see org.andromda.metafacades.uml.EventFacade
 */
public class EventFacadeLogicImpl
    extends EventFacadeLogic
{
    public EventFacadeLogicImpl(
        org.omg.uml.behavioralelements.statemachines.Event metaObject,
        java.lang.String context)
    {
        super(metaObject, context);
    }

    /**
     * @see org.andromda.metafacades.uml.EventFacade#getParameters()
     */
    protected Collection handleGetParameters()
    {
        return metaObject.getParameter();
    }

    /**
     * @see org.andromda.metafacades.uml.EventFacade#getTransition()
     */
    protected Object handleGetTransition()
    {
        Transition eventTransition = null;

        final Collection allTransitions =
            UML14MetafacadeUtils.getModel().getStateMachines().getTransition().refAllOfType();
        for (final Iterator iterator = allTransitions.iterator(); iterator.hasNext() && eventTransition == null;)
        {
            final Transition transition = (Transition)iterator.next();
            if (metaObject.equals(transition.getTrigger()))
            {
                eventTransition = transition;
            }
        }

        return eventTransition;
    }

    /**
     * @see org.andromda.metafacades.uml.EventFacade#getState()
     */
    protected Object handleGetState()
    {
        State eventState = null;

        final Collection allStates = UML14MetafacadeUtils.getModel().getStateMachines().getState().refAllOfType();
        for (final Iterator stateIterator = allStates.iterator(); stateIterator.hasNext() && eventState == null;)
        {
            final State state = (State)stateIterator.next();
            if (state.getDeferrableEvent().contains(metaObject))
            {
                eventState = state;
            }
        }

        return eventState;
    }

    /**
     * @see org.andromda.core.metafacade.MetafacadeBase#getValidationOwner()
     */
    public Object getValidationOwner()
    {
        Object validationOwner = this.getTransition();
        if (validationOwner == null)
        {
            validationOwner = this.getState();
        }
        return validationOwner;
    }
}