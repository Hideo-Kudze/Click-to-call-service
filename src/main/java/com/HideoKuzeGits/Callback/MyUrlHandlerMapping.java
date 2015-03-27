package com.HideoKuzeGits.Callback;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 13.12.14.
 */
public class MyUrlHandlerMapping extends SimpleUrlHandlerMapping {

    private List<String> exclusions = new ArrayList<String>();

    @Override
    protected Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {

        for (String exclusion : exclusions)
            if (new AntPathMatcher().match(exclusion, urlPath))
                return null;


        return super.lookupHandler(urlPath, request);
    }

    public List<String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    @Override
    public void setDefaultHandler(Object defaultHandler) {
        throw new NoSuchMethodError();
    }
}
