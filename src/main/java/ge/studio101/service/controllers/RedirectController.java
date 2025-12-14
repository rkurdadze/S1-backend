package ge.studio101.service.controllers;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.regex.Pattern;

@Controller
public class RedirectController {

    @GetMapping("/")
    public String redirectToSwagger() {
//    public String redirectToSwagger(HttpServletRequest request) {
//        String clientIp = getClientIp(request);

//        if (isAllowedIpAddress(clientIp)) {
        return "redirect:/swagger-ui/index.html";
//        }

        // Перенаправление на другой URL или выдача ошибки, если запрос не с разрешенного IP-адреса
//        return "redirect:/error/403"; // Или другой URL, например, "redirect:/home"
    }

    private boolean isAllowedIpAddress(String clientIp) {
        // Регулярные выражения для диапазонов IP-адресов частных сетей
        String ipPattern10 = "^10\\.(?:[0-9]{1,3}\\.){2}[0-9]{1,3}$"; // 10.0.0.0 - 10.255.255.255
        String ipPattern172 = "^172\\.(?:1[6-9]|2[0-9]|3[0-1])\\.(?:[0-9]{1,3}\\.)[0-9]{1,3}$"; // 172.16.0.0 - 172.31.255.255
        String ipPattern192 = "^192\\.168\\.(?:[0-9]{1,3}\\.)[0-9]{1,3}$"; // 192.168.0.0 - 192.168.255.255

        // Регулярное выражение для localhost (127.0.0.1 и ::1)
        String ipPatternLocalhost = "^127\\.0\\.0\\.1$"; // IPv4
        String ipPatternIPv6Localhost = "^::1$"; // IPv6

        return Pattern.matches(ipPattern10, clientIp) ||
                Pattern.matches(ipPattern172, clientIp) ||
                Pattern.matches(ipPattern192, clientIp) ||
                Pattern.matches(ipPatternLocalhost, clientIp) ||
                Pattern.matches(ipPatternIPv6Localhost, clientIp);
    }

    private String getClientIp(HttpServletRequest request) {
        // Проверка заголовка X-Forwarded-For для получения исходного IP
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isEmpty()) {
            return header.split(",")[0].trim();
        } else {
            return request.getRemoteAddr();
        }
    }
}
