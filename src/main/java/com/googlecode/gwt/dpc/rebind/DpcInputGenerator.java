package com.googlecode.gwt.dpc.rebind;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.gwt.dpc.client.Dpc;
import com.googlecode.gwt.dpc.shared.Input;
import com.googlecode.gwt.dpc.shared.InputOf;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * This class generate a client side class with
 * all the implementation of the {@link InputOf}
 * class as arguments of the method into the 
 * interfaces extending {@link Dpc} interface.
 * @author alessandro.simi@gmail.com
 */
public class DpcInputGenerator {
	
	private TreeLogger logger;
    private GeneratorContext context; 
    private String serviceName;
    
    private Set<String> classNames = new HashSet<String>();
    
	public DpcInputGenerator(TreeLogger logger, GeneratorContext context, String serviceName) {
		this.logger = logger;
		this.context = context;
		this.serviceName = serviceName.replace(".", "_");
	}
    
    public void create() {
        String collectionName = InputOf.class.getSimpleName() + "_" + serviceName;
        String packageName = InputOf.class.getPackage().getName();
        
        // Source Generation
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, collectionName);

        // Import
        composerFactory.addImport(Input.class.getCanonicalName());
        composerFactory.addImport(InputOf.class.getCanonicalName());
        
        // Interface
        composerFactory.setSuperclass(Input.class.getName());
        
        // Source
        PrintWriter printWriter = context.tryCreate(logger, packageName, collectionName);
        
        if (printWriter != null) {
            SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
            for(String className : classNames) {
            	String inputName = InputOf.class.getSimpleName();
            	String propertyName = className.replace(".", "_");
                sourceWriter.println("private " + inputName + "<" + className + "> " + propertyName + ";");
            }
            sourceWriter.commit(logger);
        }
    }
    
    public void addClassName(String className) {
    	classNames.add(className);
    }

}
