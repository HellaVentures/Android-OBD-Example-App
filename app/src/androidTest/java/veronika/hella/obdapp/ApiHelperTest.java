package veronika.hella.obdapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import veronika.hella.obdapp.api.ApiHelper;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

/**
 * ApiHelper class functionality test.
 */
@RunWith(AndroidJUnit4.class)
public class ApiHelperTest {
    ApiHelper api;

    @Before
    public void setup(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        api = new ApiHelper(appContext);
    }

    @Test
    public void errorCodeUrlShouldBeGenerated(){
        String dtc = "P0001";
        assertTrue("Dtc url generation.", api.getErrorCodeUrl(dtc, "WBAES26C05D", "english").equals("http://172.16.118.67:8080/api/v1/dtc/?code_id=P0001&vin=WBAES26C05D&language=EN"));
        assertTrue("Dtc url generation with missing or wrong parameters.", api.getErrorCodeUrl(null, "", "english").equals("Url generation failed - Missing parameters."));
        assertTrue("Dtc url generation with wrong language.", api.getErrorCodeUrl(dtc, "WBAES26C05D", "deutsch").equals("Your chosen language (deutsch) is not supported."));
    }

    @Test
    public void supportedErrorCodesUrlShouldBeGenerated(){
        assertTrue("Supported dtcs url generation.", api.getSupportedErrorCodesUrl().equals("http://172.16.118.67:8080/api/v1/dtc/list"));
    }

    @Test
    public void supportedMakersUrlShouldBeGenerated(){
        assertTrue("Supported makers url generation.", api.getSupportedMakersUrl().equals("http://172.16.118.67:8080/api/v1/dtc/maker"));
    }

    @Test
    public void supportedLanguagesUrlShouldBeGenerated(){
        assertTrue("Supported languages url generation.", api.getSupportedLanguagesUrl().equals("http://172.16.118.67:8080/api/v1/dtc/langs"));
    }

    @Test
    public void languageShortFormShouldBeGenerated(){
        assertTrue("Short form generation.", ApiHelper.getShortForm("german").equals("DE"));
        assertTrue("Short form generation fail.", ApiHelper.getShortForm("deutsch").equals("Your chosen language (deutsch) is not supported."));
    }

    @Test
    public void vinInformationUrlShouldBeGenerated(){
        assertTrue("Vin information url generation.", api.getVinInformationUrl("WBAES26C05D").equals("http://172.16.118.67:8080/api/v1/vin/?vin=WBAES26C05D"));
    }

    @Test
    public void urlShouldBeGenerated(){
        assertTrue("Url generation without request and parameters.", api.generateURL(null, null).equals("Url generation failed - Missing request."));
        assertTrue("Url generation without parameters.", api.generateURL("dtc/list", null).equals("http://172.16.118.67:8080/api/v1/dtc/list"));
        String[] params = {"vin=WBAES26C05D"};
        assertTrue("Url generation with request and parameters.", api.generateURL("vin", params).equals("http://172.16.118.67:8080/api/v1/vin/?vin=WBAES26C05D"));
    }
}