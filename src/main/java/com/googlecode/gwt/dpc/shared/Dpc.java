package com.googlecode.gwt.dpc.shared;

/**
 * <p>Interface to mark services. Create an
 * interface that extends the service(s) shared
 * with the server and {@link Dpc}.</p>
 * <pre>
 * interface MyGroupOfInterfaces extends MyService, Dpc {
 *   public MyResult method(...);
 * }</pre>
 * The group of interface can be used into the
 * client to access to the server implementation.
 * @author alessandro.simi@gmail.com
 */
public interface Dpc {}
