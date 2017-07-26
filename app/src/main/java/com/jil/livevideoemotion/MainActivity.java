package com.jil.livevideoemotion;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Timed;

public class MainActivity extends AppCompatActivity {

    private Subscription emoticonSubscription;
    private Subscriber subscriber;
    private final int MINIMUM_DURATION_BETWEEN_EMOTICONS = 300; // in milliseconds

    ImageView like_emoticon,love_emoticon,haha_emoticon,wow_emoticon,sad_emoticon,angry_emoticon;

    EmotionsView custom_view;

    private Animation emoticonClickAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        like_emoticon = (ImageView) findViewById(R.id.like_emoticon);
        love_emoticon = (ImageView) findViewById(R.id.love_emoticon);
        haha_emoticon = (ImageView) findViewById(R.id.haha_emoticon);
        wow_emoticon = (ImageView) findViewById(R.id.wow_emoticon);
        sad_emoticon = (ImageView) findViewById(R.id.sad_emoticon);
        angry_emoticon = (ImageView) findViewById(R.id.angry_emoticon);

        custom_view = (EmotionsView) findViewById(R.id.custom_view);

    }

    @Override
    public void onStart() {
        super.onStart();
        //Create an instance of FlowableOnSubscribe which will convert click events to streams
        FlowableOnSubscribe flowableOnSubscribe = new FlowableOnSubscribe() {
            @Override
            public void subscribe(final FlowableEmitter emitter) throws Exception {
                convertClickEventToStream(emitter);
            }
        };
        //Give the backpressure strategy as BUFFER, so that the click items do not drop.
        Flowable emoticonsFlowable = Flowable.create(flowableOnSubscribe, BackpressureStrategy.BUFFER);
        //Convert the stream to a timed stream, as we require the timestamp of each event
        Flowable<Timed> emoticonsTimedFlowable = emoticonsFlowable.timestamp();
        subscriber = getSubscriber();
        //Subscribe
         emoticonsTimedFlowable.subscribeWith(subscriber);
    }

    private Subscriber getSubscriber() {
        return new Subscriber<Timed<Emotions>>() {
            @Override
            public void onSubscribe(Subscription s) {
                emoticonSubscription = s;
                emoticonSubscription.request(1);

                custom_view.initView(MainActivity.this);
            }

            @Override
            public void onNext(final Timed<Emotions> timed) {

                custom_view.addView(timed.value());

                long currentTimeStamp = System.currentTimeMillis();
                long diffInMillis = currentTimeStamp - ((Timed) timed).time();
                if (diffInMillis > MINIMUM_DURATION_BETWEEN_EMOTICONS) {
                    emoticonSubscription.request(1);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            emoticonSubscription.request(1);
                        }
                    }, MINIMUM_DURATION_BETWEEN_EMOTICONS - diffInMillis);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                if (emoticonSubscription != null) {
                    emoticonSubscription.cancel();
                }
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (emoticonSubscription != null) {
            emoticonSubscription.cancel();
        }
    }


    private void convertClickEventToStream(final FlowableEmitter emitter) {
        like_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(like_emoticon, emitter, Emotions.LIKE);
            }
        });

        love_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(love_emoticon, emitter, Emotions.LOVE);
            }
        });

        haha_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(haha_emoticon, emitter, Emotions.HAHA);
            }
        });

        wow_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(wow_emoticon, emitter, Emotions.WOW);
            }
        });

        sad_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(sad_emoticon, emitter, Emotions.SAD);
            }
        });

        angry_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(angry_emoticon, emitter, Emotions.ANGRY);
            }
        });
    }

    private void doOnClick(View view, FlowableEmitter emitter, Emotions emoticons) {
        emoticonClickAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.emotion_onclickanimation);
        view.startAnimation(emoticonClickAnimation);
        emitter.onNext(emoticons);
    }
}
