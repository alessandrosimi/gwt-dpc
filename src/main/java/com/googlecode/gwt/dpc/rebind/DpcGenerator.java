package com.googlecode.gwt.dpc.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.googlecode.gwt.dpc.client.Dpc;
import com.googlecode.gwt.dpc.client.DpcServiceAsync;
import com.googlecode.gwt.dpc.client.DpcServiceFactory;
import com.googlecode.gwt.dpc.shared.DpcException;
import com.googlecode.gwt.dpc.shared.Input;
import com.googlecode.gwt.dpc.shared.InputOf;

public class DpcGenerator extends Generator {
	
    private DpcInputGenerator inputGenerator;
    private SourceWriterHelp writer;
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String requestedClass) throws UnableToCompleteException {
		TypeOracle typeOracle = context.getTypeOracle();
		
		JClassType objectType = typeOracle.findType(requestedClass);
        if (objectType == null) {
            logger.log(TreeLogger.ERROR, "Could not find type: " + requestedClass);
            throw new UnableToCompleteException();
        }

        // Check if object is an interface
        if(objectType.isInterface() == null) {
            logger.log(TreeLogger.ERROR, "The type " + requestedClass + " must be an interface.");
            throw new UnableToCompleteException();
        }
        
        String implTypeName = objectType.getSimpleSourceName() + "_Implementation";
        String implPackageName = objectType.getPackage().getName();
        
        // Get Interfaces
        JClassType[] implementedServices = null;
        JClassType[] implementedTypes = objectType.getImplementedInterfaces();
        
        // Check Interfaces
        if (implementedTypes == null || implementedTypes.length < 2) {
        	logger.log(TreeLogger.ERROR, "The type " + requestedClass + " has not interfaces or implements only " + Dpc.class.getName() + " interface");
            throw new UnableToCompleteException();
        } else {
        	int numberOfInterfaces = implementedTypes.length - 1;
        	implementedServices = new JClassType[numberOfInterfaces];
            for(JClassType implementedType : implementedTypes) {
            	if(!implementedType.getQualifiedSourceName().equals(Dpc.class.getName())) {
            		implementedServices[--numberOfInterfaces] = implementedType;
            	}
            }
        }
        
        // Source Generation
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(implPackageName, implTypeName);
        
        // Add Imports
        composerFactory.addImport(Input.class.getCanonicalName());
        composerFactory.addImport(InputOf.class.getCanonicalName());
        composerFactory.addImport(ArrayList.class.getCanonicalName());
        composerFactory.addImport(DpcServiceAsync.class.getCanonicalName());
        composerFactory.addImport(DpcServiceFactory.class.getCanonicalName());
        composerFactory.addImport(AsyncCallback.class.getCanonicalName());
        
        // Add Interfaces
        composerFactory.addImplementedInterface(objectType.getQualifiedSourceName());
        
        // Input Collection
        inputGenerator = new DpcInputGenerator(logger, context, requestedClass);
        
        // Write Source
        PrintWriter printWriter = context.tryCreate(logger, implPackageName, implTypeName);
        if (printWriter != null) {
        	
            // Service
            writer = new SourceWriterHelp(composerFactory.createSourceWriter(context, printWriter));
            writer.newline()
            	  .println("private final " + DpcServiceAsync.class.getSimpleName() + " service = " + DpcServiceFactory.class.getSimpleName() + ".getInstance();")
            	  .newline();
            
            // Methods
            for(JClassType implementedService : implementedServices) {
	            JMethod[] methods = implementedService.getMethods();
	            for(JMethod method : methods) {
	            	writeMethod(implementedService, method);
	            }
            }
            writer.commit(logger);
            
            // Inputs Collection
            inputGenerator.create();
        }
        return implPackageName + "." + implTypeName;
	}
	
    private void writeMethod(JClassType service, JMethod method) {
    	startMethod(method);
    	ReturnType type = ReturnType.find(method.getReturnType());
    	switch (type) {
			case CLASS:
				writeClass(method);
				break;
			case PRIMITIVE:
				writePrimitive(method, type);
				break;
			case RESULT:
				writeResult(service, method);
				break;
			default:
				//TODO Error Case
				break;
		}
    	endMethod();
    }
    
    private void writeClass(JMethod method) {
    	writer.println("return null;");
    }
    
    private void writePrimitive(JMethod method, ReturnType type) {
    	JPrimitiveType primitive = type.getPrimitive();
    	if(primitive != JPrimitiveType.VOID) {
    		writer.println(primitive.getQualifiedSourceName() + " result;");
    		writer.println("return result;");
    	}
    }
    
    private void writeResult(JClassType service, JMethod method) {
    	String className = service.getQualifiedSourceName();
    	String methodName = method.getName();
    	String returnName = method.getReturnType().getQualifiedSourceName();
    	writer.println("final " + returnName + " _r = new " + returnName + "();");
    	writeResultParameters(method);
    	writer.println("try {")
    		  .in().println("this.service.call(\"" + className + "\", \"" + methodName + "\", _types, _inputs, new " + AsyncCallback.class.getSimpleName() + "<" + returnName + ">() {")
    		 	   .in().println("@Override")
    		 	   		.println("public void onSuccess(" + returnName + " result) {")
    		 	   		.in().println("_r.onSuccess(result);").out()
		    		  	.println("}")
		    		  	.println("@Override")
		    		  	.println("public void onFailure(Throwable caught) {")
		    		  	.in().println("_r.onFailure(caught);").out()
		    		  	.println("}").out()
		    		.println("});").out()
		      .println("} catch (" + DpcException.class.getName() + " _t) {")
		      .in().println("_r.onFailure(_t);").out()
		      .println("}")
    		  .println("return _r;");
    }
    
    private void writeResultParameters(JMethod method) {
    	String className = ArrayList.class.getSimpleName();
    	String genericName = Input.class.getSimpleName();
    	writer.println(className + "<" + String.class.getName() + "> _types = new " + className + "<" + String.class.getName() + ">();");
    	writer.println(className + "<" + genericName + "> _inputs = new " + className + "<" + genericName + ">();");
    	JParameter[] parameters = method.getParameters();
    	for(JParameter parameter : parameters) {
    		String parameterName = parameter.getName();
    		JType type = parameter.getType();
    		JPrimitiveType primitive = type.isPrimitive();
    		if(primitive == null) {
    			String parameterClass = type.getQualifiedSourceName();
    			writer.println("_types.add(\"" + parameterClass + "\");");
    			writer.println("_inputs.add(new InputOf<" + parameterClass + ">().setValue(" + parameterName + "));");
				inputGenerator.addClassName(parameterClass);
    		} else {
    			String boxedClass = primitive.getQualifiedBoxedSourceName();
    			writer.println("_types.add(\"" + boxedClass + "\");");
    			writer.println("_inputs.add(new InputOf<" + boxedClass + ">().setValue(new " + boxedClass + "(" + parameterName + ")));");
				inputGenerator.addClassName(boxedClass);
    		}
    	}
    }
    
    private void startMethod(JMethod method) {
    	String readableDeclaration = method.getReadableDeclaration(false, true, true, true, true);
    	writer.println(readableDeclaration + " {")
    	      .in();
    }
    
    private void endMethod() {
    	writer.out()
    		  .println("}");
    }
    
}