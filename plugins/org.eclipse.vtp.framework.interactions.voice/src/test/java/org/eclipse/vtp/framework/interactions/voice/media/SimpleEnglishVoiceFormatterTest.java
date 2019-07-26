package org.eclipse.vtp.framework.interactions.voice.media;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FileContent;
import org.junit.Test;

import com.vht.openvxml.language.test.TestResourceManager;

public class SimpleEnglishVoiceFormatterTest {

	private String formatOptions = "today,tomorrow,next";
	public SimpleEnglishVoiceFormatter formatter = new SimpleEnglishVoiceFormatter();

	@Test
	public void testDayOfWeekToday() {
		Calendar calendar = Calendar.getInstance();
		TestResourceManager resourceManager = new TestResourceManager();
		List<Content> content = formatter.formatDate(calendar, "Day of Week", formatOptions, resourceManager);
		assertEquals("/DayOfWeek/today.wav", ((FileContent) content.get(0)).getPath());
		assertEquals(1, content.size());
	}

	@Test
	public void testDayOfWeekTomorrow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		TestResourceManager resourceManager = new TestResourceManager();
		List<Content> content = formatter.formatDate(calendar, "Day of Week", formatOptions, resourceManager);
		assertEquals("/DayOfWeek/tomorrow.wav", ((FileContent) content.get(0)).getPath());
		assertEquals(1, content.size());
	}

	@Test
	public void testDayOfWeek2DaysFromNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
		TestResourceManager resourceManager = new TestResourceManager();
		List<Content> content = formatter.formatDate(calendar, "Day of Week", formatOptions, resourceManager);
		assertEquals("/DayOfWeek/" + dayOfWeek.toLowerCase() + ".wav", ((FileContent) content.get(0)).getPath());
		assertEquals(1, content.size());
	}

	@Test
	public void testDayOfWeekOverOneWeekFromNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		TestResourceManager resourceManager = new TestResourceManager();
		List<Content> content = formatter.formatDate(calendar, "Day of Week", formatOptions, resourceManager);
		assertEquals("/DayOfWeek/next.wav", ((FileContent) content.get(0)).getPath());
		assertEquals("/DayOfWeek/friday.wav", ((FileContent) content.get(1)).getPath());
		assertEquals(2, content.size());
	}

	@Test
	public void testFormatDigits() {
		TestResourceManager resourceManager = new TestResourceManager();
		List<Content> content = formatter.formatDigits("0123456789", "DTMF", "", resourceManager);
		int index = 0;
		assertEquals("/DTMF/Dtmf-0.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-1.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-2.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-3.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-4.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-5.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-6.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-7.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-8.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals("/DTMF/Dtmf-9.wav", ((FileContent) content.get(index++)).getPath());
		assertEquals(index, content.size());

	}

}
