package com.example.animatrix.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.example.animatrix.BuildConfig;
import com.example.animatrix.R;
import com.example.animatrix.params.Statics;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CustomMethods {

    private static final String TAG = "MADARA";

    public static String getDateTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy_hhmmss");

        return formatter.format(now);
    }

    public static String extractEpisodeNumberFromId(String episodeId) {
        String[] parts = episodeId.split("-");
        return parts[parts.length - 1];
    }

    public static void mergeTwoJsonArray(JSONArray oldArray, JSONArray newArray) throws JSONException {

        for (int i = 0; i < newArray.length(); i++) {
            oldArray.put(newArray.getJSONObject(i));
        }
    }

    //--------------------------------------------------------------------------------------------------

    public static String capitalize(String sentence) {
        if (sentence == null){
            return "";
        } else {
            // Split the sentence into words
            String[] words = sentence.split(" ");

            // Capitalize the first letter of each word
            StringBuilder capitalizedSentence = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    char firstLetter = Character.toUpperCase(word.charAt(0));
                    String restOfWord = word.substring(1);
                    capitalizedSentence.append(firstLetter).append(restOfWord).append(" ");
                }
            }

            // Remove the trailing space
            capitalizedSentence.deleteCharAt(capitalizedSentence.length() - 1);

            return capitalizedSentence.toString();
        }
    }

    //--------------------------------------------------------------------------------------------------
    public static boolean isInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        return nInfo != null && nInfo.isConnectedOrConnecting();
    }

    //--------------------------------------------------------------------------------------------------

    public static void warningAlert(Activity activity, String warningTitle, String warningBody, String actionButton, boolean shouldGoBack) {
        if (!activity.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(warningTitle);
            builder.setMessage(warningBody);
            builder.setIcon(R.drawable.warning);
            builder.setPositiveButton(actionButton, (dialogInterface, i) -> {
                if (shouldGoBack) {
                    activity.finish();
                } else {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

//--------------------------------------------------------------------------------------------------

    public static boolean isAppInstalledOrNot(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//--------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------

    public static void chooseDownloadOptions(Activity activity, String refererUrl, String videoHLSUrl) {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, com.google.android.material.R.style.Base_Theme_Material3_Dark_BottomSheetDialog);
            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.setContentView(R.layout.sample_download_option_bottomsheet_layout);

            CardView option1 = bottomSheetDialog.findViewById(R.id.download_option_1);

            if (option1 != null) {

                option1.setOnClickListener(view1 -> {

                    if (!refererUrl.equals("")) {

                        try {
                            URL url = new URL(refererUrl);

                            String protocol = url.getProtocol();
                            String host = url.getHost();
                            String newPath = "/download";
                            String query = url.getQuery();

                            String downloadUrl = protocol + "://" + host + newPath + "?" + query;

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                            activity.startActivity(intent);

                            bottomSheetDialog.dismiss();
                        } catch (Exception e) {
                            Log.e(TAG, "choosePlayOrDownload: ", e);
                            Toast.makeText(activity, "Cannot parse download url. Please choose option 2.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, "Option 1 will not work. Try option 2", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //======================================================================================
            bottomSheetDialog.show();
    }
//--------------------------------------------------------------------------------------------------

    public static String encryptStringAES(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptStringAES(String encryptedData, String key, String iv) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    public static String generateEncryptAjaxParameters(Document html, String id) throws Exception {

        String encryptedId = encryptStringAES(id, Statics.firstKey, Statics.iv);

        Element scriptTag = html.select("script[data-name=episode]").first();

        assert scriptTag != null;
        String encryptedToken = scriptTag.attr("data-value");

        String token = decryptStringAES(encryptedToken, Statics.firstKey, Statics.iv);

        return "id=" + encryptedId + "&alias=" + id + "&" + token;
    }

    public static JSONObject decryptEncryptAjaxResponse(String obj) throws Exception {

        String decrypted = decryptStringAES(obj, Statics.secondKey, Statics.iv);

        return new JSONObject(decrypted);
    }


    public static String getIdFromQuery(String query) {
        String idKey = "id=";
        int startIndex = query.indexOf(idKey);
        if (startIndex == -1) {
            return null; // or throw an exception if id is not found
        }
        startIndex += idKey.length();
        int endIndex = query.indexOf("&", startIndex);
        if (endIndex == -1) {
            endIndex = query.length(); // id is at the end of the query string
        }
        return query.substring(startIndex, endIndex);
    }

    public static void errorAlert(Activity activity, String errorTitle, String errorBody, String actionButton, boolean shouldGoBack) {
        if (!activity.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(errorTitle);
            builder.setMessage(errorBody);
            builder.setIcon(R.drawable.error_outline_24);
            builder.setPositiveButton(actionButton, (dialogInterface, i) -> {
                if (shouldGoBack) {
                    activity.finish();
                } else {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
