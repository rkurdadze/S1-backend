package ge.studio101.service.configurations;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class IpAddressFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(IpAddressFilter.class);

    // Регулярные выражения для диапазонов IP-адресов частных сетей
    private final Pattern ipPattern10 = Pattern.compile("^10\\.(?:[0-9]{1,3}\\.){2}[0-9]{1,3}$");
    private final Pattern ipPattern172 = Pattern.compile("^172\\.(?:1[6-9]|2[0-9]|3[0-1])\\.(?:[0-9]{1,3}\\.)[0-9]{1,3}$");
    private final Pattern ipPattern192 = Pattern.compile("^192\\.168\\.(?:[0-9]{1,3}\\.)[0-9]{1,3}$");

    // Регулярные выражения для localhost (127.0.0.1)
    private final Pattern ipPatternLocalhost = Pattern.compile("^127\\.0\\.0\\.1$"); // IPv4

    // Регулярные выражения для диапазонов IPv6-адресов
    private final Pattern ipPatternIPv6Private1 = Pattern.compile("^fd[0-9a-f]{2}:.+"); // fd00::/8 (Unique Local Address)
    private final Pattern ipPatternIPv6Private2 = Pattern.compile("^fc[0-9a-f]{2}:.+"); // fc00::/8 (Unique Local Address)
    private final Pattern ipPatternIPv6LinkLocal = Pattern.compile("^fe80:.+"); // fe80::/10 (Link-Local Address)

    private final List<String> allowedIps = Arrays.asList(
//            "192.168.1.1", // замените на ваши IP-адреса
//            "127.0.0.1",  // IPv4 loopback
            "0:0:0:0:0:0:0:1" // IPv6 loopback
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String remoteIp = httpRequest.getRemoteAddr();

        if (isAllowedIp(remoteIp)) {
            log.info("allowed to swagger: " + remoteIp);
            chain.doFilter(request, response);

        } else {
            log.error("restricted swagger access for ip: " + remoteIp);
            response.getWriter().write("Access Denied");
            return;
        }
    }

    private boolean isAllowedIp(String ip) {
        if (allowedIps.contains(ip)) {
            return true;
        }

        return ipPattern10.matcher(ip).matches() ||
                ipPattern172.matcher(ip).matches() ||
                ipPattern192.matcher(ip).matches() ||
                ipPatternLocalhost.matcher(ip).matches() ||
                ipPatternIPv6Private1.matcher(ip).matches() ||
                ipPatternIPv6Private2.matcher(ip).matches() ||
                ipPatternIPv6LinkLocal.matcher(ip).matches();
    }

    @Override
    public void destroy() {
    }
}