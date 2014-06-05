package hu.sch.util;

import hu.sch.util.config.Configuration;
import hu.sch.util.config.Environment;
import hu.sch.util.config.ImageUploadConfig;

public class ConfigurationStub implements Configuration {

    private String devMail = "test@example.com";
    private Environment env = Environment.TEST;
    private ImageUploadConfig imgConf = new ImageUploadConfig("/", 400, 150);
    private String domain = "pek.sch.bme.hu";
    private String apiSecret = "secret";
    private boolean checkSig = false;

    @Override
    public String getDevEmail() {
        return devMail;
    }

    @Override
    public Environment getEnvironment() {
        return env;
    }

    @Override
    public ImageUploadConfig getImageUploadConfig() {
        return imgConf;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getInternalApiSecret() {
        return apiSecret;
    }

    @Override
    public boolean skipRequestSignature() {
        return checkSig;
    }

    public void setDevMail(String devMail) {
        this.devMail = devMail;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public void setImageUploadConfig(ImageUploadConfig imgConf) {
        this.imgConf = imgConf;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public void setCheckSig(boolean checkSig) {
        this.checkSig = checkSig;
    }
}
