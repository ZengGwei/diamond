package com.taobao.diamond.server.listener;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description CookieFilter
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/6-6:20 下午-2025
 */
public class CookieFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化操作
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            // 过滤无效 Cookie
            HttpServletRequest filteredRequest = filterInvalidCookies(httpRequest);
            chain.doFilter(filteredRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private HttpServletRequest filterInvalidCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return request;
        }

        List<Cookie> validCookies = new ArrayList<>();
        for (Cookie cookie : cookies) {
            if (isValidCookie(cookie)) {
                validCookies.add(cookie);
            }
        }

        if (validCookies.size() == cookies.length) {
            return request;
        }

        return new HttpServletRequestWrapper(request) {
            @Override
            public Cookie[] getCookies() {
                return validCookies.toArray(new Cookie[0]);
            }
        };
    }

    private boolean isValidCookie(Cookie cookie) {
        // 自定义 Cookie 有效性检查逻辑
        // 例如，检查 Cookie 名称和值是否符合规范
        String name = cookie.getName();
        String value = cookie.getValue();
        return name != null && !name.isEmpty() && value != null;
    }

    @Override
    public void destroy() {
        // 销毁操作
    }
}
