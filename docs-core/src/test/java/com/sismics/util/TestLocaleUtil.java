package com.sismics.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * Test of locale utilities.
 */
public class TestLocaleUtil {

    @Test
    // 验证传入 null 时，默认返回英文 Locale。
    public void getLocaleWithNullReturnsEnglish() {
        Locale locale = LocaleUtil.getLocale(null);
        Assert.assertEquals(Locale.ENGLISH, locale);
    }

    @Test
    // 验证带语言和国家的字符串可以正确拆分成 Locale。
    public void getLocaleWithLanguageAndCountry() {
        Locale locale = LocaleUtil.getLocale("fr_FR");
        Assert.assertEquals("fr", locale.getLanguage());
        Assert.assertEquals("FR", locale.getCountry());
        Assert.assertEquals("", locale.getVariant());
    }

    @Test
    // 验证语言、国家和 variant 三段式输入可以正确解析。
    public void getLocaleWithLanguageCountryAndVariant() {
        Locale locale = LocaleUtil.getLocale("en_US_POSIX");
        Assert.assertEquals("en", locale.getLanguage());
        Assert.assertEquals("US", locale.getCountry());
        Assert.assertEquals("POSIX", locale.getVariant());
    }
}
