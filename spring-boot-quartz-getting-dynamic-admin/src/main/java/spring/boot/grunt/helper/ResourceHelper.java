package spring.boot.grunt.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

@Component
public class ResourceHelper {

    @Autowired
    private ResourceUrlProvider resourceUrlProvider;

    public CharSequence src(String path) {
        return "http://localhost:8085" + this.resourceUrlProvider.getForLookupPath(path);
    }

}
