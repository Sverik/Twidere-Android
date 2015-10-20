package org.mariotaku.twidere.util.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HomeTimelineAlarmReceiver extends BroadcastReceiver {
	public static final int REQUEST_CODE = 12345;
	public static final String ACTION = "org.mariotaku.twidere.alarm";

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, HomeTimelineService.class);
		context.startService(i);
	}
}
