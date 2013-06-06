package net.mikaboshi.jdbc.monitor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * JDBC MonitorのタグにリクエストURIを設定するフィルタ。
 *
 * @author Takuma Umezawa
 * @since 1.4.3
 */
public class JdbcMonitorTagginFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		if (!(req instanceof HttpServletRequest)) {
			chain.doFilter(req, res);
			return;
		}

		HttpServletRequest request = (HttpServletRequest) req;

		try {
			Tag.getInstance().set(request.getRequestURI());

			chain.doFilter(req, res);

		} finally {
			Tag.getInstance().remove();
		}
	}

}
