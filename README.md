# recaptcha

Google [reCAPTCHA](https://developers.google.com/recaptcha/) v2 SDK for Java

## Usage

### XML-based configuration

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xmlns:c="http://www.springframework.org/schema/c"
    xmlns:util="http://www.springframework.org/schema/util"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/util
    http://www.springframework.org/schema/util/spring-util.xsd">

	<bean id="reCaptcha"
        class="com.gsscloud.recaptcha.ReCaptchaImpl"
        c:siteKey="${captcha.siteKey}"
        c:secret="${captcha.secret}"/>

</beans>
```

### Java-based configuration

```java
import com.gsscloud.recaptcha.ReCaptcha;
import com.gsscloud.recaptcha.ReCaptchaImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

@Configuration
class CaptchaConfiguration {

        @Autowired
        private Environment env;

        @Bean
        ReCaptcha reCaptcha(){
            return new ReCaptchaImpl(env.getProperty("captcha.siteKey",env.getProperty("captcha.secret"));
        }
    }
```

### Thymeleaf Template sample

```html
<div th:remove="tag" th:utext="${@reCaptcha.createScriptResource({hl:#locale.toLanguageTag()})}"></div>
<div th:remove="tag" th:utext="${@reCaptcha.createReCaptchaTag({:})}"></div>
```

### Verify reCAPTCHA response sample

```java
ReCaptchaResponse reCaptchaResponse = reCaptcha.verifyResponse(request.getParameter("g-recaptcha-response"), request.getRemoteAddr());
Assert.assertTrue(reCaptchaResponse.isSuccess());
```
