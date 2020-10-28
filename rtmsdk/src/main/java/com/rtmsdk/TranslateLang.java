package com.rtmsdk;

//文本翻译语种
public enum TranslateLang {
    NONE(""),   //空
    AR("ar"),   //阿拉伯语
    DE("de"),   //德语
    EL("el"),   //希腊语
    EN("en"),   //英语
    ES("es"),   //西班牙语
    FI("fi"),   //芬兰语
    FIL("tl"),  //菲律宾语
    FR("fr"),   //法语
    ID("id"),   //印尼语
    IT("it"),   //意大利语
    JA("ja"),   //日语
    KO("ko"),   //韩语
    MS("ms"),   //马来语
    NB("no"),   //挪威语
    NL("nl"),   //荷兰语
    PL("pl"),   //波兰语
    PT("pt"),   //葡萄牙语
    RU("ru"),   //俄语
    SV("sv"),   //瑞典语
    ZH_CN("zh-CN"),//中文
    ZH_TW("zh-TW"),//粤语
    TH("th"),       //泰语
    TR("tr");       //土耳其语

    private String name;
    TranslateLang(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TranslateLang getByName(String name) {
        for (TranslateLang prop : values()) {
            if (prop.getName().equals(name)) {
                return prop;
            }
        }
        return TranslateLang.NONE;
    }
}
