package it.polimi.tiw.project.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class LoginFilter
 */
public class LoginFilter implements Filter {
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //System.out.print("Login checker filter executing ...\n");
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String loginPath = req.getServletContext().getContextPath() + "/index.html";
		HttpSession session = req.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
          res.sendRedirect(loginPath);
          return;
        }
		
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	

    public void destroy() {
        // TODO Auto-generated method stub
    }

}
