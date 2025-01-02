package com.example.animatrix.helper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.example.animatrix.R;
import com.example.animatrix.callbacks.AnimeDetailsCallback;
import com.example.animatrix.callbacks.AnimeScrapCallback;
import com.example.animatrix.callbacks.RecentScrapCallback;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.annotation.Documented;
import java.net.URLEncoder;
import java.util.Objects;

public class Scrapper {
    private final Activity activity;
    private AnimeDetailsCallback animeDetailsCallback;
    private AnimeScrapCallback animeScrapCallback;
    private RecentScrapCallback recentScrapCallback;
    private final SharedPreferences sharedPreferences;
    private final boolean isProxyEnabled;
    private final String proxyBrowserLink;

    public Scrapper(Activity activity, RecentScrapCallback recentScraperCallback) {
        this.activity = activity;
        this.recentScrapCallback = recentScraperCallback;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        isProxyEnabled = sharedPreferences.getBoolean("use_proxy", false);
        proxyBrowserLink = activity.getString(R.string.proxy_browser_link);
    }
    public Scrapper(Activity activity,AnimeDetailsCallback animeDetailsCallback){
        this.activity=activity;
        this.animeDetailsCallback=animeDetailsCallback;
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(activity);
        isProxyEnabled=sharedPreferences.getBoolean("use_proxy",false);
        proxyBrowserLink=activity.getString(R.string.proxy_browser_link);
    }
    public Scrapper(Activity activity,AnimeScrapCallback animeScrapCallback){
        this.activity=activity;
        this.animeScrapCallback =animeScrapCallback;
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(activity);
        isProxyEnabled=sharedPreferences.getBoolean("use_proxy",false);
        proxyBrowserLink=activity.getString(R.string.proxy_browser_link);
    }

    public void scrapeRecent(int page,int type){
        new Thread(() -> {
            try{
                JSONArray allAnime = new JSONArray();
                String url = activity.getString(R.string.gogoload_recent)+"?page="+page+"&type="+type;
                Document document= Jsoup.connect(url)
                        .userAgent(activity.getString(R.string.user_agent))
                        .header("Accept-Language","en-GB,en;q=0.5")
                        .get();

                Element episodes = document.select(".items").first();

                assert episodes!=null;
                Elements allListTags = episodes.select("li");

                for(Element allListTag : allListTags){
                    JSONObject object = new JSONObject();
                    String animeId;
                    String episodeId;
                    String animeTitle;
                    String episodeNum;
                    String subOrDub="";
                    String animeImg;

                    episodeId= Objects.requireNonNull(allListTag.select("a").first()).attr("href").trim();
                    episodeId=episodeId.substring(1);

                    animeTitle=Objects.requireNonNull(allListTag.select("a").first()).attr("title").trim();
                    animeImg = Objects.requireNonNull(allListTag.select("img").first()).attr("src").trim();

                    if (document.getElementsByClass("ic-DUB").size() > 0){
                        subOrDub="DUB";
                    }
                    if (document.getElementsByClass("ic-SUB").size()> 0){
                        subOrDub="SUB";
                    }

                    episodeNum = CustomMethods.extractEpisodeNumberFromId(episodeId).trim();
                    animeId = episodeId.replace("-episode-"+ episodeNum , "").trim();

                    object.put("animeId",animeId);
                    object.put("episodeId", episodeId);
                    object.put("animeTitle",animeTitle);
                    object.put("episodeNum",episodeNum);
                    object.put("subOrDub",subOrDub);
                    object.put("animeImg",animeImg);

                    allAnime.put(object);
                    new Handler(Looper.getMainLooper()).post(() -> recentScrapCallback.onScrapeComplete(allAnime));
                }
            } catch (Exception e){
//                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> recentScrapCallback.onScrapeFailed(e.getMessage()));
            }
        }).start();
    }

    public void scrapeAnime(String url){

        new Thread(() -> {
            try{

                JSONArray allAnime = new JSONArray();

                String finalUrl = url;

                if (isProxyEnabled){
                    finalUrl = proxyBrowserLink + URLEncoder.encode(finalUrl, "UTF-8");
                }

                Document document = Jsoup.connect(finalUrl)
                        .userAgent(activity.getString(R.string.user_agent))
                        .get();

                Elements liTags = document.select("ul.items").select("li");

                for (Element liTag : liTags){

                    String animeImg = liTag.select("img").attr("src").trim();
                    String animeTitle = liTag.select("a").attr("title").trim();
                    String animeRelease = liTag.select(".released").text().toLowerCase();
                    animeRelease = animeRelease.replace("released:","").trim();
                    String animeId = Objects.requireNonNull(liTag.select("a").first()).attr("href").trim();
                    animeId = animeId.replace("/category/","");
                    String episodeId = animeId + "-episode-1";

                    JSONObject object = new JSONObject();
                    object.put("animeTitle",animeTitle);
                    object.put("animeId",animeId);
                    object.put("episodeId",episodeId);
                    object.put("animeImg",animeImg);
                    object.put("releasedDate",animeRelease);

                    allAnime.put(object);
                }

                new Handler(Looper.getMainLooper()).post(()-> animeScrapCallback.onScrapeComplete(allAnime));
            } catch(Exception e){
                new Handler(Looper.getMainLooper()).post(()-> animeScrapCallback.onScrapeFailed(e.getMessage()));
            }
        }).start();
    }


    public void scrapeDetails(String animeId , String episodeId){

        new Thread(()->{
            String episodeId2;
            Looper.prepare();
            if(episodeId== null || episodeId.isEmpty()){
                episodeId2 = animeId + "-episode-1";
            }
            else{
                episodeId2 = episodeId;
            }

            Document document;
            Connection.Response response;
            String detailUrl = activity.getString(R.string.gogoanime_url) + "/category/" + animeId;
//            Toast.makeText(activity,detailUrl, Toast.LENGTH_SHORT).show();
            try{
                response = Jsoup.connect(detailUrl)
                        .userAgent(activity.getString(R.string.user_agent))
                        .header("Accept-Language","en-GB,en;q=0.5")
                        .execute();

//                Toast.makeText(activity,response.toString(), Toast.LENGTH_SHORT).show();
                Log.e("response",response.toString());
            } catch(Exception e) {

                String episodeUrl = activity.getString(R.string.gogoanime_url) + "/" + episodeId2;
                Log.e("url", episodeUrl.toString());
                try {
                    Document episodePageDoc = Jsoup.connect(episodeUrl)
                            .userAgent(activity.getString(R.string.user_agent))
                            .header("Accept-Language", "en-GB,en;q=0.5")
                            .get();

                    Element animeIdContainerDiv = episodePageDoc.getElementsByClass("anime-info").first();

                    assert animeIdContainerDiv != null;
                    Element aTag = animeIdContainerDiv.getElementsByTag("a").first();

                    assert aTag != null;
                    detailUrl = activity.getString(R.string.gogoanime_url) + aTag.attr("href");
                    response = Jsoup.connect(detailUrl)
                            .userAgent(activity.getString(R.string.user_agent))
                            .header("Accept-Language", "en-GB,en;q=0.5")
                            .execute();

                } catch (Exception ex) {
                    response = null;
                    Log.e("Madara", "scrapeDetails", ex);
                }
            }

            try{
                if (response == null){
                    new Handler(Looper.getMainLooper()).post(()-> CustomMethods.errorAlert(activity,"error(Try using vpn)","please try again...","Ok",true));
                }
                else {
                    JSONObject animeInfoObj = new JSONObject();

                    document = response.parse();


                    Element episodePage = document.getElementById("episode_page");


                    assert episodePage!=null;
                    Element aTag = episodePage.getElementsByClass("active").first();

                    assert aTag!=null;
                    String lastEpisode = aTag.attr("ep_end");
                    String movieId = Objects.requireNonNull(document.select(".anime_info_episodes_next #movie_id").first()).attr("value");
//                    Toast.makeText(activity,movieId.toString(), Toast.LENGTH_SHORT).show();

                    String allEpisodesUrl = activity.getString(R.string.gogoload_list_episodes)+"?id="+movieId+"&alias="+animeId+"&ep_start=0&default_ep=0&ep_end="+lastEpisode;
                    Document allEpisodeHtml = Jsoup.connect(allEpisodesUrl)
                            .userAgent(activity.getString(R.string.user_agent))
                            .get();


                    Elements liTags = allEpisodeHtml.getElementsByTag("li");
                    JSONArray episodeArray = new JSONArray();

                    for (Element liTag : liTags){
                        String epId= Objects.requireNonNull(liTag.select("a").first()).attr("href").replace("/","").trim();
                        String episodeNum = CustomMethods.extractEpisodeNumberFromId(epId);

                        JSONObject newObj = new JSONObject();
                        newObj.put("episodeId",epId);
                        newObj.put("episodeNum",episodeNum);

                        episodeArray.put(newObj);
                    }

                    Element details = document.select(".anime_info_body_bg").first();

                    assert details!=null;
                    Document innerHtml=Jsoup.parse(details.html());

                    String animeImg = Objects.requireNonNull(innerHtml.select("img").first()).attr("src").trim();
                    String animeTitle = Objects.requireNonNull(innerHtml.select("h1").first()).text();
                    String type = "" ;
                    String releaseDate = "";
                    String status = "";
                    String synopsis = innerHtml.select("div.description").text();

                    JSONArray genresArray = new JSONArray();
                    Elements allPTags = innerHtml.select(".type");
                    for (int i = 0; i < allPTags.size(); i++) {
                        Document pTagDoc = Jsoup.parse(allPTags.get(i).html());

                        String spanTagValue = Objects.requireNonNull(pTagDoc.select("span").first()).text();

                        if (spanTagValue.toLowerCase().contains("type")) {
                            type = pTagDoc.text().replace(spanTagValue, "").trim();
                        }

                        if (spanTagValue.toLowerCase().contains("genre")) {

                            Elements genres = pTagDoc.getElementsByTag("a");

                            for (int k = 0; k < genres.size(); k++) {
                                genresArray.put(genres.get(k).attr("title"));
                            }
                        }
                        if (spanTagValue.toLowerCase().contains("released")) {
                            releaseDate = pTagDoc.text().replace(spanTagValue, "").trim();
                        }
                        if (spanTagValue.toLowerCase().contains("status")) {
                            status = pTagDoc.text().replace(spanTagValue, "").trim();
                        }
                    }

                    animeInfoObj.put("animeTitle", animeTitle);
                    animeInfoObj.put("animeImg", animeImg);
                    animeInfoObj.put("type", type);
                    animeInfoObj.put("releasedDate", releaseDate);
                    animeInfoObj.put("status", status);
                    animeInfoObj.put("genres", genresArray);
                    animeInfoObj.put("synopsis", synopsis);
                    animeInfoObj.put("totalEpisodes", lastEpisode);
                    animeInfoObj.put("episodesList", episodeArray);

                    new Handler(Looper.getMainLooper()).post(()-> animeDetailsCallback.onScrapeingComplete(animeInfoObj));
                }
            } catch (Exception e){
                new Handler(Looper.getMainLooper()).post(()->animeDetailsCallback.onScrapeingFailed(e.getMessage()));
            }
        }).start();

    }

}
