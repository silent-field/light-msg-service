package com.silent.lms.configuration;

import com.google.common.base.Preconditions;
import com.silent.lms.annotations.Immutable;
import com.silent.lms.annotations.NotNull;

@Immutable
public class TcpListener implements Listener {

    private final int port;
    private final String name;

    private final @NotNull String bindAddress;

    /**
     * Creates a new TCP listener which listens to a specific port and bind address
     *
     * @param port        the port
     * @param bindAddress the bind address
     */
    public TcpListener(final int port, @NotNull final String bindAddress) {
        this(port, bindAddress, "tcp-listener-" + port);
    }

    /**
     * Creates a new TCP listener which listens to a specific port and bind address
     *
     * @param port        the port
     * @param bindAddress the bind address
     * @param name        the name of the listener
     */
    public TcpListener(final int port, @NotNull final String bindAddress, final @NotNull String name) {

        Preconditions.checkNotNull(bindAddress, "bindAddress must not be null");

        this.port = port;
        this.bindAddress = bindAddress;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getBindAddress() {
        return bindAddress;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String readableName() {
        return "TCP Listener";
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

}
