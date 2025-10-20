@Configuration
public class EurekaConfigOverride {

    private static final Logger log = LoggerFactory.getLogger(EurekaConfigOverride.class);

    private final EurekaClientConfigBean eurekaClientConfig;

    public EurekaConfigOverride(EurekaClientConfigBean eurekaClientConfig) {
        this.eurekaClientConfig = eurekaClientConfig;
    }

    @PostConstruct
    public void overrideEurekaUri() {
        String eurekaUrl = System.getenv("EUREKA_URI");

        if (eurekaUrl != null && !eurekaUrl.isEmpty()) {
            eurekaClientConfig.getServiceUrl().put("defaultZone", eurekaUrl);
            log.info("✅ EUREKA_URI detected -> defaultZone set to: {}", eurekaUrl);
        } else {
            log.warn("⚠️ EUREKA_URI not set -> using defaultZone from application.yml");
        }
    }
}