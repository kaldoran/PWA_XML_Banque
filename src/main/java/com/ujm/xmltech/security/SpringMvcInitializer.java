package com.ujm.xmltech.security;

import org.springframework.core.annotation.Order;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


@Order(1)
public class SpringMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
 
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { AppConfig.class };
	}
 
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { AbstractContextLoaderInitializer.class };
	}
 
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
}
