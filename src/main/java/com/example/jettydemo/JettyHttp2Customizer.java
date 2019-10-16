package com.example.jettydemo;

import java.security.Security;

import org.conscrypt.OpenSSLProvider;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class JettyHttp2Customizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {
	
	@Value("${jetty.http.server.port:8080}")
	private Integer httpPort;
	
	@Value("${jetty.http2.server.port:5432}")
	private Integer http2Port;

	@Override
	public void customize(JettyServletWebServerFactory factory) {

		factory.addServerCustomizers(new JettyServerCustomizer() {
			@Override
			public void customize(Server server) {
				Security.insertProviderAt(new OpenSSLProvider(),1);
				SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
				sslContextFactory.setKeyStorePath("src/main/resources/dynamicloader.p12");
				sslContextFactory.setKeyStorePassword("dynamicloader");
				sslContextFactory.setKeyManagerPassword("dynamicloader");
				sslContextFactory.setProvider("Conscrypt");
				sslContextFactory.addExcludeProtocols("TLSv1.3");
				sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
				ServerConnector connector = (ServerConnector) server.getConnectors()[0];
				int port = connector.getPort();
				HttpConfiguration httpConfiguration = connector.getConnectionFactory(HttpConnectionFactory.class)
						.getHttpConfiguration();
				HttpConfiguration httpConfigNew = new HttpConfiguration(httpConfiguration);
				httpConfigNew.setSecurePort(port);
				httpConfigNew.setSecureScheme("https");
				httpConfigNew.addCustomizer(new SecureRequestCustomizer());

				ConnectionFactory[] connectionFactories = createConnectionFactories(sslContextFactory, httpConfigNew);

				ServerConnector serverConnector = new ServerConnector(server, connectionFactories);
				serverConnector.setPort(http2Port);
				serverConnector.setIdleTimeout(300000);
				
				ServerConnector serverConnectorForHttp = new ServerConnector(server);
				serverConnectorForHttp.setPort(httpPort);
				serverConnectorForHttp.setIdleTimeout(300000);
				
				server.setConnectors(new Connector[] { serverConnector, serverConnectorForHttp });
				try {
					server.start();
				} catch (Exception e) {
					
				}
			}

		});
	}

	private ConnectionFactory[] createConnectionFactories(SslContextFactory sslContextFactory,
			HttpConfiguration httpConfigNew) {

		ALPNServerConnectionFactory alpnServerConnectionFactory = new ALPNServerConnectionFactory("h2");
		HTTP2ServerConnectionFactory http2ServerConnectionFactory = new HTTP2ServerConnectionFactory(httpConfigNew);
		HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfigNew);
		SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, alpnServerConnectionFactory.getProtocol());
		return new ConnectionFactory[] { sslConnectionFactory, alpnServerConnectionFactory,
				http2ServerConnectionFactory, httpConnectionFactory };
	}

}