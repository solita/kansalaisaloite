package fi.om.initiative.conf;

import com.google.common.base.Optional;

public class EnvironmentSettings {

    public final String appEnvironment;
    public final Boolean optimizeResources;
    public final String resourcesVersion;
    public final String appVersion;
    public final String commitHash;
    public final Optional<Integer> omPiwikId;
    public final boolean samlEnabled;
    public final String recaptchaSiteKey;

    public EnvironmentSettings(String appEnvironment, Boolean optimizeResources, String resourcesVersion, String appVersion, String commitHash, Optional<Integer> omPiwikId, boolean samlEnabled, String recaptchaSiteKey) {

        this.appEnvironment = appEnvironment;
        this.optimizeResources = optimizeResources;
        this.resourcesVersion = resourcesVersion;
        this.appVersion = appVersion;
        this.commitHash = commitHash;
        this.omPiwikId = omPiwikId;
        this.samlEnabled = samlEnabled;
        this.recaptchaSiteKey = recaptchaSiteKey;
    }

}
