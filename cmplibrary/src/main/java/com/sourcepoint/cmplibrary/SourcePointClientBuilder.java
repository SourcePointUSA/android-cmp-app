package com.sourcepoint.cmplibrary;

import java.net.MalformedURLException;
import java.net.URL;

class SourcePointClientBuilder {
    private static final String DEFAULT_STAGING_MMS_URL = "https://mms.sp-stage.net";
    private static final String DEFAULT_MMS_URL = "https://mms.sp-prod.net";

    private static final String DEFAULT_INTERNAL_CMP_URL = "https://cmp.sp-stage.net";
    private static final String DEFAULT_CMP_URL = "https://sourcepoint.mgr.consensu.org";

    private static final String DEFAULT_INTERNAL_IN_APP_MESSAGING_PAGE_URL = "http://in-app-messaging.pm.cmp.sp-stage.net/";
    private static final String DEFAULT_IN_APP_MESSAGING_PAGE_URL = "http://in-app-messaging.pm.sourcepoint.mgr.consensu.org/";

    private EncodedParam site, accountId;
    private boolean staging, stagingCampaign;

    private String mmsDomain, cmpDomain, messageDomain = null;

    SourcePointClientBuilder(Integer accountId, String siteName, boolean staging) throws ConsentLibException.BuildException {
        this.accountId = new EncodedParam("AccountId", accountId.toString());
        this.site = new EncodedParam("SiteName", "https://"+siteName);
        this.staging = staging;
    }

    private void setDefaults () {
        if (messageDomain == null) {
            this.messageDomain= staging ?
                    DEFAULT_INTERNAL_IN_APP_MESSAGING_PAGE_URL :
                    DEFAULT_IN_APP_MESSAGING_PAGE_URL;
        }
        if (mmsDomain == null) {
            mmsDomain = staging ? DEFAULT_STAGING_MMS_URL : DEFAULT_MMS_URL;
        }
        if (cmpDomain == null) {
            cmpDomain = staging ? DEFAULT_INTERNAL_CMP_URL : DEFAULT_CMP_URL;
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

    SourcePointClient build() throws ConsentLibException.BuildException {
        setDefaults();
        try {
            return new SourcePointClient(
                    accountId,
                    site,
                    stagingCampaign,
                    new URL(mmsDomain),
                    new URL(cmpDomain),
                    new URL(messageDomain)
            );
        } catch (MalformedURLException e) {
            throw new ConsentLibException.BuildException(e.getMessage());
        }
    }
}
