package com.rtmsdk;

//语音识别语种
public enum TranscribeLang {
    NONE(""),   //空
    AR_EG("ar-EG"),
    AR_SA("ar-SA"),
    AR_AE("ar-AE"),
    AR_KW("ar-KW"),
    AR_QA("ar-QA"),
    DE_DE("de-DE"),
    EL_GR("el-GR"),
    EN_AU("en-AU"),
    EN_CA("en-CA"),
    EN_GB("en-GB"),
    EN_IN("en-IN"),
    EN_NZ("en-NZ"),
    EN_US("en-US"),
    ES_ES("es-ES"),
    ES_MX("es-MX"),
    ES_AR("es-AR"),
    FI_FI("fi-FI"),
    FIL_P("fil-PH"),
    FR_CA("fr-CA"),
    FR_FR("fr-FR"),
    ID_ID("id-ID"),
    IT_IT("it-IT"),
    JA_JP("ja-JP"),
    KO_KR("ko-KR"),
    MS_MY("ms-MY"),
    NB_NO("nb-NO"),
    NL_NL("nl-NL"),
    PL_PL("pl-PL"),
    PT_BR("pt-BR"),
    PT_PT("pt-PT"),
    RU_RU("ru-RU"),
    SV_SE("sv-SE"),
    ZH_CN("zh-CN"),
    ZH_HK("zh-HK"),
    ZH_TW("zh-TW"),
    TH_TH("th-TH"),
    TR_TR("tr-TR");

    private String name;
    TranscribeLang(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
