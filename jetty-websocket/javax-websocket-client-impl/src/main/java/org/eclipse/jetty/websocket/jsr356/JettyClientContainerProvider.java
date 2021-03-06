//
//  ========================================================================
//  Copyright (c) 1995-2017 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.websocket.jsr356;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

/**
 * Client {@link ContainerProvider} implementation.
 * <p>
 * Created by a {@link java.util.ServiceLoader} call in the
 * {@link javax.websocket.ContainerProvider#getWebSocketContainer()} call.
 */
public class JettyClientContainerProvider extends ContainerProvider
{
    private static Object lock = new Object();
    private static ClientContainer INSTANCE;
    
    public static ClientContainer getInstance()
    {
        return INSTANCE;
    }
    
    public static void stop() throws Exception
    {
        synchronized (lock)
        {
            if (INSTANCE == null)
            {
                return;
            }
            
            try
            {
                INSTANCE.stop();
            }
            finally
            {
                INSTANCE = null;
            }
        }
    }
    
    /**
     * Used by {@link ContainerProvider#getWebSocketContainer()} to get a new instance
     * of the Client {@link WebSocketContainer}.
     */
    @Override
    protected WebSocketContainer getContainer()
    {
        synchronized (lock)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new ClientContainer();
            }
        
            if (!INSTANCE.isStarted())
            {
                try
                {
                    INSTANCE.start();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Unable to start Client Container", e);
                }
            }
        
            return INSTANCE;
        }
    }
}
