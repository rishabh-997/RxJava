package com.example.rxjava;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    SeekBar seekBar;
    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekbar);
        textView = findViewById(R.id.textview);

        //creating a Observable object
        Observable<Task> taskObservable = Observable
                //iterate through the list
                .fromIterable(DataSource.createList())
                //Designate a thread for the work to be done
                // That means retrieving the list of data and converting to an observable.
                .subscribeOn(Schedulers.io())
                //filters the list by creating a new thread
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        Log.i("Main Activity", "onNext : " + Thread.currentThread().getName());
                        Thread.sleep(3000);
                        return task.isComplete();
                    }
                })
                //Designating a thread to observe the results on
                .observeOn(AndroidSchedulers.mainThread());

        /**
         * Now that I have an object to observe, I can subscribe to it with an observer.
         * This observer will be notified if the data set changes.
         * The onNext() method will run on the main thread so the task objects can be set to the UI there.
         * */

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Main Activity", "SubscribedOn Called");
                disposables.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.i("Main Activity", "onNext : " + Thread.currentThread().getName());
                Log.i("Main Activity", "onNext : " + task.getDescription());
                Log.i("Main Activity", "onNext : " + task.getPriority());
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Main Activity", e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("Main Activity", "Subscribed completed");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}

/**
 * 1. Create an Observable
 * 2. Apply an operator to the Observable
 * 3. Designate what thread to do the work on and what thread to emit the results to
 * 4. Subscribe an Observer to the Observable and view the results
 */