package fr.noopy.landru.quizz;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by cyrille on 10/02/15.
 */
public class QuizApplication extends Application {

    public void onCreate() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "qJglDDzyCTc8qUB2Z5KdqvD5IUQmbUWiHJ0fNeIW", "yv6ybfancTbZLenMhweYA1egQSphlyRa2KWJUpSF");
        Log.i("Parse.com", "API initialized");
    }

}
