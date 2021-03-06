package org.andromda.cartridges.jsf.taglib;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import org.andromda.cartridges.jsf.component.JSFValidatorComponent;


/**
 * The tag class for the <code>s:validatorScript</code> tag.
 */
public class JSFValidatorTag
    extends UIComponentTag
{
    /**
     * The function name for validating the enclosing form.
     */
    private String functionName;

    /**
     * Sets the function name.
     *
     * @param functionName The new value for the function name.
     */
    public void setFunctionName(final String functionName)
    {
        this.functionName = functionName;
    }

    /**
     * Sets properties for the component.
     *
     * @param component The component whose properties we're setting
     */
    public void setProperties(final UIComponent component)
    {
        super.setProperties(component);

        final String attributeName = JSFValidatorComponent.FUNCTION_NAME;
        final String attributeValue = this.functionName;
        if (attributeValue != null)
        {
            if (UIComponentTag.isValueReference(this.functionName))
            {
                final FacesContext context = FacesContext.getCurrentInstance();
                final Application application = context.getApplication();
                final ValueBinding binding = application.createValueBinding(attributeValue);
                component.setValueBinding(
                    attributeName,
                    binding);
            }
            else
            {
                component.getAttributes().put(
                    attributeName,
                    attributeValue);
            }
        }
        final String validatorId = this.getId();
        if (validatorId != null)
        {
            component.getAttributes().put(
                JSFValidatorComponent.VALIDATOR_ID,
                validatorId);
            component.setId(validatorId);
        }
    }

    /**
     * Sets the <code>functionName</code> property to null.
     */
    public void release()
    {
        super.release();
        this.functionName = null;
    }

    /**
     * Returns the renderer type, which is null.
     */
    public String getRendererType()
    {
        return null;
    }

    /**
     * The component type.
     */
    private static final String COMPONENT_TYPE = JSFValidatorComponent.class.getName();

    /**
     * Returns the component type, which is
     * <code>org.andromda.cartridges.jsf.component.JSFValidatorScript</code>.
     */
    public String getComponentType()
    {
        return COMPONENT_TYPE;
    }
}