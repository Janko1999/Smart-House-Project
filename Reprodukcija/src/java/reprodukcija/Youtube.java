/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reprodukcija;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Youtube {
    
    private static YouTube youtube;

    public static List<SearchResult> getVideos(String title,long maxNumberVideos) {
        List<SearchResult> searchResultList = null;
        try {
            youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), new JacksonFactory(), (HttpRequest request) -> {})
                    .setApplicationName("youtube-cmdline-search-sample").build();

            
            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            String apiKey = "AIzaSyAGSfIoJrccO87n3kpiQV2wqFqL9T0095s";
            search.setKey(apiKey);
            search.setQ(title);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(maxNumberVideos);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), title,maxNumberVideos);
            }
            
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (GeneralSecurityException t) {
            t.printStackTrace();
        }
        return searchResultList;
    }
    
    public static List<SearchResult> getVideos(String title) {
        return getVideos(title,5);
    }
    
    public static void main(String[] args){
        try {
            Youtube.runBrowser("nevermind");
        } catch (Exception ex) {
            Logger.getLogger(Youtube.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void runBrowser(String naslov) throws Exception {
            List<SearchResult> searchResultList = Youtube.getVideos(naslov);
            ResourceId rid = searchResultList.get(0).getId();
            String url = "https://www.youtube.com/watch?v=" + rid.getVideoId();
            URL u = new URL(url);
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(u.toURI());
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */

    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query,long maxNumberVideos) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + maxNumberVideos + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }
}
