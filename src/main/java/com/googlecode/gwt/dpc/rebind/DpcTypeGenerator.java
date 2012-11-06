package com.googlecode.gwt.dpc.rebind;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.gwt.dpc.shared.Dpc;
import com.googlecode.gwt.dpc.shared.Input;
import com.googlecode.gwt.dpc.shared.InputOf;
import com.googlecode.gwt.dpc.shared.Result;
import com.googlecode.gwt.dpc.shared.ResultOf;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * This class generate a client side class with
 * all the implementation of the {@link InputOf}
 * class as arguments of the method into the 
 * interfaces extending {@link Dpc} interface.
 * @author alessandro.simi@gmail.com
 */
public class DpcTypeGenerator {
	
	private TreeLogger logger;
    private GeneratorContext context; 
    private String serviceName;

    private Set<String> inputs = new HashSet<String>();
    private Set<String> results = new HashSet<String>();
    
	public DpcTypeGenerator(TreeLogger logger, GeneratorContext context, String serviceName) {
		this.logger = logger;
		this.context = context;
		this.serviceName = serviceName.replace(".", "_");
	}
    
    public void create() {
        // Input
        create(Input.class, InputOf.class, inputs);
        // Result
        create(Result.class, ResultOf.class, results);
    }
    
    private <T> void create(Class<T> superClass, Class<? extends T> clazz, Set<String> types) {
    	// Names
    	String classOf = clazz.getSimpleName();
        String className = classOf + "_" + serviceName;
        String packageName = InputOf.class.getPackage().getName();
        // Source Generation
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);
        // Import
        composerFactory.addImport(superClass.getCanonicalName());
        composerFactory.addImport(clazz.getCanonicalName());
        // Interface
        composerFactory.setSuperclass(superClass.getName());
        // Source
        PrintWriter printWriter = context.tryCreate(logger, packageName, className);
        if (printWriter != null) {
            SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
            // Inputs
            sourceWriter.println("// " + superClass.getSimpleName() + "s");
            for(String name : types) {
            	String typeName = name.replace(".", "_").replace("<", "$").replace(">", "");
                sourceWriter.println("private " + classOf + "<" + name + "> " + typeName + ";");
            }
            sourceWriter.commit(logger);
        }
    }

    public void addInput(JType type) {
    	inputs.add(type.getParameterizedQualifiedSourceName());
    }

    public void addResult(JType type) {
    	results.add(type.getParameterizedQualifiedSourceName());
    }

}
