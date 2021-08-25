package io.legacyfighter.cabs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Integer noOfTransitsForClaimAutomaticRefund;
    private Integer automaticRefundForVipThreshold;
    private Integer minNoOfCarsForEcoClass;

    private Integer milesExpirationInDays = 365;
    private Integer defaultMilesBonus = 10;

    public Integer getAutomaticRefundForVipThreshold() {
        return automaticRefundForVipThreshold;
    }

    public void setAutomaticRefundForVipThreshold(Integer automaticRefundForVipThreshold) {
        this.automaticRefundForVipThreshold = automaticRefundForVipThreshold;
    }

    public Integer getNoOfTransitsForClaimAutomaticRefund() {
        return noOfTransitsForClaimAutomaticRefund;
    }

    public void setNoOfTransitsForClaimAutomaticRefund(Integer noOfTransitsForClaimAutomaticRefund) {
        this.noOfTransitsForClaimAutomaticRefund = noOfTransitsForClaimAutomaticRefund;
    }

    public Integer getMinNoOfCarsForEcoClass() {
        return minNoOfCarsForEcoClass;
    }

    public void setMinNoOfCarsForEcoClass(Integer minNoOfCarsForEcoClass) {
        this.minNoOfCarsForEcoClass = minNoOfCarsForEcoClass;
    }

    public int getMilesExpirationInDays() {
        return milesExpirationInDays;
    }

    public void setMilesExpirationInDays(int milesExpirationInDays) {
        this.milesExpirationInDays = milesExpirationInDays;
    }

    public Integer getDefaultMilesBonus() {
        return defaultMilesBonus;
    }

    public void setDefaultMilesBonus(Integer defaultMilesBonus) {
        this.defaultMilesBonus = defaultMilesBonus;
    }
}

