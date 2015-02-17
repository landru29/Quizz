package fr.noopy.landru.quizz.tools;

import android.util.Log;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.markdown4j.Markdown4jProcessor;

/**
 * Created by cyrille on 13/02/15.
 */
public class MarkdownProcessor {

    private  Markdown4jProcessor markdownFourJProcessor;

    public MarkdownProcessor() {
        this.markdownFourJProcessor = new Markdown4jProcessor();
    }

    public String toHtml(String data) {
        String markdown = "";
        try {
            markdown = markdownFourJProcessor.process(preProcessing(data));
        } catch (IOException err) {
            Log.w("Markdown", err.getMessage());
        }
        return postProcessing(markdown);
    }

    private String preProcessing(String data) {
        return data;
    }

    private String postProcessing(String data) {
        Pattern regex = Pattern.compile("href=\"(\\/)([^\"]*)\"");
        Matcher regexMatcher = regex.matcher(data);
        String resultString = regexMatcher.replaceAll("href=\"http://derby.parseapp.com/$2\"");
        return resultString;
    }
}
