package veronika.hella.obdapp.api;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import veronika.hella.obdapp.R;
import veronika.hella.obdapp.ui.ResponseActivity;


/**
 * Created by Veronika on 27.07.2016.
 */

public class ApiHelper extends Application {
    private static final String TAG = ApiHelper.class.getSimpleName();
    private static final String mainURL = "https://api.eu.apiconnect.ibmcloud.com/hella-ventures-car-diagnostic-api/api";

    /**
     * TODO: Input your authentication parameters here.
     */
    private final String CLIENT_ID = "xxx";
    private final String CLIENT_SECRET = "xxx";

    private Context c;

    public ApiHelper(Context c){
        this.c = c.getApplicationContext();
    }

    /**
     * Generates an url to HELLA's Car Diagnostic API based on the given parameters.
     *
     * @param request The request to be send to the API.
     * @param param   The added parameters.
     * @return The generated url or an error if something is missing.
     */
    public String generateURL(String request, String[] param) {
        int version = 1;
        if (request != null) {
            if (!request.equals("")) {
                String url = mainURL + "/v" + version + "/" + request;
                if (param != null) {
                    if (param.length > 0) {
                        url += "?";
                        for (int i = 0; i < param.length; i++) {
                            url += param[i];
                            if (i != param.length - 1)
                                url += "&";
                        }
                    }
                }
                return url;
            }
        }
        return "Url generation failed - Missing request.";
    }

    /**
     * Translates a number of given error codes.
     *
     * @param dtcs      The DTCs to be translated.
     * @param vin       The VIN of the specific car.
     * @param language  The translation language.
     */
    public void getErrorCodeTranslation(String[] dtcs, String vin, String language) {
        RequestQueue requestQueue = Volley.newRequestQueue(c);

        for (String dtc: dtcs) {
            String url = null;
            if (dtc != null & vin != null & language != null) {
                if (!dtc.equals("") & !vin.equals("") & !language.equals(""))
                    url = getErrorCodeUrl(dtc, vin, language);
            }
            if (url != null)
                requestQueue.add(GETTranslation(url, dtc, language));
        }
    }

    /**
     * Generates the url for the error code translation
     *
     * @param dtc       The DTC to be translated.
     * @param vin       The VIN of the specific car.
     * @param language  The translation language.
     * @return error code translation url or null
     */
    public String getErrorCodeUrl(String dtc, String vin, String language){
        String request = "dtc";
        String languageSF = getShortForm(language);
        if (languageSF!= null) {
            String[] params = {"code_id=" + dtc, "vin=" + vin, "language=" + languageSF, "client_id=" + CLIENT_ID , "client_secret=" + CLIENT_SECRET};
            return generateURL(request, params);
        }
        return null;
    }

    /**
     * Generates the url for supported makers.
     *
     * @return The url to get the supported makers from.
     */
    public String getSupportedMakersUrl() {
        String request = "dtc/maker";
        String[] params = {"client_id=" + CLIENT_ID , "client_secret=" + CLIENT_SECRET};
        return generateURL(request, params);
    }

    /**
     * Generates the url for supported languages.
     *
     * @return The url to get the supported languages from.
     */
    public String getSupportedLanguagesUrl() {
        String request = "dtc/langs";
        String[] params = {"client_id=" + CLIENT_ID , "client_secret=" + CLIENT_SECRET};
        return generateURL(request, params);
    }

    /**
     * Transforms a language to its short form.
     *
     * @param language  The language to be transformed.
     * @return          The language's short form.
     */
    public static String getShortForm(String language) {
        if (language.toLowerCase().equals("german")|language.toLowerCase().equals("deutsch"))
            return "DE";
        else if (language.toLowerCase().equals("english")|language.toLowerCase().equals("englisch"))
            return "EN";
        else {
            Log.e(TAG, "Your chosen language (" + language + ") is not supported.");
        }
        return null;
    }

    /**
     * Generates the url for some vin's information.
     *
     * @param vin   The vin to get information to.
     * @return      The url to get the information from.
     */
    public String getVinInformationUrl(String vin) {
        String request = "vin";
        String[] params = {"vin=" + vin.toUpperCase(), "client_id=" + CLIENT_ID , "client_secret=" + CLIENT_SECRET};
        return generateURL(request, params);
    }

    /**
     * Generates a readable response consisting of the dtc's information (System & Fault) from a json object.
     * @param response  DTC's translation received from the API.
     * @return          The formated DTC translation.
     */
    private String formatDtcTranslation(JSONObject response, String lang) {
        String result = null;
        try {
            JSONObject dtc_data = response.getJSONObject("dtc_data");
            String system = "System: " + dtc_data.getString("system");
            String fault;
            if(lang.equals(c.getString(R.string.german).toLowerCase())) {
                fault = "Fehler: " + dtc_data.getString("fault");
            }else{
                fault = "Fault: " + dtc_data.getString("fault");
            }
            result = system + "\n" + fault;
        } catch (JSONException jsonE) {
            Log.e(TAG, jsonE.toString());
        }
        return result;
    }

    /**
     *  Creation of the HTTP GETTranslation request.
     * @param url The url the send the GETTranslation request to.
     * @param errorCode The error code to be translated.
     * @return The request object
     */
    private JsonObjectRequest GETTranslation(String url, final String errorCode, final String lang) {
        if (url != null) {
            Log.d(TAG, "making get request:" + url);

            return new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, response.toString());
                                    ResponseActivity.addListItem(errorCode + "\n" + formatDtcTranslation(response, lang));
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error instanceof ServerError)
                                        ResponseActivity.addListItem(errorCode + "\n" + "Server Error occured (Status Code: " + error.networkResponse.statusCode + ")");
                                    VolleyLog.e(error.toString());
                                }
                            });
        }
        return null;
    }
}