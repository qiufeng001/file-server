package file.server.service.utils;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesUtils {
    private static final Log LOG = LogFactory.getLog(PropertiesUtils.class);
    private static Properties envProp = new Properties();

    public PropertiesUtils() {
    }

    public static Properties getEnvProp() {
        return envProp;
    }

    static {
        InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");

        try {
            envProp.load(inputStream);
        } catch (IOException var2) {
            LOG.error(var2);
        }

    }
}
