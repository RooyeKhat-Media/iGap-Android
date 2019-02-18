package net.iGap.module.webserviceDrBot;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import com.downloader.httpclient.DefaultHttpClient;
import com.downloader.httpclient.HttpClient;

import net.iGap.R;
import net.iGap.helper.HelperCheckInternetConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


public class WebService {

    static String url = "http://botapi.igapnet:8080/rest/";

    private static String callWebService(String method, List nameValuePairs) {
        try {
            HttpParams my_httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(my_httpParams, 10000);
            HttpConnectionParams.setSoTimeout(my_httpParams, 5000);
            org.apache.http.client.HttpClient client = new org.apache.http.impl.client.DefaultHttpClient(my_httpParams);
            HttpPost post = new HttpPost(url+method);
            String apikey = ApiControl.encryption(ApiControl.apiKey);
            nameValuePairs.add(new BasicNameValuePair("key", apikey));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            HttpResponse response = client.execute(post);
            int resCode = response.getStatusLine().getStatusCode();

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            String ret = null;

            if (resCode == 200) {
                ret = "";
                while ((line = rd.readLine()) != null) {
                    ret = ret+line;
                }
                return ret;
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    public static interface AsyncTaskCompleteListener<String> {
        public void onTaskComplete(String result);
    }

    public static class AsyncCaller extends AsyncTask<String, Void, String> {

        private AsyncTaskCompleteListener<String> callback;
        List localNameValuePairs;
        Context context;

        public AsyncCaller (Context appContext, List nameValuePairs, AsyncTaskCompleteListener<String> cb, boolean showProgress) {
            super();
            this.context = appContext;
            this.localNameValuePairs = nameValuePairs;
            this.callback = cb;

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String ... url) {

            if (HelperCheckInternetConnection.hasNetwork()) {
                if (url.length >= 1) {
                    return callWebService(url[0], localNameValuePairs);
                } else {
                    return null;
                }
            } else {
                String ret = "{\"result\":2, \"detail\":\"";
                ret+= context.getString(R.string.check_internet_connection);
                ret += "\"}";
                return ret;
            }
        }

        @Override
        protected void onPostExecute(String webRet) {

            callback.onTaskComplete(webRet);
        }
    }



}
