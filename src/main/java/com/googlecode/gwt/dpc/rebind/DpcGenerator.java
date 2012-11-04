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
import com.googlecode.gwt.dpc.client.DpcExec;
import com.googlecode.gwt.dpc.client.DpcRequest;
import com.googlecode.gwt.dpc.client.DpcServiceAsync;
import com.googlecode.gwt.dpc.client.DpcServiceFactory;
import com.googlecode.gwt.dpc.shared.Dpc;
import com.googlecode.gwt.dpc.shared.Input;
import com.googlecode.gwt.dpc.shared.InputOf;

public class DpcGenerator extends Generator {
	
    private DpcTypeGenerator typeGenerator;
    private SourceWriterHelp writer;
    private TreeLogger logger;
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String requestedClass) throws UnableToCompleteException {
		this.logger = logger;
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
        if (implementedTypes == null || implementedTypes.length < 1) {
        	logger.log(TreeLogger.ERROR, "The type " + requestedClass + " doesn't implement any interface.");
            throw new UnableToCompleteException();
        } else if(implementedTypes.length == 1) {
        	implementedServices = new JClassType[1];
        	implementedServices[0] = objectType;
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
        typeGenerator = new DpcTypeGenerator(logger, context, requestedClass);
        
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
            typeGenerator.create();
        }
        return implPackageName + "." + implTypeName;
	}
	
	/**
	 * Writes the code of the method
	 * of the service interface.
	 * @param service Service class descriptor.
	 * @param method Method class descriptor.
	 * @throws UnableToCompleteException Exception error.
	 */
    private void writeMethod(JClassType service, JMethod method) throws UnableToCompleteException {
    	startMethod(method);
    	ReturnType type = ReturnType.find(method.getReturnType());
    	switch (type) {
			case PRIMITIVE:
				writePrimitive(method, type);
				break;
			case CLASS:
				writeClass(service, method);
				break;
			default:
				String message = "Impossible to determinate the return type of " + method.getName() + " method in " + service.getSimpleSourceName() + " service.";
				logger.log(TreeLogger.ERROR, message);
				throw new UnableToCompleteException();
		}
    	endMethod();
    }
    
    /**
     * Writes body code of method that
     * returns a primitive value.
     * @param method Method class descriptor.
     * @param type Return type.
     */
    private void writePrimitive(JMethod method, ReturnType type) {
    	JPrimitiveType primitive = type.getPrimitive();
    	if(primitive != JPrimitiveType.VOID) {
    		writer.println(primitive.getQualifiedSourceName() + " result;");
    		writer.println("return result;");
    	}
    }

	private static final String DPC_REQUEST = DpcRequest.class.getName();
	private static final String DPC_EXEC = DpcExec.class.getName();
	private static final String STRING = String.class.getName();
	private static final String INTEGER = Integer.class.getName();
	private static final String INPUT = Input.class.getSimpleName();
	private static final String ARRAY_LIST = ArrayList.class.getSimpleName();
    
	/**
	 * Writes the body code of method that
	 * returns an non-primitive value.
	 * @param service Service class descriptor.
	 * @param method Method class descriptor.
	 */
    private void writeClass(JClassType service, JMethod method) {
    	String className = service.getQualifiedSourceName();
    	String methodName = method.getName();
    	String returnName = method.getReturnType().getQualifiedSourceName();
    	writer.println("final " + returnName + " _result = new " + returnName + "();")
		  	  .println(INTEGER + " _id = System.identityHashCode(_result);")
    		  .println(STRING + " _className = \"" + className + "\";")
    		  .println(STRING + " _methodName = \"" + methodName + "\";")
    		  .println(ARRAY_LIST + "<" + STRING + "> _types = new " + ARRAY_LIST + "<" + STRING + ">();")
    		  .println(ARRAY_LIST + "<" + INPUT + "> _inputs = new " + ARRAY_LIST + "<" + INPUT + ">();");
    	writeResultParameters(method);
    	writer.println(DPC_REQUEST + " _request = new " + DPC_REQUEST + "(_id, _className, _methodName, _types, _inputs);")
    		  .println(DPC_EXEC + ".setRequest(_request);")
    		  .println("return _result;");
    	typeGenerator.addResult(returnName);
    }
    
    /**
     * This method writes the code
     * to populate the _types and _inputes
     * arrays with service method input
     * values and types.
     * @param method Method class descriptor.
     */
    private void writeResultParameters(JMethod method) {
    	JParameter[] parameters = method.getParameters();
    	for(JParameter parameter : parameters) {
    		String parameterName = parameter.getName();
    		JType type = parameter.getType();
    		JPrimitiveType primitive = type.isPrimitive();
    		if(primitive == null) {
    			String parameterClass = type.getQualifiedSourceName();
    			writer.println("_types.add(\"" + parameterClass + "\");");
    			writer.println("_inputs.add(new InputOf<" + parameterClass + ">().setValue(" + parameterName + "));");
				typeGenerator.addInput(parameterClass);
    		} else {
    			String boxedClass = primitive.getQualifiedBoxedSourceName();
    			writer.println("_types.add(\"" + boxedClass + "\");");
    			writer.println("_inputs.add(new InputOf<" + boxedClass + ">().setValue(new " + boxedClass + "(" + parameterName + ")));");
				typeGenerator.addInput(boxedClass);
    		}
    	}
    }
    
    /**
     * Write the code of the
     * head of the method.
     * @param method Method class.
     */
    private void startMethod(JMethod method) {
    	String readableDeclaration = method.getReadableDeclaration(false, true, true, true, true);
    	writer.println(readableDeclaration + " {")
    	      .in();
    }
    
    /**
     * Write the code of the
     * end of the method.
     * @param method Method class.
     */
    private void endMethod() {
    	writer.out()
    		  .println("}");
    }
    
}
