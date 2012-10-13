package com.googlecode.gwt.dpc.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * <p>Utility class to easily use the {@link
 * SourceWriter} class to write source.</p>
 * @author alessandro.simi@gmail.com
 */
public class SourceWriterHelp {

	private SourceWriter writer;

	public SourceWriterHelp(SourceWriter writer) {
		this.writer = writer;
	}
	
	public SourceWriterHelp println(String source) {
		writer.println(source);
		return this;
	}
	
	public SourceWriterHelp println(String source, Object... args) {
		writer.println(source, args);
		return this;
	}
	
	public SourceWriterHelp newline() {
		writer.println();
		return this;
	}
	
	public SourceWriterHelp in() {
		writer.indent();
		return this;
	}
	
	public SourceWriterHelp out() {
		writer.outdent();
		return this;
	}
	
	public void commit(TreeLogger logger) {
		writer.commit(logger);
	}

}
