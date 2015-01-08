package com.gsscloud.recaptcha;

import java.util.Map;

public interface ReCaptcha {

    /**
     * Create reCAPTCHA Script resource (api.js)
     * (https://developers.google.com/recaptcha/docs/display)
     *
     * @param parameters JavaScript resource (api.js) parameters
     * @return String
     */
    public String createScriptResource(Map<String,String> parameters);

    /**
     * Create reCAPTCHA tag
     * (https://developers.google.com/recaptcha/docs/display)
     *
     * @param parameters g-recaptcha tag attributes and grecaptcha.render parameters
     * @return String
     */
    public String createReCaptchaTag(Map<String,String> parameters);

    /**
     * Get Site Key
     *
     * @return String siteKey
     */
    public String getSiteKey();
    /**
     * Verify reCAPTCHA response
     * (https://developers.google.com/recaptcha/docs/verify)
     *
     * @param response (require)
     * @param remoteIp
     * @return ReCaptchaResponse
     */
    public ReCaptchaResponse verifyResponse(String response,String remoteIp);

}
