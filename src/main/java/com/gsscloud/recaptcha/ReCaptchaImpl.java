package com.gsscloud.recaptcha;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReCaptchaImpl implements ReCaptcha {

    protected Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String SCRIPT_URL = "//www.google.com/recaptcha/api.js";
    public static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    protected String siteKey;
    protected String secret;

    public ReCaptchaImpl(String siteKey,String secret){
        if (siteKey == null || siteKey.isEmpty()){
            throw new IllegalArgumentException("Invalid siteKey");
        }
        if (secret == null || secret.isEmpty()){
            throw new IllegalArgumentException("Invalid secret");
        }
        this.siteKey = siteKey;
        this.secret = secret;
    }

    @Override
    public String createScriptResource(Map<String, String> parameters) {
        String url = SCRIPT_URL;
        List<String> queryList = new ArrayList<String>();
        for(String key : parameters.keySet()){
            try {
                queryList.add(String.format("%s=%s",key, URLEncoder.encode(parameters.get(key),"UTF-8")));
            } catch (UnsupportedEncodingException e) {
                //should never happen
            }
        }
        if (!queryList.isEmpty()){
            url = url + "?" + StringUtils.join(queryList,"&");
        }
        return "<script src=\"" + url + "\" async defer></script>";
    }

    @Override
    public String createReCaptchaTag(Map<String, String> parameters) {
        String attrs = String.format("data-sitekey=%s",siteKey);
        List<String> attrList = new ArrayList<String>();
        for(String key : parameters.keySet()){
            attrList.add(String.format("data-%s=\"%s\"",key,parameters.get(key)));
        }
        if (!attrList.isEmpty()){
            attrs = attrs + " " + StringUtils.join(attrList," ");
        }
        return "<div id=\"g-recaptcha\" class=\"g-recaptcha\" " + attrs + "></div>";
    }

    @Override
    public String getSiteKey() {
        return this.siteKey;
    }

    @Override
    public ReCaptchaResponse verifyResponse(String response, String remoteIp) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            URIBuilder uriBuilder = new URIBuilder(VERIFY_URL).addParameter("secret",secret);
            if (response != null){
                uriBuilder.addParameter("response", response);
            }
            if (remoteIp != null){
                uriBuilder.addParameter("remoteip",remoteIp);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            log.debug("status: {}", httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null){
                JSONObject json = new JSONObject(EntityUtils.toString(entity,"UTF-8"));
                Boolean success = json.getBoolean("success");
                JSONArray errorCodes = json.optJSONArray("error-codes");
                String errorCode = (errorCodes != null && errorCodes.length() > 0) ? errorCodes.optString(0, null) : null;
                return new ReCaptchaResponse(success, errorCode);
            }
        } catch (UnsupportedEncodingException e) {
            //bypass
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(),e);
        } finally {
            if (httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    //bypass
                }
            }
        }
        return new ReCaptchaResponse(false,"not-reachable");
    }
}
