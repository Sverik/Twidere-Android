package org.mariotaku.twidere.util.alarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static org.mariotaku.twidere.TwidereConstants.LOGTAG;

import org.mariotaku.twidere.TwidereConstants;
import org.mariotaku.twidere.api.twitter.Twitter;
import org.mariotaku.twidere.api.twitter.TwitterException;
import org.mariotaku.twidere.api.twitter.model.Paging;
import org.mariotaku.twidere.api.twitter.model.ResponseList;
import org.mariotaku.twidere.api.twitter.model.Status;
import org.mariotaku.twidere.util.AsyncTaskManager;
import org.mariotaku.twidere.util.AsyncTwitterWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeTimelineService extends IntentService {
	public static Twitter twitter;

	protected static int count = 0;
	protected static long sinceId = 0;

	public HomeTimelineService() {
		super("HomeTimelineService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(LOGTAG, "Service.onHandleIntent() #" + count + " " + sinceId  + ", " + (twitter == null));

		if (twitter != null) {
			try {
				count++;
				Paging paging = new Paging().count(1);
				if (sinceId > 0) {
					paging.sinceId(sinceId);
				}
				ResponseList<Status> statuses = twitter.getHomeTimeline(paging);
				Log.i(LOGTAG, "statuses: " + statuses.size());
				if ( ! statuses.isEmpty()) {
					int i = 0;
					for (Status status : statuses) {
						sinceId = status.getId();
						publish(status, i);
						i++;
					}
				}
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}

/*
		if (count > 2) {
			cancelAlarm();
		}
*/
	}

	public void cancelAlarm() {
		Intent intent = new Intent(getApplicationContext(), HomeTimelineAlarmReceiver.class);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, HomeTimelineAlarmReceiver.REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pIntent);
	}

	protected void publish(Status status, int seqId) {
		if (status.getRetweetedStatus() != null) {
			status = status.getRetweetedStatus();
		}
		String text = status.getText();
		String name = status.getUser().getName();
		String screenName = status.getUser().getScreenName();
		Log.i(LOGTAG, name + "(" + screenName + "): " + text);
		sendToMinimalisticText(getApplicationContext(), "twidere.tweet" + seqId + ".text", status.getText());
		sendToMinimalisticText(getApplicationContext(), "twidere.tweet" + seqId + ".screen", status.getUser().getScreenName());
		sendToMinimalisticText(getApplicationContext(), "twidere.tweet" + seqId + ".name", status.getUser().getName());
	}

	private static void sendToMinimalisticText(Context context, String varName, String varContent)
	{
		Intent sendintent = new Intent("com.twofortyfouram.locale.intent.action.FIRE_SETTING");
		// important that we only target minimalistic text widget, and not all Locale plugins
		sendintent.setClassName("de.devmil.minimaltext", "de.devmil.minimaltext.locale.LocaleFireReceiver");
		sendintent.putExtra("de.devmil.minimaltext.locale.extras.VAR_NAME", varName);
		sendintent.putExtra("de.devmil.minimaltext.locale.extras.VAR_TEXT", varContent);

		context.sendBroadcast(sendintent);
	}

}
