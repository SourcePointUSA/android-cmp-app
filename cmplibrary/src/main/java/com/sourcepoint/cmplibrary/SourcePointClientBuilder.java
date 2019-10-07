package com.sourcepoint.cmplibrary;

import android.os.Build;

import java.net.MalformedURLException;
import java.net.URL;

class SourcePointClientBuilder {
    private static final String DEFAULT_STAGING_MMS_DOMAIN = "mms.sp-stage.net";
    private static final String DEFAULT_MMS_DOMAIN = "mms.sp-prod.net";

    private static final String DEFAULT_INTERNAL_CMP_DOMAIN = "cmp.sp-stage.net";
    private static final String DEFAULT_CMP_DOMAIN = "sourcepoint.mgr.consensu.org";

    private static final String DEFAULT_INTERNAL_IN_APP_MESSAGING_PAGE_DOMAIN = "in-app-messaging.pm.cmp.sp-stage.net/v2.0.html";
    private static final String DEFAULT_IN_APP_MESSAGING_PAGE_DOMAIN = "in-app-messaging.pm.sourcepoint.mgr.consensu.org/v3/index.html";




    private EncodedParam site, accountId ,siteId;
    private boolean staging, stagingCampaign, isShowPM;

    private String mmsDomain, cmpDomain, messageDomain;

    SourcePointClientBuilder(Integer accountId, String siteName,Integer siteId,boolean staging) throws ConsentLibException.BuildException {
        this.accountId = new EncodedParam("AccountId", accountId.toString());
        this.site = new EncodedParam("SiteName", protocol()+"://"+siteName);
        this.siteId = new EncodedParam("siteId", siteId.toString());
        this.staging = staging;
    }

    private boolean isSafeToHTTPS() {
        // SSL Handshake fails on Android < Nougat
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    private String protocol() {
       return isSafeToHTTPS() ? "https" : "http";
    }

    private void setDefaults () {
        if (messageDomain == null) {
            this.messageDomain= staging ?
                    DEFAULT_INTERNAL_IN_APP_MESSAGING_PAGE_DOMAIN :
                    DEFAULT_IN_APP_MESSAGING_PAGE_DOMAIN;
        }
        if (mmsDomain == null) {
            mmsDomain = staging ? DEFAULT_STAGING_MMS_DOMAIN : DEFAULT_MMS_DOMAIN;
        }
        if (cmpDomain == null) {
            cmpDomain = staging ? DEFAULT_INTERNAL_CMP_DOMAIN : DEFAULT_CMP_DOMAIN;
        }
    }

    SourcePointClientBuilder setMmsDomain(String mmsDomain) {
        this.mmsDomain = mmsDomain;
        return this;
    }

    SourcePointClientBuilder setCmpDomain(String cmpDomain) {
        this.cmpDomain = cmpDomain;
        return this;
    }

    SourcePointClientBuilder setMessageDomain(String messageDomain) {
        this.messageDomain = messageDomain;
        return this;
    }

    SourcePointClientBuilder setStagingCampaign(boolean stagingCampaign) {
        this.stagingCampaign = stagingCampaign;
        return this;
    }

    SourcePointClientBuilder setShowPM(boolean isShowPM){
        this.isShowPM = isShowPM;
        return this;
    }

    SourcePointClient build() throws ConsentLibException.BuildException {
        setDefaults();
        try {
            return new SourcePointClient(
                    accountId,
                    site,
                    siteId,
                    stagingCampaign,
                    isShowPM,
                    new URL(protocol(), mmsDomain, ""),
                    new URL(protocol(), cmpDomain, ""),
                    new URL(protocol(), messageDomain, "")
            );
        } catch (MalformedURLException e) {
            throw new ConsentLibException.BuildException(e.getMessage());
        }
    }
}
