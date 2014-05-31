package hu.sch.util;

import hu.sch.util.config.Configuration;
import hu.sch.util.config.Environment;
import hu.sch.util.config.ImageUploadConfig;

public class ConfigurationStub implements Configuration {

    private String devMail = "test@example.com";
    private Environment env = Environment.TEST;
    private ImageUploadConfig imgConf = new ImageUploadConfig("/", 400, 150);
    private String korokDomain = "korok.sch.bme.hu";
    private String profileDomain = "profile.sch.bme.hu";
    private String apiSecret = "secret";

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
    public String getKorokDomain() {
        return korokDomain;
    }

    @Override
    public String getProfileDomain() {
        return profileDomain;
    }

    @Override
    public String getInternalApiSecret() {
        return apiSecret;
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

    public void setKorokDomain(String korokDomain) {
        this.korokDomain = korokDomain;
    }

    public void setProfileDomain(String profileDomain) {
        this.profileDomain = profileDomain;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}
