package ws.m4ktub.quacking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;

import ws.m4ktub.quacking.utils.Mixins;

public class ReadmeTest {

	public HttpServletRequest getRequest() {
		return new HttpServletRequest() {

			@Override
			public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

			}

			@Override
			public void setAttribute(String name, Object o) {

			}

			@Override
			public void removeAttribute(String name) {

			}

			@Override
			public boolean isSecure() {
				return false;
			}

			@Override
			public int getServerPort() {
				return 0;
			}

			@Override
			public String getServerName() {
				return null;
			}

			@Override
			public String getScheme() {
				return null;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String path) {
				return null;
			}

			@Override
			public int getRemotePort() {
				return 0;
			}

			@Override
			public String getRemoteHost() {
				return null;
			}

			@Override
			public String getRemoteAddr() {

				return null;
			}

			@Override
			public String getRealPath(String path) {

				return null;
			}

			@Override
			public BufferedReader getReader() throws IOException {

				return null;
			}

			@Override
			public String getProtocol() {

				return null;
			}

			@Override
			public String[] getParameterValues(String name) {

				return null;
			}

			@Override
			public Enumeration<?> getParameterNames() {

				return null;
			}

			@Override
			public Map<?, ?> getParameterMap() {

				return null;
			}

			@Override
			public String getParameter(String name) {

				return null;
			}

			@Override
			public Enumeration<?> getLocales() {

				return null;
			}

			@Override
			public Locale getLocale() {

				return null;
			}

			@Override
			public int getLocalPort() {

				return 0;
			}

			@Override
			public String getLocalName() {

				return null;
			}

			@Override
			public String getLocalAddr() {

				return null;
			}

			@Override
			public ServletInputStream getInputStream() throws IOException {

				return null;
			}

			@Override
			public String getContentType() {

				return null;
			}

			@Override
			public int getContentLength() {

				return 0;
			}

			@Override
			public String getCharacterEncoding() {

				return null;
			}

			@Override
			public Enumeration<?> getAttributeNames() {

				return null;
			}

			@Override
			public Object getAttribute(String name) {

				return null;
			}

			@Override
			public boolean isUserInRole(String role) {

				return false;
			}

			@Override
			public boolean isRequestedSessionIdValid() {

				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromUrl() {

				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromURL() {

				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromCookie() {

				return false;
			}

			@Override
			public Principal getUserPrincipal() {

				return null;
			}

			@Override
			public HttpSession getSession(boolean create) {

				return null;
			}

			@Override
			public HttpSession getSession() {

				return null;
			}

			@Override
			public String getServletPath() {

				return null;
			}

			@Override
			public String getRequestedSessionId() {

				return null;
			}

			@Override
			public StringBuffer getRequestURL() {

				return null;
			}

			@Override
			public String getRequestURI() {

				return null;
			}

			@Override
			public String getRemoteUser() {

				return null;
			}

			@Override
			public String getQueryString() {

				return null;
			}

			@Override
			public String getPathTranslated() {

				return null;
			}

			@Override
			public String getPathInfo() {

				return null;
			}

			@Override
			public String getMethod() {

				return null;
			}

			@Override
			public int getIntHeader(String name) {

				return 0;
			}

			@Override
			public Enumeration<?> getHeaders(String name) {
				return null;
			}

			@Override
			public Enumeration<?> getHeaderNames() {

				return null;
			}

			@Override
			public String getHeader(String name) {

				return null;
			}

			@Override
			public long getDateHeader(String name) {

				return 0;
			}

			@Override
			public Cookie[] getCookies() {

				return null;
			}

			@Override
			public String getContextPath() {

				return null;
			}

			@Override
			public String getAuthType() {

				return null;
			}
		};
	}

	public class ForceCharacterEncodingWrapper extends HttpServletRequestWrapper {

		private String encoding;
		
		public ForceCharacterEncodingWrapper(HttpServletRequest request, String encoding) {
			super(request);
			this.encoding = encoding;
		}
		
		@Override
		public String getCharacterEncoding() {
			return this.encoding;
		}
		
	}
	
	public class ForceCharacterEncoding {
		
		private String encoding;
		
		public ForceCharacterEncoding(String encoding) {
			this.encoding = encoding;
		}

		public String getCharacterEncoding() {
			return this.encoding;
		}
		
	}
	
	@Test
	public void base() throws Exception {
		HttpServletRequest wrappedRequest = new ForceCharacterEncodingWrapper(getRequest(), "UTF-8");
		Assert.assertEquals("UTF-8", wrappedRequest.getCharacterEncoding());
	}
	
	@Test
	public void mixed() throws Exception {
		Mixin mixin = Mixins.create(new ForceCharacterEncoding("UTF-8"), getRequest());
		HttpServletRequest mixedRequest = mixin.as(HttpServletRequest.class);
		Assert.assertEquals("UTF-8", mixedRequest.getCharacterEncoding());
	}
	
	@Test
	public void mixedRename() throws Exception {
		Mixin mixin = new Mixin();
		mixin.mix(new ForceCharacterEncoding("UTF-8")).rename("toString", "getCharacterEncoding");
		mixin.mix(getRequest());
		
		HttpServletRequest mixedRequest = mixin.as(HttpServletRequest.class);
		Assert.assertEquals("UTF-8", mixedRequest.toString());
	}
	
}
