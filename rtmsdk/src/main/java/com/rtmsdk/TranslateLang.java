package com.rtmsdk;
public enum TranslateLang {
    AR("ar"),
    DE("de"),
    EL("el"),
    EN("en"),
    ES("es"),
    FI("fi"),
    FIL("fil"),
    FR("fr"),
    ID("id"),
    IT("it"),
    JA("ja"),
    KO("ko"),
    MS("ms"),
    NB("nb"),
    NL("nl"),
    PL("pl"),
    PT("pt"),
    RU("ru"),
    SV("sv"),
    ZH_CN("zh-CN"),
    ZH_TW("zh-TW"),
    TH("th"),
    TR("tr");

    private String name;
    TranslateLang(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
