package ru.javaops.masterjava.web.handler;

import java.util.List;
import java.util.Map;

import javax.xml.ws.handler.MessageContext;

import com.sun.xml.ws.api.handler.MessageHandlerContext;

import ru.javaops.masterjava.web.AuthUtil;

public class SoapServerSecurityHandler extends SoapBaseHandler {

    private String authHeader;

    public SoapServerSecurityHandler(String authHeader) {
        this.authHeader = authHeader;
    }

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        if (!isOutbound(context)) {
            Map<String, List<String>> headers = (Map<String, List<String>>) context.get(MessageContext.HTTP_REQUEST_HEADERS);
            int code = AuthUtil.checkBasicAuth(headers, authHeader);
            if (code != 0) {
                context.put(MessageContext.HTTP_RESPONSE_CODE, code);
                throw new SecurityException();
            }
        }
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        return true;
    }
}
