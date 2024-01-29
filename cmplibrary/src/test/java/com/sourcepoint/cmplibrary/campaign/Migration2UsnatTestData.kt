package com.sourcepoint.cmplibrary.campaign

val ccpaRejectedSome752 = """
    {
      "sp.usnat.key.sampling.result": true,
      "sp.ccpa.key.consent.status": "{\n  \"applies\": true,\n  \"consentedAll\": false,\n  \"dateCreated\": \"2023-12-07T14:40:32.515Z\",\n  \"rejectedAll\": false,\n  \"rejectedCategories\": [\n    \"5df91028cf42027ce707bb20\"\n  ],\n  \"rejectedVendors\": [\n  ],\n  \"signedLspa\": false,\n  \"uspstring\": \"1YYN\",\n  \"status\": \"rejectedSome\",\n  \"GPPData\": {\n    \"IABGPP_HDR_Version\": \"1\",\n    \"IABGPP_HDR_Sections\": \"7\",\n    \"IABGPP_HDR_GppString\": \"DBABLA~BVQVAAAAAgA\",\n    \"IABGPP_GppSID\": \"7\",\n    \"IABGPP_7_String\": \"BVQVAAAAAgA\",\n    \"IABGPP_USNAT_Version\": 1,\n    \"IABGPP_USNAT_SharingNotice\": 1,\n    \"IABGPP_USNAT_SaleOptOutNotice\": 1,\n    \"IABGPP_USNAT_SharingOptOutNotice\": 1,\n    \"IABGPP_USNAT_TargetedAdvertisingOptOutNotice\": 1,\n    \"IABGPP_USNAT_SensitiveDataProcessingOptOutNotice\": 0,\n    \"IABGPP_USNAT_SensitiveDataLimitUseNotice\": 0,\n    \"IABGPP_USNAT_SaleOptOut\": 1,\n    \"IABGPP_USNAT_SharingOptOut\": 1,\n    \"IABGPP_USNAT_TargetedAdvertisingOptOut\": 1,\n    \"IABGPP_USNAT_SensitiveDataProcessing\": \"0_0_0_0_0_0_0_0_0_0_0_0\",\n    \"IABGPP_USNAT_KnownChildSensitiveDataConsents\": \"0_0\",\n    \"IABGPP_USNAT_PersonalDataConsents\": 0,\n    \"IABGPP_USNAT_MspaCoveredTransaction\": 2,\n    \"IABGPP_USNAT_MspaOptOutOptionMode\": 0,\n    \"IABGPP_USNAT_MspaServiceProviderMode\": 0,\n    \"IABGPP_USNAT_GpcSegmentType\": 1,\n    \"IABGPP_USNAT_Gpc\": 0\n  },\n  \"uuid\": \"3c20e827-2d26-4c2a-b991-ddae57879352\",\n  \"webConsentPayload\": {\n    \"actions\": [\n    ],\n    \"cookies\": [\n      {\n        \"key\": \"ccpaUUID\",\n        \"value\": \"3c20e827-2d26-4c2a-b991-ddae57879352\",\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      },\n      {\n        \"key\": \"ccpaReject\",\n        \"value\": true,\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      },\n      {\n        \"key\": \"ccpaConsentAll\",\n        \"value\": false,\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      },\n      {\n        \"key\": \"consentStatus\",\n        \"value\": \"rejectedSome\",\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      }\n    ],\n    \"consentedAll\": false,\n    \"dateCreated\": \"2023-12-07T14:40:32.515Z\",\n    \"expirationDate\": \"2024-12-06T14:40:32.515Z\",\n    \"rejectedAll\": false,\n    \"rejectedCategories\": [\n      \"5df91028cf42027ce707bb20\"\n    ],\n    \"rejectedVendors\": [\n    ],\n    \"signedLspa\": false,\n    \"status\": \"rejectedSome\",\n    \"uspstring\": \"1YYN\",\n    \"uuid\": \"3c20e827-2d26-4c2a-b991-ddae57879352\"\n  },\n  \"expirationDate\": \"2024-12-06T14:40:32.515Z\"\n}",
      "IABGPP_USNAT_SaleOptOutNotice": 1,
      "sp.ccpa.consentUUID": "3c20e827-2d26-4c2a-b991-ddae57879352",
      "IABGPP_USNAT_SharingOptOutNotice": 1,
      "IABGPP_USNAT_GpcSegmentType": 1,
      "sp.key.messages.v7.local.state": "{\n  \"ccpa\": {\n    \"mmsCookies\": [\n      \"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\"\n    ],\n    \"propertyId\": 34049,\n    \"messageId\": 0\n  }\n}",
      "IABGPP_USNAT_Version": 1,
      "IABGPP_7_String": "BVQVAAAAAgA",
      "IABGPP_USNAT_SharingNotice": 1,
      "IABGPP_USNAT_MspaServiceProviderMode": 0,
      "sp.ccpa.key.expiration.date": "2024-12-06T14:28:19.538Z",
      "IABGPP_USNAT_SensitiveDataLimitUseNotice": 0,
      "IABGPP_USNAT_Gpc": 0,
      "sp.key.meta.data": "{\n  \"ccpa\": {\n    \"applies\": true,\n    \"sampleRate\": 1.0\n  }\n}",
      "sp.gdpr.key.sampling.result": true,
      "IABGPP_USNAT_SharingOptOut": 1,
      "IABGPP_USNAT_MspaCoveredTransaction": 2,
      "IABUSPrivacy_String": "1YYN",
      "sp.ccpa.key.sampling.result": true,
      "IABGPP_USNAT_MspaOptOutOptionMode": 0,
      "IABGPP_HDR_Sections": "7",
      "IABGPP_USNAT_TargetedAdvertisingOptOutNotice": 1,
      "IABGPP_USNAT_PersonalDataConsents": 0,
      "IABGPP_GppSID": "7",
      "IABGPP_USNAT_SensitiveDataProcessingOptOutNotice": 0,
      "IABGPP_USNAT_TargetedAdvertisingOptOut": 1,
      "IABGPP_HDR_Version": "1",
      "IABGPP_USNAT_SensitiveDataProcessing": "0_0_0_0_0_0_0_0_0_0_0_0",
      "sp.key.localDataVersion": 1,
      "IABGPP_HDR_GppString": "DBABLA~BVQVAAAAAgA",
      "sp.key.config.propertyId": 34049,
      "IABGPP_USNAT_SaleOptOut": 1,
      "sp.key.messages.v7.nonKeyedLocalState": "{\n  \"ccpa\": {\n    \"_sp_v1_data\": \"746582\",\n    \"_sp_v1_p\": \"594\"\n  }\n}",
      "IABGPP_USNAT_KnownChildSensitiveDataConsents": "0_0"
    }
""".trimIndent()

val ccpaRejectedAll752 = """
    {
      "sp.usnat.key.sampling.result": true,
      "sp.ccpa.key.consent.status": "{\n  \"applies\": true,\n  \"consentedAll\": false,\n  \"dateCreated\": \"2023-12-07T14:39:54.637Z\",\n  \"rejectedAll\": true,\n  \"rejectedCategories\": [\n    \"5df91028cf42027ce707bb1f\",\n    \"5df91028cf42027ce707bb20\"\n  ],\n  \"rejectedVendors\": [\n    \"5dd83168aba21b1129cd7f9b\",\n    \"5dcb0979fcf9312bfe54e081\",\n    \"5dcb0979fcf9312bfe54e09b\"\n  ],\n  \"signedLspa\": false,\n  \"uspstring\": \"1YYN\",\n  \"status\": \"rejectedAll\",\n  \"GPPData\": {\n    \"IABGPP_HDR_Version\": \"1\",\n    \"IABGPP_HDR_Sections\": \"7\",\n    \"IABGPP_HDR_GppString\": \"DBABLA~BVQVAAAAAgA\",\n    \"IABGPP_GppSID\": \"7\",\n    \"IABGPP_7_String\": \"BVQVAAAAAgA\",\n    \"IABGPP_USNAT_Version\": 1,\n    \"IABGPP_USNAT_SharingNotice\": 1,\n    \"IABGPP_USNAT_SaleOptOutNotice\": 1,\n    \"IABGPP_USNAT_SharingOptOutNotice\": 1,\n    \"IABGPP_USNAT_TargetedAdvertisingOptOutNotice\": 1,\n    \"IABGPP_USNAT_SensitiveDataProcessingOptOutNotice\": 0,\n    \"IABGPP_USNAT_SensitiveDataLimitUseNotice\": 0,\n    \"IABGPP_USNAT_SaleOptOut\": 1,\n    \"IABGPP_USNAT_SharingOptOut\": 1,\n    \"IABGPP_USNAT_TargetedAdvertisingOptOut\": 1,\n    \"IABGPP_USNAT_SensitiveDataProcessing\": \"0_0_0_0_0_0_0_0_0_0_0_0\",\n    \"IABGPP_USNAT_KnownChildSensitiveDataConsents\": \"0_0\",\n    \"IABGPP_USNAT_PersonalDataConsents\": 0,\n    \"IABGPP_USNAT_MspaCoveredTransaction\": 2,\n    \"IABGPP_USNAT_MspaOptOutOptionMode\": 0,\n    \"IABGPP_USNAT_MspaServiceProviderMode\": 0,\n    \"IABGPP_USNAT_GpcSegmentType\": 1,\n    \"IABGPP_USNAT_Gpc\": 0\n  },\n  \"uuid\": \"63ddda00-937f-45ad-bfdb-9429590006bb\",\n  \"webConsentPayload\": {\n    \"actions\": [\n    ],\n    \"cookies\": [\n      {\n        \"key\": \"ccpaUUID\",\n        \"value\": \"63ddda00-937f-45ad-bfdb-9429590006bb\",\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      },\n      {\n        \"key\": \"ccpaReject\",\n        \"value\": true,\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      },\n      {\n        \"key\": \"ccpaConsentAll\",\n        \"value\": false,\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      },\n      {\n        \"key\": \"consentStatus\",\n        \"value\": \"rejectedAll\",\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      }\n    ],\n    \"consentedAll\": false,\n    \"dateCreated\": \"2023-12-07T14:39:54.637Z\",\n    \"expirationDate\": \"2024-12-06T14:39:54.637Z\",\n    \"rejectedAll\": true,\n    \"rejectedCategories\": [\n      \"5df91028cf42027ce707bb1f\",\n      \"5df91028cf42027ce707bb20\"\n    ],\n    \"rejectedVendors\": [\n      \"5dd83168aba21b1129cd7f9b\",\n      \"5dcb0979fcf9312bfe54e081\",\n      \"5dcb0979fcf9312bfe54e09b\"\n    ],\n    \"signedLspa\": false,\n    \"status\": \"rejectedAll\",\n    \"uspstring\": \"1YYN\",\n    \"uuid\": \"63ddda00-937f-45ad-bfdb-9429590006bb\"\n  },\n  \"expirationDate\": \"2024-12-06T14:39:54.637Z\"\n}",
      "IABGPP_USNAT_SaleOptOutNotice": 1,
      "sp.ccpa.key.message.metadata": "{\n  \"bucket\": 594,\n  \"categoryId\": 2,\n  \"messageId\": 954132,\n  \"msgDescription\": \"\",\n  \"prtnUUID\": \"23150b2c-d999-40e9-9f9b-87bed46b2f6e\",\n  \"subCategoryId\": 5\n}",
      "sp.ccpa.consentUUID": "63ddda00-937f-45ad-bfdb-9429590006bb",
      "IABGPP_USNAT_SharingOptOutNotice": 1,
      "IABGPP_USNAT_GpcSegmentType": 1,
      "sp.key.messages.v7.local.state": "{\n  \"ccpa\": {\n    \"mmsCookies\": [\n      \"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\"\n    ],\n    \"propertyId\": 34049,\n    \"messageId\": 954132\n  }\n}",
      "IABGPP_USNAT_Version": 1,
      "IABGPP_7_String": "BVQVAAAAAgA",
      "IABGPP_USNAT_SharingNotice": 1,
      "IABGPP_USNAT_MspaServiceProviderMode": 0,
      "sp.ccpa.key.expiration.date": "2024-12-06T14:28:19.538Z",
      "IABGPP_USNAT_SensitiveDataLimitUseNotice": 0,
      "IABGPP_USNAT_Gpc": 0,
      "sp.key.meta.data": "{\n  \"ccpa\": {\n    \"applies\": true,\n    \"sampleRate\": 1.0\n  }\n}",
      "sp.gdpr.key.sampling.result": true,
      "IABGPP_USNAT_SharingOptOut": 1,
      "IABGPP_USNAT_MspaCoveredTransaction": 2,
      "IABUSPrivacy_String": "1YYN",
      "sp.ccpa.key.sampling.result": true,
      "IABGPP_USNAT_MspaOptOutOptionMode": 0,
      "IABGPP_HDR_Sections": "7",
      "IABGPP_USNAT_TargetedAdvertisingOptOutNotice": 1,
      "IABGPP_USNAT_PersonalDataConsents": 0,
      "IABGPP_GppSID": "7",
      "IABGPP_USNAT_SensitiveDataProcessingOptOutNotice": 0,
      "IABGPP_USNAT_TargetedAdvertisingOptOut": 1,
      "IABGPP_HDR_Version": "1",
      "IABGPP_USNAT_SensitiveDataProcessing": "0_0_0_0_0_0_0_0_0_0_0_0",
      "sp.key.localDataVersion": 1,
      "IABGPP_HDR_GppString": "DBABLA~BVQVAAAAAgA",
      "sp.key.config.propertyId": 34049,
      "IABGPP_USNAT_SaleOptOut": 1,
      "sp.key.messages.v7.nonKeyedLocalState": "{\n  \"ccpa\": {\n    \"_sp_v1_data\": \"746582\",\n    \"_sp_v1_p\": \"594\"\n  }\n}",
      "IABGPP_USNAT_KnownChildSensitiveDataConsents": "0_0"
    }
""".trimIndent()

val ccpaConsentedAll752 = """
    {
      "sp.usnat.key.sampling.result": true,
      "sp.ccpa.key.consent.status": "{\n  \"applies\": true,\n  \"consentedAll\": true,\n  \"dateCreated\": \"2023-12-07T14:34:17.875Z\",\n  \"rejectedAll\": false,\n  \"rejectedCategories\": [\n  ],\n  \"rejectedVendors\": [\n  ],\n  \"signedLspa\": false,\n  \"uspstring\": \"1YNN\",\n  \"status\": \"consentedAll\",\n  \"GPPData\": {\n    \"IABGPP_HDR_Version\": \"1\",\n    \"IABGPP_HDR_Sections\": \"7\",\n    \"IABGPP_HDR_GppString\": \"DBABLA~BVQqAAAAAgA\",\n    \"IABGPP_GppSID\": \"7\",\n    \"IABGPP_7_String\": \"BVQqAAAAAgA\",\n    \"IABGPP_USNAT_Version\": 1,\n    \"IABGPP_USNAT_SharingNotice\": 1,\n    \"IABGPP_USNAT_SaleOptOutNotice\": 1,\n    \"IABGPP_USNAT_SharingOptOutNotice\": 1,\n    \"IABGPP_USNAT_TargetedAdvertisingOptOutNotice\": 1,\n    \"IABGPP_USNAT_SensitiveDataProcessingOptOutNotice\": 0,\n    \"IABGPP_USNAT_SensitiveDataLimitUseNotice\": 0,\n    \"IABGPP_USNAT_SaleOptOut\": 2,\n    \"IABGPP_USNAT_SharingOptOut\": 2,\n    \"IABGPP_USNAT_TargetedAdvertisingOptOut\": 2,\n    \"IABGPP_USNAT_SensitiveDataProcessing\": \"0_0_0_0_0_0_0_0_0_0_0_0\",\n    \"IABGPP_USNAT_KnownChildSensitiveDataConsents\": \"0_0\",\n    \"IABGPP_USNAT_PersonalDataConsents\": 0,\n    \"IABGPP_USNAT_MspaCoveredTransaction\": 2,\n    \"IABGPP_USNAT_MspaOptOutOptionMode\": 0,\n    \"IABGPP_USNAT_MspaServiceProviderMode\": 0,\n    \"IABGPP_USNAT_GpcSegmentType\": 1,\n    \"IABGPP_USNAT_Gpc\": 0\n  },\n  \"uuid\": \"d660e9df-d5b7-4cc0-863d-6f67429d334b\",\n  \"webConsentPayload\": {\n    \"actions\": [\n    ],\n    \"cookies\": [\n      {\n        \"key\": \"ccpaConsentAll\",\n        \"value\": true,\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      },\n      {\n        \"key\": \"ccpaReject\",\n        \"value\": false,\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      },\n      {\n        \"key\": \"consentStatus\",\n        \"value\": \"consentedAll\",\n        \"setPath\": true,\n        \"maxAge\": 31536000\n      }\n    ],\n    \"consentedAll\": true,\n    \"dateCreated\": \"2023-12-07T14:28:25.058Z\",\n    \"expirationDate\": \"2024-12-06T14:28:25.058Z\",\n    \"rejectedAll\": false,\n    \"rejectedCategories\": [\n    ],\n    \"rejectedVendors\": [\n    ],\n    \"signedLspa\": false,\n    \"status\": \"consentedAll\"\n  },\n  \"expirationDate\": \"2024-12-06T14:34:17.875Z\"\n}",
      "IABGPP_USNAT_SaleOptOutNotice": 1,
      "sp.ccpa.consentUUID": "d660e9df-d5b7-4cc0-863d-6f67429d334b",
      "IABGPP_USNAT_SharingOptOutNotice": 1,
      "IABGPP_USNAT_GpcSegmentType": 1,
      "sp.key.messages.v7.local.state": "{\n  \"ccpa\": {\n    \"mmsCookies\": [\n      \"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\"\n    ],\n    \"propertyId\": 34049,\n    \"messageId\": 0\n  }\n}",
      "IABGPP_USNAT_Version": 1,
      "IABGPP_7_String": "BVQqAAAAAgA",
      "IABGPP_USNAT_SharingNotice": 1,
      "IABGPP_USNAT_MspaServiceProviderMode": 0,
      "sp.ccpa.key.expiration.date": "2024-12-06T14:28:19.538Z",
      "IABGPP_USNAT_SensitiveDataLimitUseNotice": 0,
      "IABGPP_USNAT_Gpc": 0,
      "sp.key.meta.data": "{\n  \"ccpa\": {\n    \"applies\": true,\n    \"sampleRate\": 1.0\n  }\n}",
      "sp.gdpr.key.sampling.result": true,
      "IABGPP_USNAT_SharingOptOut": 2,
      "IABGPP_USNAT_MspaCoveredTransaction": 2,
      "sp.ccpa.key.sampling.result": true,
      "IABUSPrivacy_String": "1YNN",
      "IABGPP_USNAT_MspaOptOutOptionMode": 0,
      "IABGPP_HDR_Sections": "7",
      "IABGPP_USNAT_TargetedAdvertisingOptOutNotice": 1,
      "IABGPP_USNAT_PersonalDataConsents": 0,
      "IABGPP_GppSID": "7",
      "IABGPP_USNAT_SensitiveDataProcessingOptOutNotice": 0,
      "IABGPP_USNAT_TargetedAdvertisingOptOut": 2,
      "IABGPP_HDR_Version": "1",
      "IABGPP_USNAT_SensitiveDataProcessing": "0_0_0_0_0_0_0_0_0_0_0_0",
      "sp.key.localDataVersion": 1,
      "IABGPP_HDR_GppString": "DBABLA~BVQqAAAAAgA",
      "sp.key.config.propertyId": 34049,
      "IABGPP_USNAT_SaleOptOut": 2,
      "sp.key.messages.v7.nonKeyedLocalState": "{\n  \"ccpa\": {\n    \"_sp_v1_data\": \"746582\",\n    \"_sp_v1_p\": \"594\"\n  }\n}",
      "IABGPP_USNAT_KnownChildSensitiveDataConsents": "0_0"
    }
""".trimIndent()