import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.SelectChannelEndPoint;
import org.eclipse.jetty.io.SelectorManager;
import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.Scheduler;


public class ServerConnector extends AbstractNetworkConnector
{
    private final SelectorManager _manager;
    private volatile ServerSocketChannel _acceptChannel;
    private volatile boolean _inheritChannel = false;
    private volatile int _localPort = -1;
    private volatile int _acceptQueueSize = 0;
    private volatile boolean _reuseAddress = true;
    private volatile int _lingerTime = -1;


    public ServerConnector(
        @Name("server") Server server,
        @Name("acceptors") int acceptors,
        @Name("selectors") int selectors)
    {
        this(server,null,null,null,acceptors,selectors,new HttpConnectionFactory());
    }

    public ServerConnector(
        @Name("server") Server server,
        @Name("factories") ConnectionFactory... factories)
    {
        this(server,null,null,null,-1,-1,factories);
    }


    public ServerConnector(
        @Name("server") Server server,
        @Name("sslContextFactory") SslContextFactory sslContextFactory)
    {
        this(server,null,null,null,-1,-1,AbstractConnectionFactory.getFactories(sslContextFactory,new HttpConnectionFactory()));
    }



    public ServerConnector(
        @Name("server") Server server,
        @Name("sslContextFactory") SslContextFactory sslContextFactory,
        @Name("factories") ConnectionFactory... factories)
    {
        this(server, null, null, null, -1, -1, AbstractConnectionFactory.getFactories(sslContextFactory, factories));
    }



    protected SelectorManager newSelectorManager(Executor executor, Scheduler scheduler, int selectors)
    {
        return new ServerConnectorManager(executor, scheduler, selectors);
    }

    @Override
    protected void doStart() throws Exception
    {
        super.doStart();

        if (getAcceptors()==0)
        {
            _acceptChannel.configureBlocking(false);
            _manager.acceptor(_acceptChannel);
        }
    }

    @Override
    public boolean isOpen()
    {
        ServerSocketChannel channel = _acceptChannel;
        return channel!=null && channel.isOpen();
    }

    /**
     * @return the selector priority delta
     * @deprecated not implemented
     */
    @Deprecated
    public int getSelectorPriorityDelta()
    {
        return _manager.getSelectorPriorityDelta();
    }


    public void setSelectorPriorityDelta(int selectorPriorityDelta)
    {
        _manager.setSelectorPriorityDelta(selectorPriorityDelta);
    }

    public boolean isInheritChannel()
    {
        return _inheritChannel;
    }

    public void setInheritChannel(boolean inheritChannel)
    {
        _inheritChannel = inheritChannel;
    }

    @Override
    public void open() throws IOException
    {
        if (_acceptChannel == null)
        {
            ServerSocketChannel serverChannel = null;
            if (isInheritChannel())
            {
                Channel channel = System.inheritedChannel();
                if (channel instanceof ServerSocketChannel)
                    serverChannel = (ServerSocketChannel)channel;
                else
                    LOG.warn("Unable to use System.inheritedChannel() [{}]. Trying a new ServerSocketChannel at {}:{}", channel, getHost(), getPort());
            }

            if (serverChannel == null)
            {
                serverChannel = ServerSocketChannel.open();

                InetSocketAddress bindAddress = getHost() == null ? new InetSocketAddress(getPort()) : new InetSocketAddress(getHost(), getPort());
                serverChannel.socket().setReuseAddress(getReuseAddress());
                serverChannel.socket().bind(bindAddress, getAcceptQueueSize());

                _localPort = serverChannel.socket().getLocalPort();
                if (_localPort <= 0)
                    throw new IOException("Server channel not bound");

                addBean(serverChannel);
            }

            serverChannel.configureBlocking(true);
            addBean(serverChannel);

            _acceptChannel = serverChannel;
        }
    }

    @Override
    public Future<Void> shutdown()
    {
        // shutdown all the connections
        return super.shutdown();
    }

    @Override
    public void close()
    {
        ServerSocketChannel serverChannel = _acceptChannel;
        _acceptChannel = null;

        if (serverChannel != null)
        {
            removeBean(serverChannel);

            // If the interrupt did not close it, we should close it
            if (serverChannel.isOpen())
            {
                try
                {
                    serverChannel.close();
                }
                catch (IOException e)
                {
                    LOG.warn(e);
                }
            }
        }
        // super.close();
        _localPort = -2;
    }

    @Override
    public void accept(int acceptorID) throws IOException
    {
        ServerSocketChannel serverChannel = _acceptChannel;
        if (serverChannel != null && serverChannel.isOpen())
        {
            SocketChannel channel = serverChannel.accept();
            accepted(channel);
        }
    }
    
    private void accepted(SocketChannel channel) throws IOException
    {
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        configure(socket);
        _manager.accept(channel);
    }

    protected void configure(Socket socket)
    {
        try
        {
            socket.setTcpNoDelay(true);
            if (_lingerTime >= 0)
                socket.setSoLinger(true, _lingerTime / 1000);
            else
                socket.setSoLinger(false, 0);
        }
        catch (SocketException e)
        {
            LOG.ignore(e);
        }
    }

    public SelectorManager getSelectorManager()
    {
        return _manager;
    }

    @Override
    public Object getTransport()
    {
        return _acceptChannel;
    }

    @Override
    @ManagedAttribute("local port")
    public int getLocalPort()
    {
        return _localPort;
    }

    protected SelectChannelEndPoint newEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key) throws IOException
    {
        return new SelectChannelEndPoint(channel, selectSet, key, getScheduler(), getIdleTimeout());
    }


    public int getSoLingerTime()
    {
        return _lingerTime;
    }

    public void setSoLingerTime(int lingerTime)
    {
        _lingerTime = lingerTime;
    }


    public int getAcceptQueueSize()
    {
        return _acceptQueueSize;
    }

    public void setAcceptQueueSize(int acceptQueueSize)
    {
        _acceptQueueSize = acceptQueueSize;
    }


    public boolean getReuseAddress()
    {
        return _reuseAddress;
    }

   
    public void setReuseAddress(boolean reuseAddress)
    {
        _reuseAddress = reuseAddress;
    }

    protected class ServerConnectorManager extends SelectorManager
    {
        public ServerConnectorManager(Executor executor, Scheduler scheduler, int selectors)
        {
            super(executor, scheduler, selectors);
        }


        protected void accepted(SocketChannel channel) throws IOException
        {
            ServerConnector.this.accepted(channel);
        }

        protected SelectChannelEndPoint newEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey selectionKey) throws IOException
        {
            return ServerConnector.this.newEndPoint(channel, selectSet, selectionKey);
        }

        public Connection newConnection(SocketChannel channel, EndPoint endpoint, Object attachment) throws IOException
        {
            return getDefaultConnectionFactory().newConnection(ServerConnector.this, endpoint);
        }


        protected void endPointOpened(EndPoint endpoint)
        {
            super.endPointOpened(endpoint);
            onEndPointOpened(endpoint);
        }


        protected void endPointClosed(EndPoint endpoint)
        {
            onEndPointClosed(endpoint);
            super.endPointClosed(endpoint);
        }
    }
}