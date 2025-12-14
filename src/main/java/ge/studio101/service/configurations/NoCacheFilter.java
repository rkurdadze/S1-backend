package ge.studio101.service.configurations;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpReq = (HttpServletRequest) request;
            HttpServletResponse httpResp = (HttpServletResponse) response;

            String requestURI = httpReq.getRequestURI();

            if (requestURI.matches(".*/photos/.*")) { // Проверка на вложенный путь /photos/
                // Разрешить кэширование для /photos/*
                httpResp.setHeader("Cache-Control", "public, max-age=31536000, immutable");
            } else {
                // Отключить кэширование для всех остальных запросов
                httpResp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                httpResp.setHeader("Pragma", "no-cache");
                httpResp.setDateHeader("Expires", 0);
            }
        }
        chain.doFilter(request, response);
    }
}
