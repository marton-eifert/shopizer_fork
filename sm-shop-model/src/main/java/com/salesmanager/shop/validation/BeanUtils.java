package com.salesmanager.shop.validation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtils
{
 private BeanUtils(){
        
    }
    
    public static BeanUtils newInstance(){
        return new BeanUtils();
    }
    
    @SuppressWarnings( "nls" )
    public Object getPropertyValue( Object bean, String property )
        throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if(property == null){
            
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        Class<?> beanClass = bean.getClass();
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor( beanClass, property );
        if ( propertyDescriptor == null )
        {
            throw new IllegalArgumentException( "No such property " + property + " for " + beanClass + " exists" );
        }

        Method readMethod = propertyDescriptor.getReadMethod();
        if ( readMethod == null )
        {
            throw new IllegalStateException( "No getter available for property " + property + " on " + beanClass );
        }
        return readMethod.invoke( bean );
    }

    private PropertyDescriptor getPropertyDescriptor( Class<?> beanClass, String propertyname )
        throws IntrospectionException
    {
        BeanInfo beanInfo = Introspector.getBeanInfo( beanClass );
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        PropertyDescriptor propertyDescriptor = null;




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:02:43.654908):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


        for ( int i = 0; i < propertyDescriptors.length; i++ )
        {
            PropertyDescriptor currentPropertyDescriptor = propertyDescriptors[i];
            if ( currentPropertyDescriptor.getName().equals( propertyname ) )
            {
                propertyDescriptor = currentPropertyDescriptor;
            }

        }
        return propertyDescriptor;
    }
    
}
