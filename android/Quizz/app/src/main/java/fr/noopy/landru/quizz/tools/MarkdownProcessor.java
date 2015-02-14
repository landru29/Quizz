package fr.noopy.landru.quizz.tools;

import android.util.Log;

import java.io.IOException;
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
            markdown = markdownFourJProcessor.process(data);
        } catch (IOException err) {
            Log.w("Markdown", err.getMessage());
        }
        return markdown;
    }

    private String preProcessing(String data) {
        return data;
    }

    private String postProcessing(String data) {
        return data;
    }
}
